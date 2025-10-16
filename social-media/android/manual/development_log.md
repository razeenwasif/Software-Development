# Development Log

This document outlines the steps taken to implement new features and fix bugs in the Android app.

## Feature: Wavy Animation and Gradient Background

1.  **Add Lottie Dependency:** The Lottie library was added to the `app/build.gradle.kts` file to enable the use of Lottie animations.

    ```gradle
    implementation("com.airbnb.android:lottie:5.2.0")
    ```

2.  **Download Animation:** A "waves" animation was downloaded from LottieFiles and saved as `waves_animation.json` in the `app/src/main/res/raw` directory.

3.  **Modify Layout:** The `LottieAnimationView` was added to the `activity_main.xml` layout file to display the animation.

    ```xml
    <com.airbnb.lottie.LottieAnimationView
        android:id="@+id/waves"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:lottie_rawRes="@raw/waves_animation"
        app:lottie_autoPlay="true"
        app:lottie_loop="true" />
    ```

4.  **Create Gradient:** A gradient drawable was created in `app/src/main/res/drawable/bg_main_gradient.xml` to be used as the background.

5.  **Modify Code:** The `MainActivity.java` file was modified to show the animation only in dark mode.

## Feature: Scrollable Community Pulse

1.  **Identify Layout:** The `item_feed_header.xml` file was identified as the layout for the "Community pulse" section.

2.  **Wrap in HorizontalScrollView:** The `ChipGroup` within the layout was wrapped in a `HorizontalScrollView` to allow for horizontal scrolling.

## Feature: Post Tags and Filtering

The app now supports tagging of posts and filtering the feed based on these tags.

### Data Structure

The `Post` model now includes a `tag` field. This field is a `String` that represents the tag for the post.

In the `posts.json` file, a `tag` can be added to a post like this:

```json
  {
    "id": "b4ecf6c3-5c8c-4f72-92f5-8f8002c3a5e9",
    "poster": "5a95eb0d-2c04-4cd0-a753-8d7d4d0b6b0f",
    "topic": "Design patterns for async flows",
    "tag": "design patterns",
    "messages": {}
  }
```

### Tag Resolution

The `DataManager.java` class has a `resolveTag` method that determines the tag for a post. If a post in `posts.json` has an explicit `tag` field, it is used. Otherwise, the tag is inferred from the post's `topic`.

### Filtering Logic

The `MainActivity.java` class is responsible for filtering the posts.

-   The `setupFilterChips` method sets up the filter chips in the "Community pulse" section. Each chip is associated with a `FeedCategory`.
-   When a chip is selected, the `pendingCategory` field is updated.
-   When the "Explore Feed" button is clicked, the `runFilter` method is called with the `pendingCategory`.
-   The `runFilter` method calls the `tagFilter` method, which filters the posts based on the selected tag.

## Bug Fixes

1.  **`InflateException`:** Fixed a crash caused by a missing `layout_width` and `layout_height` attributes in the `HorizontalScrollView`.

2.  **`NullPointerException`:** Fixed a crash caused by `findViewById` returning `null` for a view in the header. This was resolved by getting a reference to the header view from the `PostAdapter` and then finding the view within the header.

3.  **`NoSuchMethodError`:** Ensured that the `RandomContentGenerator.java` file was using a version of `boundedRandomLong` that is compatible with all Android API levels.

## Other Changes

*   **`PostAdapter` Header:** The `PostAdapter` was modified to support a header view, allowing the "Community pulse" section to be displayed at the top of the `RecyclerView`.
