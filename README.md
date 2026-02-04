# TaskManagementSystem

Task management REST API with daily-unique titles and urgency sorting.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA + H2 (in-memory)
- Spring Kafka (producer only)

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
