package com.example.comp2100miniproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.allOf;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.assertion.ViewAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies that the user search flow completes and navigates to the posts screen. Built using
 * Espresso only with project-specific logic.
 */
@RunWith(AndroidJUnit4.class)
public class UserSearchNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void searchForUserAndOpenPosts() {
        onView(withContentDescription(R.string.search_users)).perform(click());

        onView(withId(R.id.editTextSearch)).perform(typeText("Alex"), closeSoftKeyboard());

        onView(withId(R.id.recyclerViewUsers))
                .check(ViewAssertions.matches(hasDescendant(withText(containsString("Alex")))));

        onView(allOf(withId(R.id.buttonViewPosts), isDisplayed())).perform(click());

        onView(withId(R.id.userPostsToolbar))
                .check(ViewAssertions.matches(hasDescendant(withText(containsString("posts")))));
    }
}
