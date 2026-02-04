package com.project.taskmgmt.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {

  private final ApiExceptionHandler handler = new ApiExceptionHandler();

  @Test
  void validationExceptionReturnsBadRequestWithMessage() {
    ValidationException ex = new ValidationException("title must not be blank");

    ResponseEntity<String> response = handler.handleValidationException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("title must not be blank");
  }
}
