# PRD: VOC Race Entry — Backend Rebuild

## Problem Statement

The Victoria Outboard Club currently manages race entries manually (paper forms, spreadsheets). Club members have no way to register a boat for an event online, and the race secretary has no central system to review, approve, or reject entries. Entry status is opaque — members don't know if their entry has been approved or if changes are needed — and the secretary cannot enforce business rules (valid SBA licences, minimum drivers, boat ownership) consistently across events.

## Solution

Rebuild the Spring Boot REST API backend from scratch to implement the correct domain model established during the domain-modelling session. The API supports the full race entry lifecycle — from a member creating a draft entry through to the secretary approving and marking it paid on race day — including email notifications, SBA licence validation, per-event boat class selection, and Declaration Form PDF generation.

The backend exposes a JSON REST API consumed by the existing React frontend.

## User Stories

### Authentication

1. As a member, I want to register with my name and email so that I can access the system.
2. As a member, I want to log in with my email and password so that I can manage my boats and entries.
3. As a member, I want to receive a JWT token on login so that subsequent API calls are authenticated.
4. As an admin, I want my admin role enforced by the API so that only I can perform administrative actions.

### Person (Racing Identity)

5. As a member, I want to complete my racing profile (address, phone, SBA licence number, SBA expiry date) after registering so that I can be listed as a driver on entries.
6. As a member, I want to update my racing profile so that my details stay current.
7. As an admin, I want to create a Person without a linked User account so that I can add drivers who haven't registered in the system yet.
8. As an admin, I want to reassign a Person to a different User account so that I can correct mismatches.
9. As an admin, I want to view and edit all Persons in the system so that I can keep racing records accurate.

### BoatClass

10. As an admin, I want to create a BoatClass so that boats have a handicapping category to race under.
11. As an admin, I want to mark a BoatClass as inactive so that it no longer appears as an option for new events without deleting historical records.
12. As a member, I want to view the list of active BoatClasses so that I know which categories are available.

### Boat

13. As a member, I want to register a boat with a name and race number so that I can enter it in events.
14. As a member, I want to view all my boats so that I can manage my fleet.
15. As a member, I want to update my boat's details so that the information stays accurate.
16. As a member, I want to mark a boat as inactive so that it no longer appears in entry forms without losing its history.
17. As an admin, I want to view all boats in the system so that I can manage club records.
18. As an admin, I want to assign or change the owner of a boat so that I can correct ownership records.
19. As an admin, I want to remove the owner from a boat so that orphaned boats can be reassigned.
20. As a member, I want the system to prevent two boats from sharing the same race number so that each boat is uniquely identifiable.

### Event

21. As an admin, I want to create an event with a name, description, start date, end date (optional), closing deadline, and maximum drivers per entry so that members know what they're entering.
22. As an admin, I want to add one or more BoatClasses to an event so that I can specify which categories are racing.
23. As an admin, I want to remove a BoatClass from an event so that I can adjust the class lineup; all affected entries in that class are automatically cancelled.
24. As an admin, I want to manually close an event so that I can stop accepting new entries before the deadline if needed.
25. As an admin, I want to cancel an event so that all associated entries are automatically cancelled and members are notified.
26. As a member, I want the event to automatically close at its closing deadline so that entries cannot be submitted after the cutoff without admin intervention.
27. As a member, I want to view all open events so that I can decide which to enter.
28. As a member, I want to view the details of an event (dates, available classes, closing deadline) so that I can prepare my entry.

### Entry

29. As a boat owner, I want to create a draft entry for my boat in an open event, selecting the BoatClass to race under, so that I can start preparing my entry.
30. As a boat owner, I want to add drivers to my entry so that all people who may drive the boat are listed.
31. As a boat owner, I want to remove a driver from my entry so that I can correct the driver list before submission.
32. As a boat owner, I want to submit my entry so that the race secretary can review it.
33. As a boat owner, I want the system to validate all drivers' SBA licences against the event start date at submission so that expired licences are caught before the secretary sees the entry.
34. As a boat owner, I want the system to enforce at least one driver before submission so that entries are never submitted without a nominated driver.
35. As a boat owner, I want the system to enforce the event's maximum driver limit so that I cannot add more drivers than allowed.
36. As a boat owner, I want to see the secretary's comment when changes are requested so that I know exactly what to fix.
37. As a boat owner, I want to resubmit my entry after fixing the requested changes so that it returns to the secretary's review queue.
38. As a boat owner, I want to cancel my entry at any time — including after the closing deadline — so that I can withdraw if my boat is unavailable.
39. As a member, I want to view all my entries and their current status so that I know where each entry stands.
40. As an admin, I want to view all entries across all events so that I can manage the full entry list.
41. As an admin, I want to approve a submitted entry so that it moves to the approved state.
42. As an admin, I want to request changes on a submitted entry, including a comment explaining what to fix, so that the owner knows what to correct.
43. As an admin, I want to mark an approved entry as paid so that I can track who has settled on race day.
44. As an admin, I want to cancel any entry at any time so that I can manage exceptional situations.
45. As an admin, I want to approve a draft entry for a closed event (late entry override) so that I can accommodate members who missed the deadline with a valid reason.
46. As a member, I want the system to prevent me from entering the same boat in the same event twice so that duplicate entries are never created.
47. As a member, I want the system to prevent a boat from entering a BoatClass that is not available for the event so that only valid class selections are made.

### Notifications

48. As a boat owner, I want to receive an email when my entry is approved so that I know I'm confirmed for the event.
49. As a boat owner, I want to receive an email when changes are requested on my entry so that I know to log in and fix it.
50. As a boat owner, I want to receive an email when my entry is cancelled (including by event cancellation) so that I'm not expecting to race.

### Declaration Form

51. As a boat owner, I want to download a Declaration Form PDF for my entry so that I can bring it to race day for signing.
52. As an admin, I want the Declaration Form to list the boat, event, and all drivers so that the secretary has a complete record for the start list.

## Implementation Decisions

### Architecture

- **Mediator pattern** (see ADR-0003): business logic that spans multiple entities lives in mediator services. Individual domain services (`BoatService`, `EventService`, `EntryService`, `PersonService`, `EntryDriverService`) handle CRUD and single-entity invariants only. Mediators orchestrate cross-entity rules.
- **ID-based cross-entity references** (see ADR-0004): entities store foreign keys as plain `Long` IDs. No JPA `@ManyToOne`/`@OneToMany` annotations across domain boundaries. Cross-entity lookups are resolved explicitly by the service or mediator layer.
- **User/Person separation** (see ADR-0002): `User` (auth) and `Person` (racing identity) are separate entities. A `Person` may exist without a linked `User`.
- **Class per Entry** (see ADR-0001): `Boat` has no `boatClassId`. The class is chosen at Entry creation and stored on `Entry`.

### Schema

**User**: `id`, `firstName`, `lastName`, `email`, `passwordHash`, `role` (MEMBER / ADMIN)

**Person**: `id`, `firstName`, `lastName`, `email`, `phone`, `streetAddress`, `suburb`, `state`, `postcode`, `sbaLicenceNumber`, `sbaExpiryDate`, `userId` (nullable FK to User)

**BoatClass**: `id`, `name` (unique), `status` (ACTIVE / INACTIVE)

**Boat**: `id`, `name`, `raceNumber` (unique), `ownerId` (nullable FK to Person), `status` (ACTIVE / INACTIVE)

**Event**: `id`, `name`, `description` (nullable), `startDate`, `endDate` (nullable), `closingDeadline`, `maxDrivers` (nullable), `status` (OPEN / CLOSED / CANCELLED)

**EventBoatClass**: `id`, `eventId`, `boatClassId` — records which BoatClasses are available for an Event

**Entry**: `id`, `boatId`, `eventId`, `boatClassId`, `status` (DRAFT / SUBMITTED / CHANGES_REQUESTED / APPROVED / PAID / CANCELLED), `secretaryComment` (nullable) — unique constraint on (boatId, eventId)

**EntryDriver**: `id`, `entryId`, `personId` — unique constraint on (entryId, personId)

### Entry status state machine

```
DRAFT ──submit──► SUBMITTED ──approve──► APPROVED ──mark-paid──► PAID
                      ▲          │
                      │      request-changes
                      │          │
                      └──────────▼
                    CHANGES_REQUESTED

Any state ──cancel──► CANCELLED  (permanent; cannot be reinstated)
DRAFT (closed event) ──late-approve──► APPROVED  (admin only)
```

### API contract

**Authentication**
- `POST /api/auth/register` → 201
- `POST /api/auth/login` → 200 + JWT

**Person**
- `POST /api/persons` → 201 (admin or self)
- `GET /api/persons` → 200 (admin only)
- `GET /api/persons/{id}` → 200 / 404
- `PUT /api/persons/{id}` → 200 / 404
- `PUT /api/persons/{id}/user/{userId}` → 200 (admin: reassign Person to User)

**BoatClass**
- `POST /api/boat-classes` → 201 (admin)
- `GET /api/boat-classes` → 200 (active only for members; all for admin)
- `GET /api/boat-classes/{id}` → 200 / 404
- `PATCH /api/boat-classes/{id}/deactivate` → 200 (admin)

**Boat**
- `POST /api/boats` → 201 (member; owner defaults to caller's Person)
- `GET /api/boats` → 200 (member: own boats; admin: all boats)
- `GET /api/boats/{id}` → 200 / 404
- `PUT /api/boats/{id}` → 200 / 404 (owner or admin)
- `PATCH /api/boats/{id}/deactivate` → 200 (owner or admin)
- `PUT /api/boats/{id}/owner/{personId}` → 200 / 404 (admin)
- `DELETE /api/boats/{id}/owner` → 200 (admin)

**Event**
- `POST /api/events` → 201 (admin)
- `GET /api/events` → 200
- `GET /api/events/{id}` → 200 / 404
- `PUT /api/events/{id}` → 200 / 404 (admin)
- `POST /api/events/{id}/close` → 200 (admin)
- `POST /api/events/{id}/cancel` → 200 (admin; cascades to entries)
- `POST /api/events/{id}/boat-classes/{classId}` → 200 (admin: add class)
- `DELETE /api/events/{id}/boat-classes/{classId}` → 200 (admin: remove class; cancels affected entries)

**Entry**
- `POST /api/entries` → 201 (boat owner; boatId + eventId + boatClassId required)
- `GET /api/entries` → 200 (member: own entries; admin: all entries)
- `GET /api/entries/{id}` → 200 / 404
- `POST /api/entries/{id}/submit` → 200 / 422 (validates SBA, owner, drivers, event open)
- `POST /api/entries/{id}/cancel` → 200 (owner or admin; any state)
- `POST /api/entries/{id}/approve` → 200 (admin)
- `POST /api/entries/{id}/request-changes` → 200 (admin; body: `{ "comment": "..." }`)
- `POST /api/entries/{id}/mark-paid` → 200 (admin)
- `POST /api/entries/{id}/late-approve` → 200 (admin; bypasses closed-event check)

**EntryDriver**
- `POST /api/entries/{id}/drivers` → 201 (owner or admin; body: `{ "personId": ... }`)
- `DELETE /api/entries/{id}/drivers/{personId}` → 204 (owner or admin)

**Declaration Form**
- `GET /api/entries/{id}/declaration` → 200 PDF (owner or admin)

### Error contract

| Status | Meaning |
|--------|---------|
| 400 | Malformed request (missing required fields) |
| 401 | Not authenticated |
| 403 | Forbidden (not the boat owner, not an admin) |
| 404 | Entity not found |
| 409 | Conflict (duplicate race number, duplicate driver on entry, duplicate entry per boat+event) |
| 422 | Business rule violation (submit without owner, expired SBA licence, submit to closed event, max drivers exceeded, class not available for event) |

### Build order

By dependency: Auth/User → Person → BoatClass → Boat → Event → EventBoatClass → Entry → EntryDriver → Mediators → Declaration PDF → Email notifications

### Key mediators

- **EntrySubmissionMediator** — validates event is OPEN, boat has owner, all drivers have valid SBA licences (expiry ≥ event startDate), at least 1 driver, driver count ≤ event maxDrivers, then sets status to SUBMITTED
- **LateEntryApprovalMediator** — admin override: validates entry is DRAFT, bypasses closed-event check, sets status to APPROVED
- **EventCancellationMediator** — sets event to CANCELLED, then cancels all non-cancelled entries for that event; triggers email notifications
- **EventBoatClassRemovalMediator** — removes EventBoatClass record, then cancels all DRAFT/SUBMITTED/CHANGES_REQUESTED/APPROVED entries in that class for that event

## Testing Decisions

**What makes a good test:** tests exercise the external HTTP behaviour of the API only — request in, response out, database state after. They do not assert on internal method calls, service internals, or implementation structure. A test should remain valid if the internal implementation is completely restructured, provided the external behaviour is unchanged.

**Single testing seam:** Spring MockMvc integration tests against an in-memory H2 database. Each test class covers one controller and spins up the full Spring context. Tests issue HTTP requests via MockMvc and assert on response status, response body JSON, and (where relevant) subsequent GET calls to confirm database state.

**Naming convention:** `*ControllerIT.java` (suffix `IT` so Maven Surefire includes them in the integration test phase).

**Coverage per module:**
- `AuthControllerIT` — register, login, token returned, invalid credentials
- `PersonControllerIT` — CRUD, admin-only list, reassign-user
- `BoatClassControllerIT` — CRUD, deactivate, visibility rules
- `BoatControllerIT` — CRUD, deactivate, owner assign/remove, race number uniqueness
- `EventControllerIT` — CRUD, close, cancel (cascade), add/remove BoatClass (cascade cancel) ✅ **DONE** (13 tests; cascade entry cancellation deferred to Mediators slice)
- `EntryControllerIT` — create (owner check, 403/409/422), submit (happy path + 4x 422: closed event/no owner/no drivers/expired SBA), cancel (owner check), list (admin all vs member own), get by id — ✅ **DONE** (16 tests)
- `EntryDriverControllerIT` — add driver (owner check, 403/409), list drivers, remove driver (204/404) — ✅ **DONE** (6 tests)
- `DeclarationControllerIT` — PDF returned, correct Content-Type

**Prior art:** the previous implementation used `@TestPropertySource` to override connection pool properties for H2, and `@SpringBootTest` + `MockMvc` with `@AutoConfigureMockMvc`. Follow the same setup.

## Out of Scope

- Race results and scoring — not tracked in this system
- Payment amounts, invoicing, or online payments — the secretary marks entries paid in-person; no amounts are stored
- Race timing software integration
- Member-facing event results or standings
- PDF customisation or branding beyond the basic Declaration Form

## Further Notes

- The `prd-api-tdd.md` at the repo root documents the previous implementation's TDD kanban. It reflects the old domain model (sailNumber on Boat, boatClassId on Boat, no APPROVED/PAID/CHANGES_REQUESTED statuses, no EventBoatClass). It should be replaced by this PRD as the authoritative specification.
- The domain glossary is in `CONTEXT.md` at the repo root. All code — field names, variable names, endpoint paths — should use the vocabulary defined there (e.g. `raceNumber` not `sailNumber`, `Driver` not `crew`).
- Four ADRs in `docs/adr/` record the key architectural decisions: class-per-entry, User/Person separation, mediator pattern, and ID-based entity references. Read them before implementing cross-entity logic.
- Auto-close on `closingDeadline` requires a scheduled job (Spring `@Scheduled`) that polls for events whose `closingDeadline` has passed and transitions them from OPEN to CLOSED. ✅ **DONE** — `AutoCloseScheduler` runs every 60s via `@EnableScheduling` on `RaceEntryApplication`.
- Slice #8 Entry + Drivers + Submit ✅ **DONE** — `Entry`, `EntryDriver`, `EntryRepository`, `EntryDriverRepository`, `EntryService`, `EntryDriverService`, `EntrySubmissionMediator`, `EntryController`, `EntryDriverController` implemented. 22 new tests (16 Entry IT + 6 EntryDriver IT). Admin-only SecurityConfig matchers for approve/request-changes/mark-paid/late-approve endpoints pre-wired for Slice #9.
- Slice #9 Entry Admin Workflow + Cascade Cancellations ✅ **DONE** — `EntryService.approve/requestChanges/markPaid` (state machine transitions with 422 on invalid state), `LateEntryApprovalMediator` (DRAFT → APPROVED, bypasses closed-event check), `EventCancellationMediator` (sets event CANCELLED + cascades to cancel all non-cancelled entries), `EventBoatClassRemovalMediator` (removes EventBoatClass + cancels DRAFT/SUBMITTED/CHANGES_REQUESTED/APPROVED entries in that class). `EventController.cancel` and `removeBoatClass` now delegate to mediators. `EntryController` adds POST /approve, /request-changes, /mark-paid, /late-approve endpoints. 8 new tests (6 admin-workflow IT + 2 cascade-cancellation IT). Total: 77/77 PASSED.
