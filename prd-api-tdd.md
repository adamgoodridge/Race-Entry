# Kanban: Race Entry REST API (TDD)

## Reference

**Build order (by dependency):** BoatClass → Person → Boat → Event → Entry → EntryDriver

**TDD steps per entity:** Repository → Service unit test → Service → Controller integration test → Controller

**Error contract**

| Status | Meaning |
|--------|---------|
| `400` | Malformed request (missing required fields) |
| `404` | Entity not found |
| `409` | Conflict (duplicate boat+event entry, duplicate driver) |
| `422` | Business rule violation (submit without owner/drivers, entry to closed event) |

**Domain rules**

| Rule | Enforced in |
|------|------------|
| Boat owner optional at creation | `BoatService` |
| Entry cannot be submitted without boat owner | `EntryService.submit()` |
| One entry per boat per event | DB unique constraint + `EntryService` |
| Entry must have ≥ 1 driver before submission | `EntryService.submit()` |
| Driver can only appear once per entry | DB unique constraint on `entry_driver` |
| Event must be OPEN to accept entries | `EntryService` |

---

## Board

### Done

#### BoatClass

- [x] **1** — Repository interface (`BoatClassRepository`) — `BoatClass` entity + `BoatClassRepository extends JpaRepository<BoatClass, Long>`
- [x] **2** — Service unit tests (`BoatClassServiceTest`) — 6 tests covering create, findAll, findById (found + 404), delete (found + 404); all green
- [x] **3** — Service implementation (`BoatClassService`) — create, findAll, findById, delete; throws `ResourceNotFoundException` on missing id
- [x] **4** — Controller integration tests (`BoatClassControllerIT`) — 6 MockMvc tests against H2 covering POST/GET/GET-by-id/DELETE with 404 paths; `@TestPropertySource` overrides legacy c3p0 pool properties; surefire configured to include `*IT.java`
- [x] **5** — Controller (`BoatClassController`) — `POST /api/boat-classes` (201), `GET` (200), `GET /{id}` (200/404), `DELETE /{id}` (204/404)

---

### In Progress

_(none yet)_

---

### To Do

#### BoatClass

- [x] **1** — Repository interface (`BoatClassRepository`)
- [x] **2** — Service unit tests (`BoatClassServiceTest`) — `POST /api/boat-classes`, `GET /api/boat-classes`, `GET /api/boat-classes/{id}`, `DELETE /api/boat-classes/{id}`
- [x] **3** — Service implementation (`BoatClassService`)
- [x] **4** — Controller integration tests (`BoatClassControllerIT`) — `MockMvc` against H2
- [x] **5** — Controller (`BoatClassController`)

#### Person

- [x] **6** — Repository interface (`PersonRepository`)
- [ ] **7** — Service unit tests (`PersonServiceTest`) — `POST /api/persons`, `GET /api/persons`, `GET /api/persons/{id}`, `PUT /api/persons/{id}`, `DELETE /api/persons/{id}`
- [ ] **8** — Service implementation (`PersonService`)
- [ ] **9** — Controller integration tests (`PersonControllerIT`)
- [ ] **10** — Controller (`PersonController`)

#### Boat

- [ ] **11** — Repository interface (`BoatRepository`) — include `findByOwnerId`
- [ ] **12** — Service unit tests (`BoatServiceTest`) — create (owner optional), list, get, update, assign owner, remove owner, delete
- [ ] **13** — Service implementation (`BoatService`)
- [ ] **14** — Controller integration tests (`BoatControllerIT`) — covers `PUT /api/boats/{id}/owner/{personId}` and `DELETE /api/boats/{id}/owner`
- [ ] **15** — Controller (`BoatController`)

#### Event

- [ ] **16** — Repository interface (`EventRepository`)
- [ ] **17** — Service unit tests (`EventServiceTest`) — create (defaults to OPEN), list, get, update, close, delete
- [ ] **18** — Service implementation (`EventService`)
- [ ] **19** — Controller integration tests (`EventControllerIT`) — covers `PUT /api/events/{id}/close`
- [ ] **20** — Controller (`EventController`)

#### Entry

- [ ] **21** — Repository interface (`EntryRepository`) — include `findByEventId`, unique constraint boat+event
- [ ] **22** — Service unit tests (`EntryServiceTest`) — create draft, get, list by event, submit (happy path), submit without owner → 422, submit without drivers → 422, entry to closed event → 422, duplicate boat+event → 409, update status, delete draft
- [ ] **23** — Service implementation (`EntryService`) — enforce all business rules in `submit()`
- [ ] **24** — Controller integration tests (`EntryControllerIT`) — covers `POST /api/entries/{id}/submit` and `PUT /api/entries/{id}/status`
- [ ] **25** — Controller (`EntryController`)

#### EntryDriver

- [ ] **26** — Repository interface (`EntryDriverRepository`) — unique constraint entry+driver
- [ ] **27** — Service unit tests (`EntryDriverServiceTest`) — add driver, list drivers, update role, remove driver, duplicate driver → 409
- [ ] **28** — Service implementation (`EntryDriverService`)
- [ ] **29** — Controller integration tests (`EntryDriverControllerIT`)
- [ ] **30** — Controller (`EntryDriverController`)
