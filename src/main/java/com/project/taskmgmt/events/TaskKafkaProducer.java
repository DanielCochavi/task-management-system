package com.project.taskmgmt.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends task lifecycle events to Kafka for downstream consumers.
 */
@Component
public class TaskKafkaProducer {
  private static final Logger log = LoggerFactory.getLogger(TaskKafkaProducer.class);

  private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

  public TaskKafkaProducer(KafkaTemplate<String, TaskEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Publishes a single task event to the tasks-events topic.
   * Kafka publishing is best-effort and must not affect task creation.
   */
  public void send(TaskEvent event) {
    try {
      kafkaTemplate
          .send("tasks-events", event.taskId().toString(), event)
          .whenComplete(
              (result, ex) -> {
                if (ex == null) {
                  log.info("Task event sent successfully. taskId={}", event.taskId());
                } else {
                  log.info("Kafka not available, event skipped. taskId={}", event.taskId());
                }
              });
    } catch (RuntimeException ex) {
      log.info("Kafka not available, event skipped. taskId={}", event.taskId());
    }
  }
}
