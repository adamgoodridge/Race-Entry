# Product Requirements Document
## Boat Racing Event Management System

---

## 1. Overview

A backend application to manage boat racing events, including fleet management, event registration, and driver assignments. The system must support multiple events, each with independent boat entries and driver assignments.

---

## 2. Core Entities

| Entity | Key Attributes |
|---|---|
| Owner | `ownerId`, `name`, `contactEmail` |
| Boat | `boatId`, `ownerId`, `name`, `class` |
| Event | `eventId`, `name`, `date`, `location` |
| Entry | `entryId`, `boatId`, `eventId`, `status` |
| Driver | `driverId`, `name`, `licenseNumber` |
| EntryDriverMap | `mapId`, `entryId`, `driverId`, `role` |

All entities are anemic — they hold state only. No entity holds object references to another entity.

---

## 3. Business Rules

### Ownership
- An owner is a person (not an organisation).
- Each boat must have exactly one owner.
- An owner can have multiple boats.

### Event Entries
- Each entry must be associated with exactly one boat.
- Each boat may have at most one entry per event.
- An entry's status must be one of: `PENDING`, `ACTIVE`, `CANCELLED`.

### Drivers
- Each entry must have one or more drivers.
- A driver can have multiple entries across different events.
- A driver's role on an entry must be one of: `HELMSMAN`, `CREW`.

### Cascading Rules
- Deleting an owner flags all their boats as `ORPHANED` (via `RacingEventMediator`).
- Flagging a boat as `ORPHANED` cancels all its active entries.
- Cancelling an entry removes all associated `EntryDriverMap` records.

---

## 4. Architecture Requirements

### Entities
- Entities store ID references only — never object instances of related entities.
- No business logic lives inside entities.

### Service Layer
- `OwnerService` — manages owner lifecycle.
- `BoatService` — manages boat registration and status.
- `EntryService` — enforces entry business rules (one entry per boat per event, minimum one driver).
- `DriverService` — manages driver records and license validation.

### Repository Layer
- `BoatRepository.findByOwnerId(ownerId)`
- `EntryRepository.findByBoatId(boatId)`
- `EntryRepository.findByEventId(eventId)`
- `EntryDriverRepository.findDriverIdsByEntryId(entryId)`
- `EntryDriverRepository.findEntryIdsByDriverId(driverId)`

### Mediator
- `RacingEventMediator` orchestrates cross-entity workflows.
- Calls services and repositories in sequence — contains no business logic itself.
- Responsible for the owner deletion cascade described in section 3.

### DTOs
- `BoatDetailsDTO` — combines `Boat`, `Owner`, and active `Entry` for display.
- `EntryDetailsDTO` — combines `Entry`, `Boat`, and assigned `Driver` list for display.
- DTOs are assembled on read; never persisted.

---

## 5. API Endpoints (Proposed)

### Owners
- `POST /owners` — create owner
- `DELETE /owners/{id}` — delete owner (triggers cascade via Mediator)

### Boats
- `POST /boats` — register boat (requires valid `ownerId`)
- `GET /boats?ownerId={id}` — list boats by owner

### Events
- `POST /events` — create event
- `GET /events` — list all events

### Entries
- `POST /entries` — create entry (requires valid `boatId`, `eventId`)
- `GET /entries?eventId={id}` — list entries for an event
- `GET /entries?boatId={id}` — list entries for a boat
- `PATCH /entries/{id}/status` — update entry status

### Drivers
- `POST /drivers` — register driver
- `POST /entries/{id}/drivers` — assign driver to entry (creates `EntryDriverMap` record)
- `DELETE /entries/{id}/drivers/{driverId}` — remove driver from entry
- `GET /entries/{id}/drivers` — list drivers for an entry

---

## 6. Out of Scope

- Authentication and authorisation
- Payment processing for entry fees
- Race result recording
- Real-time tracking

---

## 7. Open Questions

- Should orphaned boats be hard-deleted or soft-deleted (flagged)?
flagging allows for historical data retention and potential reassignment, while hard-deletion permanently removes the record.
- Is there a maximum number of drivers per entry?
when defining an entry, an race committee will specify the required number of drivers based on the boat class and event type. This can be enforced in the `EntryService` when creating or updating an entry.
- Can an entry be transferred to a different boat within the same event?
No, once an entry is created for a specific boat and event, it cannot be transferred to another boat. This ensures the integrity of the entry and prevents complications in the event management process. If a boat needs to be replaced, the existing entry must be cancelled and a new entry must be created for the replacement boat.