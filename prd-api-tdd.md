# PRD: Race Entry REST API (TDD)

## Overview

Build a REST API for the Race Entry application using test-driven development. The API exposes the existing domain model (Person, Boat, BoatClass, Event, Entry, EntryDriver) over HTTP so that frontends and integrations can manage boat racing events without tight coupling to the Spring MVC layer.

---

## Goals

- Full CRUD for all domain entities via REST endpoints
- Business rules enforced at the service layer, not the controller
- Every endpoint backed by an integration test written before the implementation
- No logic in controllers — they delegate to services

---

## Domain rules the API must enforce

| Rule | Where enforced |
|------|---------------|
| A boat's owner is optional at creation | `BoatService` |
| An entry cannot be submitted if the boat has no owner | `EntryService.submit()` |
| A boat can have at most one entry per event | DB unique constraint + `EntryService` |
| An entry must have at least one driver before submission | `EntryService.submit()` |
| A driver can only appear once per entry | DB unique constraint on `entry_driver` |
| An event must be OPEN to accept new entries | `EntryService` |

---

## Endpoints

### Person

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/persons` | Create a person |
| `GET` | `/api/persons/{id}` | Get a person |
| `GET` | `/api/persons` | List all persons |
| `PUT` | `/api/persons/{id}` | Update a person |
| `DELETE` | `/api/persons/{id}` | Delete a person |

### BoatClass

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/boat-classes` | Create a boat class |
| `GET` | `/api/boat-classes` | List all boat classes |
| `GET` | `/api/boat-classes/{id}` | Get a boat class |
| `DELETE` | `/api/boat-classes/{id}` | Delete a boat class |

### Boat

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/boats` | Create a boat (owner optional) |
| `GET` | `/api/boats/{id}` | Get a boat |
| `GET` | `/api/boats` | List all boats |
| `PUT` | `/api/boats/{id}` | Update a boat |
| `PUT` | `/api/boats/{id}/owner/{personId}` | Assign an owner to a boat |
| `DELETE` | `/api/boats/{id}/owner` | Remove owner from a boat |
| `DELETE` | `/api/boats/{id}` | Delete a boat |

### Event

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/events` | Create an event (defaults to OPEN) |
| `GET` | `/api/events/{id}` | Get an event |
| `GET` | `/api/events` | List all events |
| `PUT` | `/api/events/{id}` | Update event details |
| `PUT` | `/api/events/{id}/close` | Close an event |
| `DELETE` | `/api/events/{id}` | Delete an event |

### Entry

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/entries` | Create a draft entry (boat + event) |
| `GET` | `/api/entries/{id}` | Get an entry |
| `GET` | `/api/events/{eventId}/entries` | List all entries for an event |
| `POST` | `/api/entries/{id}/submit` | Submit entry (validates owner + drivers) |
| `PUT` | `/api/entries/{id}/status` | Update entry status (APPROVED / WITHDRAWN) |
| `DELETE` | `/api/entries/{id}` | Delete a draft entry |

### EntryDriver

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/entries/{entryId}/drivers` | Add a driver with a role to an entry |
| `GET` | `/api/entries/{entryId}/drivers` | List drivers on an entry |
| `PUT` | `/api/entries/{entryId}/drivers/{driverId}` | Update driver role |
| `DELETE` | `/api/entries/{entryId}/drivers/{driverId}` | Remove a driver from an entry |

---

## Request / Response shapes

### POST `/api/boats`
```json
{
  "name": "Black Pearl",
  "sailNumber": "AUS1234",
  "boatClassId": 2,
  "ownerId": 7
}
```
`ownerId` is optional. Returns `201 Created` with the created boat body.

### POST `/api/entries`
```json
{
  "boatId": 3,
  "eventId": 1
}
```
Returns `201 Created`. Status defaults to `DRAFT`.

### POST `/api/entries/{id}/submit`
No body required. Returns `200 OK` or `422 Unprocessable Entity` with an error message if:
- The boat has no owner
- The entry has no drivers

### POST `/api/entries/{entryId}/drivers`
```json
{
  "personId": 5,
  "role": "HELMSMAN"
}
```
Returns `201 Created`.

---

## Error responses

All errors return a consistent shape:
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Boat must have an owner before entry can be submitted"
}
```

HTTP status codes:
- `400` — malformed request (missing required fields)
- `404` — entity not found
- `409` — conflict (duplicate entry for boat+event, duplicate driver on entry)
- `422` — business rule violation (submit without owner, submit without drivers, entry to closed event)

---

## TDD approach

Each endpoint is developed in this order:

1. **Write the repository interface** (Spring Data JPA — usually no code needed beyond the interface)
2. **Write the service unit test** — mock the repository, assert the business rule
3. **Write the service implementation** — make the unit test pass
4. **Write the controller integration test** — use `@SpringBootTest` + `MockMvc`, hit the real service against an in-memory H2 database
5. **Write the controller** — make the integration test pass

### Test layers

| Layer | Test type | Tools |
|-------|-----------|-------|
| Service | Unit | JUnit 5, Mockito |
| Controller | Integration | `@SpringBootTest`, `MockMvc`, H2 |
| Repository | Integration (optional) | `@DataJpaTest`, H2 |

### Test naming convention
```
methodName_whenCondition_thenExpectedBehaviour
```
Example:
```java
submit_whenBoatHasNoOwner_thenThrowsBusinessRuleException()
createEntry_whenBoatAlreadyEnteredInEvent_thenReturns409()
```

---

## Build order (by dependency)

1. `BoatClass` — no dependencies
2. `Person` — no dependencies
3. `Boat` — depends on Person, BoatClass
4. `Event` — no dependencies
5. `Entry` — depends on Boat, Event
6. `EntryDriver` — depends on Entry, Person

Each module: Repository → Service test → Service → Controller test → Controller.

---

## Out of scope

- Authentication / authorisation (existing Spring Security config handles this separately)
- PDF generation
- Email notifications
- Frontend / Thymeleaf views
