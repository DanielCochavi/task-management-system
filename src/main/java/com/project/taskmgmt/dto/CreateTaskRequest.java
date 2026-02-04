package com.project.taskmgmt.dto;

import com.project.taskmgmt.entity.Priority;
import jakarta.validation.constraints.NotBlank;

public class CreateTaskRequest {
  @NotBlank(message = "title must not be blank")
  private String title;

  private String description;

  private Priority priority;

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
}
