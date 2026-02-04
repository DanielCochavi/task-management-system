package com.project.taskmgmt.dto;

import com.project.taskmgmt.entity.Priority;
import com.project.taskmgmt.entity.Status;
import java.time.Instant;

public record TaskResponse(
    Long id,
    String title,
    String description,
    Priority priority,
    Status status,
    Instant createdAt,
    Instant completedAt) {}
