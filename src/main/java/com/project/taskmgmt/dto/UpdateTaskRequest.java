package com.project.taskmgmt.dto;

import com.project.taskmgmt.entity.Priority;
import com.project.taskmgmt.entity.Status;
import jakarta.validation.constraints.Pattern;

public class UpdateTaskRequest {
  @Pattern(regexp = ".*\\S.*", message = "title must not be blank")
  private String title;

  private String description;

  private Priority priority;

  private Status status;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
