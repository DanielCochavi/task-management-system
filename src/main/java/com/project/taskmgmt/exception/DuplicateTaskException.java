package com.project.taskmgmt.exception;

import java.time.LocalDate;

public class DuplicateTaskException extends RuntimeException {
  public DuplicateTaskException(String title, LocalDate createdDate) {
    super("Task with title '" + title + "' already exists for date " + createdDate);
  }
}
