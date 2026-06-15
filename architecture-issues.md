This application has major architectural issues and is not production ready. It is a prototype to demonstrate the concept of storing information for competitors and race officials. Currently, the application lacks proper architectural breaking and separation of concerns, which can lead to maintenance challenges in the future. Objects are tightly coupled, and there is a lack of clear boundaries between different layers of the application. Relationships are tightly coupled, making it difficult to modify or extend the application without affecting other parts of the codebase. 
An object should be able to exist without the other, and they should not be dependent on each other. Do you suggest using different objects solely for maintaining relationships? Or do you suggest using a different design pattern to decouple the objects and their relationships?



Business logic
- An owner is a person
- Each boat must have an owner, and each owner can have multiple boats.
- Each boat can have multiple entries, and each entry must be associated with a boat.
- Each entry must have one or more drivers, and each driver can have multiple entries.
- Each entry must have exactly one boat, and each boat can have one entry to each event.