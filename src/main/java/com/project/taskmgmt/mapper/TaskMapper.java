package com.project.taskmgmt.mapper;

import com.project.taskmgmt.dto.TaskResponse;
import com.project.taskmgmt.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
  public TaskResponse toResponse(TaskEntity task) {
    return new TaskResponse(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getPriority(),
        task.getStatus(),
        task.getCreatedAt(),
        task.getCompletedAt());
  }
}
