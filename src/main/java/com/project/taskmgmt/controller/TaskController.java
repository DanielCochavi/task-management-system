package com.project.taskmgmt.controller;

import com.project.taskmgmt.dto.CreateTaskRequest;
import com.project.taskmgmt.dto.TaskResponse;
import com.project.taskmgmt.dto.UpdateTaskRequest;
import com.project.taskmgmt.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {
  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
    TaskResponse response = taskService.createTask(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public List<TaskResponse> getAllTasks() {
    return taskService.getAllTasks();
  }

  @GetMapping("/urgent")
  public List<TaskResponse> getUrgentTasks() {
    return taskService.getUrgentTasks();
  }

  @PutMapping("/{id}")
  public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
    return taskService.updateTask(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
