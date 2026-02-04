package com.project.taskmgmt.service;

import com.project.taskmgmt.dto.CreateTaskRequest;
import com.project.taskmgmt.dto.TaskResponse;
import com.project.taskmgmt.dto.UpdateTaskRequest;
import com.project.taskmgmt.entity.Priority;
import com.project.taskmgmt.entity.Status;
import com.project.taskmgmt.entity.TaskEntity;
import com.project.taskmgmt.exception.DuplicateTaskException;
import com.project.taskmgmt.exception.TaskNotFoundException;
import com.project.taskmgmt.events.TaskEvent;
import com.project.taskmgmt.events.TaskKafkaProducer;
import com.project.taskmgmt.mapper.TaskMapper;
import com.project.taskmgmt.repository.TaskRepository;
import jakarta.validation.ValidationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for task lifecycle and enforcing core business rules.
 */
@Service
public class TaskService {
  private static final Logger log = LoggerFactory.getLogger(TaskService.class);

  private record UpdateResult(boolean changed, boolean completedNow) {}

  private final TaskRepository repository;
  private final TaskMapper mapper;
  private final TaskUrgencyComparator urgencyComparator;
  private final TaskKafkaProducer kafkaProducer;

  public TaskService(
      TaskRepository repository,
      TaskMapper mapper,
      TaskUrgencyComparator urgencyComparator,
      TaskKafkaProducer kafkaProducer) {
    this.repository = repository;
    this.mapper = mapper;
    this.urgencyComparator = urgencyComparator;
    this.kafkaProducer = kafkaProducer;
  }

  /**
   * Creates a new task and enforces the one-task-per-title-per-day rule.
   */
  @Transactional
  public TaskResponse createTask(CreateTaskRequest request) {
    String title = normalizeTitle(request.getTitle());
    Instant now = Instant.now();
    LocalDate createdDate = LocalDate.ofInstant(now, ZoneId.systemDefault());

    ensureNoDuplicateOnCreate(title, createdDate);

    TaskEntity entity = buildNewTask(request, title, now, createdDate);

    try {
      TaskEntity saved = repository.saveAndFlush(entity);
      log.info("Task created successfully. id={}, title='{}'", saved.getId(), saved.getTitle());
      kafkaProducer.send(new TaskEvent(saved.getId(), "CREATED", now));
      return mapper.toResponse(saved);
    } catch (DataIntegrityViolationException ex) {
      log.warn("Duplicate task detected at persistence layer", ex);
      throw new DuplicateTaskException(title, createdDate);
    }
  }

  @Transactional(readOnly = true)
  public List<TaskResponse> getAllTasks() {
    List<TaskResponse> tasks = repository.findAll().stream().map(mapper::toResponse).toList();
    log.info("Fetched all tasks. count={}", tasks.size());
    return tasks;
  }

  /**
   * Returns pending tasks ordered by priority and oldest creation time first.
   */
  @Transactional(readOnly = true)
  public List<TaskResponse> getUrgentTasks() {
    List<TaskEntity> tasks = repository.findByStatus(Status.PENDING);
    tasks.sort(urgencyComparator);
    List<TaskResponse> responses = tasks.stream().map(mapper::toResponse).toList();
    log.info("Fetched urgent tasks. count={}", responses.size());
    return responses;
  }

  /**
   * Updates a task with validation and ensures completion time is set when done.
   */
  @Transactional
  public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
    TaskEntity entity = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    Instant now = Instant.now();
    UpdateResult result = applyUpdates(entity, request, now);
    if (!result.changed) {
      return mapper.toResponse(entity);
    }

    try {
      TaskEntity saved = repository.saveAndFlush(entity);
      log.info("Task updated successfully. id={}, title='{}'", saved.getId(), saved.getTitle());
      String eventType = result.completedNow ? "COMPLETED" : "UPDATED";
      kafkaProducer.send(new TaskEvent(saved.getId(), eventType, now));
      return mapper.toResponse(saved);
    } catch (DataIntegrityViolationException ex) {
      log.warn("Duplicate task detected at persistence layer", ex);
      throw new DuplicateTaskException(entity.getTitle(), entity.getCreatedDate());
    }
  }

  @Transactional
  public void deleteTask(Long id) {
    TaskEntity entity = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    repository.delete(entity);
    log.info("Task deleted successfully. id={}, title='{}'", entity.getId(), entity.getTitle());
    kafkaProducer.send(new TaskEvent(id, "DELETED", Instant.now()));
  }

  private String normalizeTitle(String title) {
    if (title == null) {
      throw new ValidationException("title must not be null");
    }
    String trimmed = title.trim();
    if (trimmed.isEmpty()) {
      throw new ValidationException("title must not be blank");
    }
    return trimmed;
  }

  private TaskEntity buildNewTask(
      CreateTaskRequest request, String title, Instant now, LocalDate createdDate) {
    TaskEntity entity = new TaskEntity();
    entity.setTitle(title);
    entity.setDescription(request.getDescription());
    entity.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
    entity.setStatus(Status.PENDING);
    entity.setCreatedAt(now);
    entity.setCreatedDate(createdDate);
    entity.setCompletedAt(null);
    return entity;
  }

  private UpdateResult applyUpdates(TaskEntity entity, UpdateTaskRequest request, Instant now) {
    boolean changed = false;
    boolean completedNow = false;

    if (request.getTitle() != null) {
      String title = normalizeTitle(request.getTitle());
      if (!title.equals(entity.getTitle())) {
        ensureNoDuplicateOnUpdate(title, entity.getCreatedDate(), entity.getId());
        entity.setTitle(title);
        changed = true;
      }
    }

    if (request.getDescription() != null) {
      entity.setDescription(request.getDescription());
      changed = true;
    }

    if (request.getPriority() != null && request.getPriority() != entity.getPriority()) {
      entity.setPriority(request.getPriority());
      changed = true;
    }

    Status newStatus = request.getStatus();
    if (newStatus != null && newStatus != entity.getStatus()) {
      if (newStatus == Status.DONE) {
        entity.setCompletedAt(now);
        completedNow = true;
      } else if (newStatus == Status.PENDING) {
        entity.setCompletedAt(null);
      }
      entity.setStatus(newStatus);
      changed = true;
    }

    return new UpdateResult(changed, completedNow);
  }

  private void ensureNoDuplicateOnCreate(String title, LocalDate createdDate) {
    if (repository.existsByTitleAndCreatedDate(title, createdDate)) {
      throw new DuplicateTaskException(title, createdDate);
    }
  }

  private void ensureNoDuplicateOnUpdate(String title, LocalDate createdDate, Long id) {
    if (repository.existsByTitleAndCreatedDateAndIdNot(title, createdDate, id)) {
      throw new DuplicateTaskException(title, createdDate);
    }
  }

}
