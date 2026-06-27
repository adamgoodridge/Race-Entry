# User (auth) and Person (racing identity) are separate entities

`User` holds only what login requires — name, email, password, and role. `Person` holds racing-specific details: physical address, phone, and SBA Licence. A `User` may complete a self-service profile to create and link a `Person`; an Admin can also create a `Person` without a `User` (e.g. to add a driver who hasn't registered yet) or reassign a `Person` to a different `User`.

Merging them would force every account holder to provide racing details at registration, exclude non-racing admins from having accounts, and make admin reassignment impossible. Keeping them separate lets the two concerns evolve independently.
