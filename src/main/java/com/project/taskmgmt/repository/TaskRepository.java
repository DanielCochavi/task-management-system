package com.project.taskmgmt.repository;

import com.project.taskmgmt.entity.Status;
import com.project.taskmgmt.entity.TaskEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

/**
 * Checks if another task (excluding the current one) exists
 * with the same title on the given day.
 * Used when updating a task.
*/
  boolean existsByTitleAndCreatedDate(String title, LocalDate createdDate);

/**
 * Checks if another task (excluding the current one) exists
 * with the same title on the given day.
 * Used when updating a task.
 */
  boolean existsByTitleAndCreatedDateAndIdNot(String title, LocalDate createdDate, Long id);

  List<TaskEntity> findByStatus(Status status);
}
