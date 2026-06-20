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

---

### In Progress

_(none yet)_

---

### To Do

#### BoatClass

- [x] **1** — Repository interface (`BoatClassRepository`)
- [ ] **2** — Service unit tests (`BoatClassServiceTest`) — `POST /api/boat-classes`, `GET /api/boat-classes`, `GET /api/boat-classes/{id}`, `DELETE /api/boat-classes/{id}`
- [ ] **3** — Service implementation (`BoatClassService`)
- [ ] **4** — Controller integration tests (`BoatClassControllerIT`) — `MockMvc` against H2
- [ ] **5** — Controller (`BoatClassController`)

#### Person

- [ ] **6** — Repository interface (`PersonRepository`)
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
