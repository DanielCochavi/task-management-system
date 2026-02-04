package com.project.taskmgmt.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.taskmgmt.entity.Priority;
import com.project.taskmgmt.entity.TaskEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaskUrgencyComparatorTest {

  private final TaskUrgencyComparator comparator = new TaskUrgencyComparator();

  @Test
  void sortsByPriorityThenOldestCreatedAt() {
    TaskEntity highOld = task("high-old", Priority.HIGH, Instant.parse("2024-01-01T10:00:00Z"));
    TaskEntity highNew = task("high-new", Priority.HIGH, Instant.parse("2024-01-02T10:00:00Z"));
    TaskEntity medium = task("medium", Priority.MEDIUM, Instant.parse("2024-01-01T09:00:00Z"));
    TaskEntity low = task("low", Priority.LOW, Instant.parse("2024-01-01T08:00:00Z"));

    List<TaskEntity> tasks = new ArrayList<>(List.of(medium, highNew, low, highOld));
    tasks.sort(comparator);

    assertThat(tasks)
        .extracting(TaskEntity::getTitle)
        .containsExactly("high-old", "high-new", "medium", "low");
  }

  private TaskEntity task(String title, Priority priority, Instant createdAt) {
    TaskEntity entity = new TaskEntity();
    entity.setTitle(title);
    entity.setPriority(priority);
    entity.setCreatedAt(createdAt);
    return entity;
  }
}
