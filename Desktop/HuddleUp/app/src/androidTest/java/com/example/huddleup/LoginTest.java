package com.example.huddleup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.huddleup.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    /*
    @Test
    public void listGoesOverTheFold() {
        onView(withText("Hello world!")).check(matches(isDisplayed())); // checks if text is shown anywhere on screen
    }
    */

    @Test
    public void emailTest() // tests logging in
    {
        /* Examples
        onView(withId(R.id.next)).perform(click());
        onView(withText("May 2020")).check(matches(isDisplayed()));
        onView(withId(R.id.name_field)).perform(typeText("Steve"));
        onView(withId(R.id.name_field)).check(matches(isDisplayed()));
         */
        login();
    }

    private void login()
    {
        // MainActivity
        onView(withId(R.id.ButtonGetStarted)).perform(click());
        // Login Fragment
        onView(withId(R.id.EditTextLoginEmail)).perform(typeText("UserDillon")); // enters the username
        onView(withId(R.id.EditTextLoginPassword)).perform(typeText("EasyL@g1n"), closeSoftKeyboard()); // enters the password and closes the keyboard
        onView(withId(R.id.ButtonLogin)).perform(click());
    }
}
