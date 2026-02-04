package com.project.taskmgmt.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.taskmgmt.dto.CreateTaskRequest;
import com.project.taskmgmt.entity.Priority;
import com.project.taskmgmt.exception.DuplicateTaskException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskServiceDuplicateTitleTest {

  @Autowired private TaskService taskService;

  @Test
  void duplicateTitleSameDayThrowsConflict() {
    CreateTaskRequest request = new CreateTaskRequest();
    request.setTitle("Daily report");
    request.setDescription("First");
    request.setPriority(Priority.MEDIUM);

    taskService.createTask(request);

    assertThatThrownBy(() -> taskService.createTask(request))
        .isInstanceOf(DuplicateTaskException.class)
        .hasMessageContaining("Daily report");
  }
}
