# Race Entry — Architecture Refactor Plan

Derived from architectural review of `architecture-issues.md`. 23 tasks across 5 phases. Start with Phase 1 — all six tasks are unblocked.

---

## Kanban

### Phase 1 — Safe Cleanup (no schema changes, start immediately)

| # | Task | Blocks |
|---|------|--------|
| 1 | Delete `Random.java` and `main()` code-generator methods from `Person`, `Event`, `EventView` | #9 |
| 2 | Fix `@Controller("/")` annotation bug in `GeneralController`; rename package `Controller` → `controller` | #9 |
| 3 | Replace all `System.out.println` with SLF4J logging | — |
| 4 | Make `UserAuthentication` a `@Component`; inject instead of `new UserAuthentication()` | #21 |
| 5 | Remove `previousUrl` `@Transient` field from all domain entities (`Boat`, `Person`, `Entry`, `Event`) | — |
| 6 | Remove `User` from `EntryFormData`; resolve current user from `SecurityContextHolder` in controllers | #19 |

### Phase 2 — DTO Cleanup (unblocks most of Phase 3)

| # | Task | Blocked by | Blocks |
|---|------|-----------|--------|
| 7 | Convert `BoatView` → `BoatFormData`: remove `@Entity`, make plain POJO with validation only | — | #11, #12, #13, #14 |
| 8 | Convert `EventView` → `EventFormData`: remove `@Entity`, make plain POJO with validation only | — | #11, #12, #13, #14 |

### Phase 3 — Entity Model (schema migrations begin)

| # | Task | Blocked by | Blocks |
|---|------|-----------|--------|
| 9 | Add surrogate `Long` PK to `BoatClass`; make `className` a `@Column(unique = true)` | #1, #2 | #10, #16 |
| 10 | Rename `Boat.className` → `boatType` (field + column); update all references | #9 | — |
| 11 | Introduce `EntryStatus` enum (`DRAFT → OPEN → CLOSED`); replace `Event.open` Boolean | #7, #8 | #15, #20, #23 |
| 12 | Change `Event.visible` from `Integer` to `Boolean` | #7, #8 | — |
| 13 | Remove `Event.duration` column; add computed `getDuration()` from `startDate`/`endDate` | #7, #8 | — |
| 14 | Remove `List<Entry>` from `Event`; move `@PreRemove` guard to `EntryService` | #7, #8 | #15, #16, #21 |

### Phase 4 — Entry Model (heaviest schema changes)

| # | Task | Blocked by | Blocks |
|---|------|-----------|--------|
| 15 | Replace `driverOne`/`driverTwo` with `List<Person> drivers` via `@ManyToMany` join table; update `EntryFormData` to `List<Long> driverIds` | #11, #14 | #17, #18, #19, #20, #22, #23 |
| 16 | Replace `Entry.boatClass` String with `List<BoatClass> boatClasses` via `@ManyToMany` join table | #9, #14 | #19 |
| 17 | Create `EntryDeclaration` entity `(entry, person, signature, signedDate)`; remove hardcoded signature fields from `Entry`; update `EntryFormData` to `List<DeclarationFormData>` | #15 | #18, #22 |

### Phase 5 — Service Layer

| # | Task | Blocked by | Blocks |
|---|------|-----------|--------|
| 18 | Extract `EntryPDFService` from `EntryService`; fix NPE bug at line 111–112 | #15, #17 | — |
| 19 | Create `EntryAssemblyService`; move `formEventEntry()` logic; remove `BoatRepository`/`PersonRepository` from `EntryService` | #6, #15, #16 | #20 |
| 20 | Create `EntryEligibilityService`; enforce: event OPEN, boat/drivers not soft-deleted, boat-per-event uniqueness, min one driver | #11, #15, #19 | — |
| 21 | Create `DashboardService` with `getAdminDashboard()` and `getUserDashboard(User)`; gut `GeneralController.home()` | #4, #14 | — |
| 22 | Move encryption to `EncryptionService`; remove `@ColumnTransformer` from `Person`/`Entry`; load key from `@Value("${encryption.key}")` | #15, #17 | — |
| 23 | Update `EntryRepository` JPQL queries for `EntryStatus` enum and `drivers` collection | #11, #15 | — |

---

## Decision Log

| Decision | Outcome |
|----------|---------|
| `User` vs `Person` | `User` = system account (audit only). `Person` = real-world individual. Both refs on `Boat` are intentional and distinct. |
| `Person` without `User` | A `Person` can exist without a `User` account. The `user` ref is nullable created-by, not a domain relationship. |
| Driver count | Hardcoded `driverOne`/`driverTwo` replaced with `List<Person> drivers` (min 1, validated in service). |
| `Boat.className` | Removed. Renamed concept to `boatType` on `Boat`. |
| `Entry` boat classes | `Entry.boatClass` String → `List<BoatClass>` via `@ManyToMany`. |
| `BoatClass` PK | Surrogate `Long` PK. `className` becomes `@Column(unique = true)`. |
| Event status | `Event.open` Boolean → `EntryStatus` enum (`DRAFT → OPEN → CLOSED`). |
| Event visibility | `Event.visible` stays, typed as `Boolean` (orthogonal to status). |
| Event duration | `Event.duration` column removed. Computed from `startDate`/`endDate`. |
| Event→Entry coupling | `List<Entry>` removed from `Event`. Queries use `EntryRepository.findByEventId()`. |
| Declaration signatures | `EntryDeclaration` entity `(entry, person, signature, signedDate)`. All signatories sign upfront in one submission. |
| Cross-domain services | Multiple fine-grained services allowed (`EntryAssemblyService`, `EntryEligibilityService`, `EntryPDFService`, `DashboardService`). |
| Business rule enforcement | Service layer only. No database-level unique constraints for business rules. |
| Soft-delete guard | UI filters deleted entities; `EntryEligibilityService` also rejects them at save time. |
| `BoatView` / `EventView` | Converted to `BoatFormData` / `EventFormData` — plain POJOs, `@Entity` removed. |
| `User` in form data | Removed from `EntryFormData`. Resolved server-side from Spring Security context only. |
| `previousUrl` | Removed from all domain entities. Handled at controller layer (request param or `@SessionAttribute`). |
| Encryption | Removed from `@ColumnTransformer`. Moved to `EncryptionService` with key from environment config. |
| Logging | All `System.out.println` → SLF4J. |
| `UserAuthentication` | Converted to `@Component`, injected via constructor. |
