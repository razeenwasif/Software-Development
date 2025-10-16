# Reference Documentation for Reaction Feature Implementation

list of documentations covering the primary platforms, libraries, and key APIs 
used for building the reaction views, censor integration, and dark-mode toggle.

## Android & Jetpack Components
- **AppCompatDelegate#setDefaultNightMode** – controlling night mode at runtime. <https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#setDefaultNightMode(int)>
- **Edge-to-Edge APIs** – guidance for `EdgeToEdge.enable` usage. <https://developer.android.com/develop/ui/views/layout/edge-to-edge>
- **RecyclerView & Adapter patterns** – building scrolling lists (`RecyclerView`, `RecyclerView.Adapter`, `RecyclerView.ViewHolder`). <https://developer.android.com/develop/ui/views/layout/recyclerview>
- **LinearLayoutManager** – vertical list layout manager. <https://developer.android.com/reference/androidx/recyclerview/widget/LinearLayoutManager>
- **Intent navigation & extras** – launching activities and passing data. <https://developer.android.com/reference/android/content/Intent>
- **SharedPreferences** – persisting simple key/value pairs for UI state. <https://developer.android.com/reference/android/content/SharedPreferences>

## Animation 
- **Lottie** - wave animation in background. <https://airbnb.io/lottie/#/android>

## Material Design 3 Components
- **MaterialToolbar** – top app bar from the Material Components library. <https://m3.material.io/components/top-app-bar/overview>
- **MaterialSwitch** – Material 3 switch widget used for the dark-mode toggle. <https://m3.material.io/components/switch/overview>
- **MaterialCardView** – Material container for message and reaction rows. <https://developer.android.com/reference/com/google/android/material/card/MaterialCardView>
- **MaterialButton** – text button styling for “View reactions”. <https://m3.material.io/components/buttons/overview>
- **TextInputLayout / TextInputEditText** – Material outlined search field. <https://m3.material.io/components/text-fields/overview>

## Layout & View System
- **ConstraintLayout** – positioning UI elements in the activity and row layouts. <https://developer.android.com/develop/ui/views/layout/constraint-layout>
- **LayoutInflater** – inflating XML layouts within adapters. <https://developer.android.com/reference/android/view/LayoutInflater>
- **RecyclerView actions** – interacting with list rows in Espresso tests. <https://developer.android.com/develop/ui/views/layout/recyclerview>

## Java Platform APIs & Libraries
- **java.util.stream.Collectors** – grouping and counting reactions by type. <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html>
- **java.util.concurrent.ConcurrentHashMap** – thread-safe storage for reactions by message. <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html>
- **java.util.UUID** – generating unique identifiers for reactions. <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/UUID.html>
- **Gson** – JSON serialisation/deserialisation used by the Android `DataManager`. <https://github.com/google/gson>

## Internal Modules
- **Censor Facade** – in-house profanity filtering utilities (`censor` package copied into Android module). Source lives under `app/src/censor`.

## Testing Frameworks
- **Espresso** – UI testing framework used for user-search navigation tests. <https://developer.android.com/training/testing/espresso>

These links collectively document the public APIs and components relied upon while delivering the reaction visualisation, message censoring, and theme toggle capabilities on the `activity-4` branch.
