# Project Enhancements (activity-4)

## Feature Summary

1. **Reaction System Integration**
   - Ported reaction models (`Reaction`, `ReactionType`, `ReactionSummary`) 
   and DAO logic into the Android module.
   - Added `ReactionDAO` to manage reactions per message using in-memory maps 
   and later extended it with persistence-aware accessors.
   - Created `MessageReactionsActivity` with RecyclerView 
   (via `ReactionSummaryAdapter`) to show emoji counts, including censored 
   message content, and a dialog-driven reaction picker.

2. **Message/UI Refinements**
   - Revamped message list (`MessageAdapter`, `fragment_message.xml`) to show 
   author chips, timestamps, reaction summaries, and a “View reactions” button.
   - Integrated profanity censoring (copied `censor` package) to sanitise 
   message text before display.

3. **User Search Experience**
   - Implemented `UserSearchActivity`, `UserSearchAdapter`, and supporting 
   layouts to filter usernames live, persist last query with `SharedPreferences`,
   and display Material cards.
   - Added `UserPostsActivity` with `PostDAO.getPostsByUser` to list posts 
   authored by a selected user, reusing `PostAdapter` and linking back to 
   `PostViewerActivity`.

4. **Toolbar Enhancements & Dark Mode**
   - Converted the main toolbar to inflate menu items in code, adding a search 
   icon and MaterialSwitch for night mode. State stored via `SharedPreferences`.
   - Added optional Gradle task `installDebugOnEmulator` to build/install via 
   `adb`.
   - Dark mode switch sits in the menu ensuring the Material action view 
   renders reliably.

5. **Modern Material Theming (Black/Purple)**
   - Defined custom light/dark palettes (primary, surface, background) for 
   black/purple branding.
   - Restyled layouts (`activity_main.xml`, `item_post.xml`, etc.) with 
   Material cards, chips, and input components to match Material 3 demos.

6. **Testing & Documentation**
   - Added Espresso instrumentation test `UserSearchNavigationTest` to cover 
   search -> posts navigation.

7. **Persistent Data on Android**
   - Introduced an Android-aware `AndroidIOFactory` so the existing 
   `DataManager` writes to internal `saved-data/` files.
   - Added `ReactionSerializer` and extended `DataManager` pipelines to 
   persist reactions alongside users/posts/messages.
   - `MainActivity` now loads exclusively from disk—no random seeding—while 
   reaction updates trigger `DataManager.writeAll()`.

8. **Selective Reaction Insertion**
   - Replaced the “add all reactions” FAB with a tonal Material button 
   opening a reaction picker dialog.
   - Selected reactions are added individually, persisted, and confirmed 
   via Snackbar.

## Implementation Notes
- Menu wiring moved to `MainActivity.onCreateOptionsMenu` to guarantee action views and switch display.
- Reaction seeding hooks into comment generation to create realistic demo data (only in JVM module now).
- SharedPreferences used for both dark mode (`ui_prefs`) and search query persistence (`user_search_prefs`).
- Card-based layouts use `MaterialCardView`, `Chip`, and modern spacing for a cohesive Material look.
- Android module now initialises `DataManager` with internal storage, reading/writing JSON snapshots via `AndroidIOFactory`.
- Gson (`com.google.code.gson:gson`) is bundled in the Android module to serialise and deserialise the JSON payloads.

# persistentdata Package Overview

The `persistentdata` package offers a layered data pipeline for serialising DAO contents to disk.

## Core Components

1. **DataManager**
   - Singleton orchestrator holding pipelines for users, posts, messages, and reactions (`DataPipeline<Domain, JsonElement>`).
   - `readAll()` clears DAOs, reads JSON arrays, and repopulates objects (including wiring messages to their parent posts and feeding reactions back to `ReactionDAO`).
   - `writeAll()` streams DAO iterators through their respective pipelines to `saved-data/*.json`.

2. **DataPipeline<T, S>**
   - Generic pipeline combining an `IOFactory` (file IO), `FormattedFactory<S>` (JSON/CSV abstraction), and `Serializer<T,S>`.
   - `writeFrom(Iterator<T>)` wraps a writer, informs format to put header/footer, serialises each element, and flushes.
   - `readTo(AddToDAO<T>)` wraps a reader, iterates formatted entries, deserialises with `Serializer`, and sends to callback (e.g., DAO insertion).

3. **IO Layer (persistentdata.io)**
   - `IOFactory` interface; `ComputerIOFactory` resolves relative paths (`../saved-data`) and returns `BufferedReader/Writer`. Silently returns `null` on IO failure to keep pipeline resilient.

4. **Formatted Factories**
   - `formatted.JsonFormattedFactory` / `JsonReader` / `JsonWriter`: convert arrays of `JsonElement` using Gson. `JsonWriter` handles header “[”, comma separation, footer “]”.
   - CSV counterparts exist (`CSVFormattedFactory`, `CSVReader`, `CSVWriter`) to support alternate tasks, but JSON is used currently.

5. **Serializers**
   - Convert between domain objects and serialisable form (String[] for CSV, JsonElement for JSON). Examples: `JsonUserSerializer`, `JsonPostSerializer` (registers `SortedDataInstanceCreator`), `JsonReactionSerializer`.
   - Records such as `Message` and `User` are handled with Gson auto-mapping.

6. **Support Utilities**
   - `SortedDataInstanceCreator` ensures posts’ `SortedData` field is reinitialised with `SortedDataFactory` when deserialising.
   - `PersistentDataException` wraps IO/format errors caught inside pipelines.

## Flow Summary
- **Write Path**: DAO iterator → `DataPipeline.writeFrom` → `Serializer.serialize` → `FormattedWriter.putNext` → disk via `ComputerIOFactory` writer.
- **Read Path**: `ComputerIOFactory` reader → `FormattedReader` (`hasNext/getNext`) → `Serializer.deserialize` → callback (DAO insert or relationship wiring).
- The design isolates file format, serialisation, and storage mechanics so swapping to another medium (CSV, different IO) only requires swapping factories.
