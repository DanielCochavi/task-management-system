package com.project.taskmgmt.service;

import com.project.taskmgmt.entity.Priority;
import com.project.taskmgmt.entity.TaskEntity;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskUrgencyComparator implements Comparator<TaskEntity> {
  private static final Map<Priority, Integer> PRIORITY_ORDER =
          Map.of(
                  Priority.HIGH, 0,
                  Priority.MEDIUM, 1,
                  Priority.LOW, 2
          );

  @Override
  public int compare(TaskEntity left, TaskEntity right) {
    int leftRank = PRIORITY_ORDER.getOrDefault(left.getPriority(), Integer.MAX_VALUE);
    int rightRank = PRIORITY_ORDER.getOrDefault(right.getPriority(), Integer.MAX_VALUE);
    int priorityComparison = Integer.compare(leftRank, rightRank);
    if (priorityComparison != 0) {
      return priorityComparison;
    }
    Instant leftCreated = left.getCreatedAt();
    Instant rightCreated = right.getCreatedAt();
    if (leftCreated == null && rightCreated == null) {
      return 0;
    }
    if (leftCreated == null) {
      return 1;
    }
    if (rightCreated == null) {
      return -1;
    }
    return leftCreated.compareTo(rightCreated);
  }
}
