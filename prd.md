# Boat Racing Event Management — Project Board

---

## Kanban

### To Do

_Nothing currently queued._

---

### In Progress

_Nothing currently in progress._

---

### Done

| # | Task | Type | Tests After |
|---|------|------|-------------|
| 1 | Create core entities (Owner, Boat, Event, Driver, Entry, EntryDriverMap + enums) | feat | — |
| 2 | Create repositories (OwnerRepository, BoatRepository, EventRepository, DriverRepository, EntryRepository, EntryDriverRepository) | feat | — |
| 3 | Create services (OwnerService, BoatService, EntryService, DriverService) + BoatStatus enum | feat | — |
| 4 | Create RacingEventMediator with owner-deletion cascade | feat | — |
| 5 | Create DTOs (BoatDetailsDTO, EntryDetailsDTO) | feat | — |
| 6 | Create REST controllers (Owner, Boat, Event, Entry, Driver) | feat | — |
| 7 | Add GlobalExceptionHandler (404 / 409 mappings) | feat | — |
| 8 | Write integration tests for all controllers and exception mappings | test | 20 |
| 9 | Wire DTOs into GET list endpoints | feat | 20 |
| 10 | Add input validation (`@Valid` on request bodies + 400 handler) | feat | 25 |
| 11 | Refactor DriverController: move driver-list logic into EntryService | refactor | 25 |
| 12 | Handle `MissingServletRequestParameterException` and `HttpMessageNotReadableException` in GlobalExceptionHandler | feat | 27 |
| 13 | Fix missing FK existence checks in EntryService (boat, event, driver) | fix | 30 |
| 14 | Enforce unique driver license numbers (409 on duplicate) | feat | 31 |
| 15 | Prevent entry creation for ORPHANED boats (409) | feat | 32 |
| 16 | Fix PATCH `/entries/{id}/status` not removing EntryDriverMaps on cancel | fix | 33 |
| 17 | Return 404 for unknown `eventId` / `boatId` on entry list endpoints | feat | 35 |
| 18 | Fix `removeDriver` to validate entry existence before checking assignment | fix | 36 |
| 19 | Fix `GET /entries` with no params returning 404 instead of 400 | fix | 37 |
| 20 | Block driver assignment to CANCELLED entries; deduplicate `cancel()` | fix | 38 |
| 21 | Test last-driver removal (409) and duplicate driver assignment (409) | test | 40 |
| 22 | Make CANCELLED a terminal entry status — block all status transitions out of it | fix | 41 |
| 23 | Enforce unique owner contact email (409 on duplicate) | feat | 42 |
| 24 | Block driver removal from CANCELLED entries (409) | fix | 43 |
| 25 | Cover missing 404 paths for PATCH status, GET drivers, POST drivers on unknown entry | test | 46 |
| 26 | Block activating entries whose boat is ORPHANED (409) | fix | 47 |
| 27 | Cover missing 404 paths for boat list by unknown owner and removal of unassigned driver | test | 49 |

---

## Reference

### Entities

| Entity | Key Attributes |
|---|---|
| Owner | `ownerId`, `name`, `contactEmail` |
| Boat | `boatId`, `ownerId`, `name`, `class`, `status` |
| Event | `eventId`, `name`, `date`, `location` |
| Entry | `entryId`, `boatId`, `eventId`, `status` |
| Driver | `driverId`, `name`, `licenseNumber` |
| EntryDriverMap | `mapId`, `entryId`, `driverId`, `role` |

All entities are anemic — they hold state only and store ID references, never object references to other entities.

---

### Business Rules

**Ownership**
- An owner is a person (not an organisation).
- Each boat must have exactly one owner.
- An owner can have multiple boats.
- Owner contact email must be unique.

**Event Entries**
- Each entry must be associated with exactly one boat.
- Each boat may have at most one entry per event.
- An entry's status must be one of: `PENDING`, `ACTIVE`, `CANCELLED`.
- `CANCELLED` is a terminal state — no transitions out of it are permitted.
- An entry for an `ORPHANED` boat cannot be created or activated.

**Drivers**
- Each entry must have one or more drivers at all times.
- A driver can have multiple entries across different events.
- A driver's role on an entry must be one of: `HELMSMAN`, `CREW`.
- Driver license number must be unique.
- Drivers cannot be assigned to or removed from a `CANCELLED` entry.

**Cascading Rules**
- Deleting an owner flags all their boats as `ORPHANED` (via `RacingEventMediator`).
- Flagging a boat as `ORPHANED` cancels all its active entries.
- Cancelling an entry removes all associated `EntryDriverMap` records.

---

### Architecture

**Service Layer**
- `OwnerService` — owner lifecycle; enforces unique email.
- `BoatService` — boat registration, status, DTO assembly.
- `EntryService` — entry business rules and DTO assembly.
- `DriverService` — driver records; enforces unique license number.

**Repository Layer**
- `BoatRepository.findByOwnerId(ownerId)`
- `EntryRepository.findByBoatId(boatId)`
- `EntryRepository.findByEventId(eventId)`
- `EntryDriverRepository.findDriverIdsByEntryId(entryId)`
- `EntryDriverRepository.findEntryIdsByDriverId(driverId)`

**Mediator**
- `RacingEventMediator` orchestrates cross-entity workflows.
- Calls services and repositories in sequence — contains no business logic itself.

**DTOs**
- `BoatDetailsDTO` — combines `Boat`, `Owner`, and nullable active `Entry`.
- `EntryDetailsDTO` — combines `Entry`, `Boat`, and assigned `Driver` list.
- DTOs are assembled on read; never persisted.

---

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/owners` | Create owner |
| DELETE | `/owners/{id}` | Delete owner (triggers cascade via Mediator) |
| POST | `/boats` | Register boat (requires valid `ownerId`) |
| GET | `/boats?ownerId={id}` | List boats by owner → `List<BoatDetailsDTO>` |
| POST | `/events` | Create event |
| GET | `/events` | List all events |
| POST | `/entries` | Create entry (requires valid `boatId`, `eventId`) |
| GET | `/entries?eventId={id}` | List entries for an event → `List<EntryDetailsDTO>` |
| GET | `/entries?boatId={id}` | List entries for a boat → `List<EntryDetailsDTO>` |
| PATCH | `/entries/{id}/status` | Update entry status |
| POST | `/drivers` | Register driver |
| POST | `/entries/{id}/drivers` | Assign driver to entry |
| DELETE | `/entries/{id}/drivers/{driverId}` | Remove driver from entry |
| GET | `/entries/{id}/drivers` | List drivers for an entry |

---

### Out of Scope

- Authentication and authorisation
- Payment processing for entry fees
- Race result recording
- Real-time tracking

---

### Open Questions

- **Orphaned boats:** Flagged (not hard-deleted) — allows historical data retention and potential reassignment.
- **Max drivers per entry:** Not yet enforced. Race committee specifies the number based on boat class and event type; could be added to `EntryService`.
- **Entry transfer:** Not permitted. A cancelled entry must be replaced with a new one for the substitute boat.
