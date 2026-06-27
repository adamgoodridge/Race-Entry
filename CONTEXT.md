# Victoria Outboard Club — Race Entry

The VOC Race Entry system manages boat registrations for speedboat racing events run by the Victoria Outboard Club. It covers the full entry lifecycle from draft through to race-day approval, but excludes results and scoring.

## Language

### People

**Person**: A club member or driver with full contact and licensing details (name, address, phone, SBA Licence). A Person may or may not have a linked User account.
_Avoid_: Member, participant, sailor

**User**: An account holder who can log in to the system. Holds only name, email, and password. May be linked to at most one Person (self-service profile completion).
_Avoid_: Member, account

**Driver**: A Person listed on an Entry as eligible to operate the boat during the Event. An Entry must have at least one Driver before it can be submitted.
_Avoid_: Skipper, helm, crew, operator

**Admin**: A User with secretary-level privileges. Can manage Events, BoatClasses, Entries, and Persons. There is no separate "race secretary" role.
_Avoid_: Race secretary, moderator, staff

### Boats

**Boat**: A speedboat registered in the VOC system, identified by its Race Number. A Boat has one owner (a Person) but no fixed class — the class is chosen per Entry.
_Avoid_: Vessel, craft

**Race Number**: A number that uniquely identifies a Boat across the entire system. Not per-class.
_Avoid_: Sail number, boat number, hull number

**BoatClass**: A handicapping category that groups Boats by performance so they race fairly against each other. A Boat may enter different classes in different Events. A BoatClass can be marked Inactive to hide it from future Events.
_Avoid_: Class, category, rating

### Events

**Event**: A regatta — a day or consecutive days of speedboat racing organised by VOC. An Event lists which BoatClasses are available and may cap the number of Drivers per Entry (`maxDrivers`).

**Closing Deadline**: A datetime set on an Event. When it passes the Event automatically closes and no new Entries can be submitted.

**Late Entry**: An Entry submitted or approved after the Closing Deadline, possible only via Admin override on that specific Entry.

### Entries

**Entry**: A Boat's registration to compete in a specific Event under a specific BoatClass. One Entry per Boat per Event.
_Avoid_: Registration, application

**Declaration Form**: A PDF auto-generated per Entry listing the Boat, Event, and all Drivers. Signed by the boat owner on race day.

**SBA Licence**: A boating licence required for every Driver. The expiry date is checked against the Event's start date at submission time — an expired licence blocks submission.

### Status values

**Entry statuses** (in lifecycle order):

- `DRAFT` — created but not yet submitted by the owner
- `SUBMITTED` — owner has confirmed intent to race; awaiting Admin review
- `CHANGES_REQUESTED` — Admin has requested corrections; the `secretaryComment` field states what to fix. Reverts to `SUBMITTED` once the owner resubmits
- `APPROVED` — Admin has approved the Entry
- `PAID` — entry fee collected in person (Admin toggle)
- `CANCELLED` — withdrawn; permanent; cannot be reinstated

**Event statuses:**

- `OPEN` — accepting new Entries
- `CLOSED` — no longer accepting Entries (reached automatically at Closing Deadline, or manually by Admin)
- `CANCELLED` — Event will not proceed; all associated Entries are automatically cancelled
