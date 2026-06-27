# Entities reference each other by ID, not by JPA object reference

Entities store foreign keys as plain `Long` IDs (e.g. `Boat.ownerId`, `Entry.boatId`) rather than JPA `@ManyToOne` / `@OneToMany` relationships. Cross-entity lookups are resolved explicitly by the service or mediator layer when needed.

This keeps entities anemic and prevents accidental eager-loading of entire object graphs. It also enforces the bounded-context boundary: `EntryService` cannot reach into `Boat` internals without going through `BoatService`, making the dependency explicit and testable.

The trade-off is that joins that JPA would handle automatically must be done manually in the service layer. That cost is accepted here because the coupling JPA relationships introduce causes more problems than it solves in a multi-entity domain like this one.
