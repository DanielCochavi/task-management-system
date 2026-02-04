package com.project.taskmgmt.events;

import java.time.Instant;

public record TaskEvent(Long taskId, String type, Instant timestamp) {}
