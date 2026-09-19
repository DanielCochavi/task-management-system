# TaskManagementSystem

Task management REST API with daily-unique titles and urgency sorting.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA + H2 (in-memory)
- Spring Kafka (producer only)


## AI-Assisted Development

This project was developed using an AI-assisted engineering workflow with Cursor and Claude models.

AI tools were used throughout the development process to support:

- Backend implementation and feature development
- Code exploration and refactoring
- Debugging and troubleshooting
- Test generation and validation
- Code review and quality improvements
- Development workflow acceleration

Cursor served as the primary AI-assisted development environment, while Claude models were used to support reasoning, implementation decisions, and code improvements.

All AI-assisted suggestions and generated changes were reviewed, validated, and tested manually before being incorporated into the project.

## Running the Application
The application runs out of the box with an in-memory database. No external infrastructure is required.

```bash
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

## Running Tests
Run the test suite with:

```bash
mvn test
```

## API Examples
Create a task (priority defaults to `MEDIUM`, status defaults to `PENDING`):

```bash
curl -i -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Write report","description":"Q1 summary","priority":"HIGH"}'
```

List all tasks:

```bash
curl -i http://localhost:8080/tasks
```

List urgent tasks (PENDING only, sorted by priority then oldest createdAt):

```bash
curl -i http://localhost:8080/tasks/urgent
```

Update a task (partial update semantics; nulls mean "no change"):

```bash
curl -i -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"status":"DONE"}'
```

Delete a task:

```bash
curl -i -X DELETE http://localhost:8080/tasks/1
```

## Notes
- `PUT /tasks/{id}` supports partial updates (nulls mean "no change").
- When status changes to `DONE`, `completedAt` is set; changing back to `PENDING` clears it.

## Kafka (Optional)
Kafka is included as an optional integration to publish task lifecycle events.
The producer demonstrates event publishing but Kafka is not required to run or test the application.
If Kafka is unavailable, the app logs the failure and continues.

## Notes
- `PUT /tasks/{id}` is intentionally partial update semantics (nulls = no change).
- When status changes to `DONE`, `completedAt` is set to now. When status changes back to `PENDING`, `completedAt` is cleared.
