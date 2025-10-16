## Populating and Viewing Sample Reactions

### Populate JSON/CSV Seed Data (JVM module)
1. `cd app`
2. `mvn -q exec:java -Dexec.mainClass=PopulateDemoData`
   - Clears DAOs, seeds demo users/posts/messages/reactions, and writes them to `saved-data/`.
3. `mvn -q exec:java -Dexec.mainClass=Main`
   - Loads the saved dataset and prints the detailed reactions report.

Example output:

```
Data loaded successfully.
User: alice
User: bob
User: charlie
Number of users: 3
Post: 1acc130c-e7a7-4380-b2bf-9b69ab74320c
Post: 5d3c9c4f-7fb2-4322-8769-ba552dd59e16
Number of posts: 2
Reactions for post 1acc130c-e7a7-4380-b2bf-9b69ab74320c (Study Group: Design Patterns):
  Message 4fc7d9a2-266a-4e9b-89c2-856fbc28c2d1 by alice @ 2025-10-14 15:26:01
    "Let's review Factory vs Strategy tonight."
    Summary: LIKE=1, HAPPY=1
    - LIKE by bob @ 2025-10-14 15:27:01
    - HAPPY by charlie @ 2025-10-14 15:27:01
  Message 01554b35-abb9-4080-9b88-fbc93b51295b by bob @ 2025-10-14 15:26:16
    "Count me in! I can cover strategy examples."
    Summary: LOVE=1, GOOD_LUCK=1
    - LOVE by alice @ 2025-10-14 15:27:01
    - GOOD_LUCK by charlie @ 2025-10-14 15:27:01
Reactions for post 5d3c9c4f-7fb2-4322-8769-ba552dd59e16 (Help with AVL Trees):
  Message 458cee76-f408-4717-ba75-7d9c38b4a897 by charlie @ 2025-10-14 15:25:01
    "My rotations keep breaking the tree. Any tips?"
    Summary: SURPRISE=1, ANGRY=1
    - SURPRISE by alice @ 2025-10-14 15:27:01
    - ANGRY by bob @ 2025-10-14 15:27:01
Data saved successfully.
```

