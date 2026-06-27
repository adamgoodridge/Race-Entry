# BoatClass is chosen per Entry, not assigned to the Boat

A Boat at VOC can race in different BoatClasses in different Events (e.g. entering Class A one weekend and Class B the next). Because of this, `boatClassId` lives on `Entry`, not on `Boat`. The `Boat` entity carries no class at all.

The obvious alternative — a fixed class on the Boat — would prevent reclassification without migrating entries and would make it impossible to enter the same boat in two different class heats across different events. The per-entry model avoids both problems.

**Consequence:** when an Admin removes a BoatClass from an Event, all Entries in that class for that Event are automatically cancelled, since the class is no longer available.
