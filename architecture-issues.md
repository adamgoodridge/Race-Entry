This application will be used to manage boat racing events, including the boats, their owners, the entries for each event, and the drivers associated with each entry. The main entities in this application are Boat, Owner, Entry, and Driver.

You should design the classes and their relationships in a way that allows for flexibility and maintainability. The relationships between these entities should be designed to allow for easy navigation and management of the data.

Under no circumstances should the relationships between these entities create a tight coupling that would make it difficult to maintain or extend the application in the future. Relationships should be designed in a way that allows for easy navigation and management of the data, without creating dependencies that in objects that should be able to exist independently of each other.
An object should be able to exist without the other, and they should not be dependent on each other. Do you suggest using different objects solely for maintaining relationships? Or do you suggest using a different design pattern to decouple the objects and their relationships?



Business logic
- An owner is a person
- Each boat must have an owner, and each owner can have multiple boats.
- Each boat can have multiple entries, and each entry must be associated with a boat.
- Each entry must have one or more drivers, and each driver can have multiple entries.
- Each entry must have exactly one boat, and each boat can have one entry to each event.