# Mediator services own all cross-domain business logic

Business rules that span multiple domain entities (e.g. submitting an Entry requires checking the Event status, the Boat owner, and each Driver's SBA Licence) live in dedicated mediator services rather than in the individual domain services (`EntryService`, `BoatService`, etc.).

Domain services are kept single-entity: they own CRUD and simple invariants for their own entity only. The mediator calls them in sequence and enforces the cross-entity rules. This prevents domain services from taking on each other's dependencies and becoming entangled.

The alternative — putting cross-domain logic in `EntryService` — would require `EntryService` to import `BoatService`, `EventService`, and `PersonService`, creating a god service that's hard to test and impossible to reason about in isolation.
