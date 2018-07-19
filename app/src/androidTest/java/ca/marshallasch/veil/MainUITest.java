package ca.marshallasch.veil;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * This class automates the tests for the UI components on the application.
 * @author Weihan Li
 * @version 1.0
 * @since 2018-07-05
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainUITest {

    //start activity before testing
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    //actual tests
    @Test
    public void mainUITest() {
        //on the start page click the sign up button
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.sign_up_btn), withText("Sign Up"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1)));
        materialButton.perform(scrollTo(), click());

        //on the SignUp page click and add the string "John" to the first name text edit
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.first_name_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.first_name_text_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("John"), closeSoftKeyboard());

        //on the SignUp page click and add the string "Doe" to the last name text edit
        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.last_name_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.last_name_text_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("Doe"), closeSoftKeyboard());

        //on the SignUp page click and add the string "john@gmail.com" to the email text edit
        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.email_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.email_text_input),
                                        0),
                                0)));
        textInputEditText3.perform(scrollTo(), replaceText("john@gmail.com"), closeSoftKeyboard());

        //on the SignUp page click and add the string "Symfony65!" to the password text edit
        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.password_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.password_text_input),
                                        0),
                                0)));
        textInputEditText5.perform(scrollTo(), replaceText("Symfony65!"), closeSoftKeyboard());

        //on the SignUp page click and add the string "Symfony65!" to the confirm password text edit
        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.password_conf_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.password_conf_text_input),
                                        0),
                                0)));
        textInputEditText6.perform(scrollTo(), replaceText("Symfony65!"), closeSoftKeyboard());

        //on the SignUp page click the sign up button
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.done_button), withText("Sign Up"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1)));
        materialButton2.perform(scrollTo(), click());

        //On the Dashboard page click the add new post button
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.new_post),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_container),
                                        0),
                                7),
                        isDisplayed()));
        floatingActionButton.perform(click());

        //On the create a post page add the string "Post" to the title parameter
        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.title_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.title_text_input),
                                        0),
                                0)));
        textInputEditText7.perform(scrollTo(), replaceText("Post"), closeSoftKeyboard());

        //On the create a post page add the string "test,john" to the tags parameter
        ViewInteraction textInputEditText8 = onView(
                allOf(withId(R.id.tags_text_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tags_text_input),
                                        0),
                                0)));
        textInputEditText8.perform(scrollTo(), replaceText("test,john"), closeSoftKeyboard());

        //on the create post page check the Anonymous option
        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.anonymous), withText("Anonymous"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatCheckBox.perform(scrollTo(), click());

        //on the create post page uncheck the Anonymous option
        ViewInteraction appCompatCheckBox2 = onView(
                allOf(withId(R.id.anonymous), withText("Anonymous"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatCheckBox2.perform(scrollTo(), click());

        //on the create re-check the Anonymous option
        ViewInteraction appCompatCheckBox3 = onView(
                allOf(withId(R.id.anonymous), withText("Anonymous"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatCheckBox3.perform(scrollTo(), click());

        //on the create post page add the string "This is a test" to the content parameter
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.post_message),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("This is a test."), closeSoftKeyboard());

        //on the create post page press the post button
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.save), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                6)));
        materialButton3.perform(scrollTo(), click());

        //on the dashboard page click the Discover Forums button
        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.discover_forums_btn), withText("Discover Forums"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_container),
                                        0),
                                0),
                        isDisplayed()));
        materialButton4.perform(click());

        //on the Discover Forums page click view on the newly created post
        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.view_btn), withText("View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.list_view),
                                        0),
                                3),
                        isDisplayed()));
        materialButton5.perform(click());

        //on the view post page click the comment bar to add a comment
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.comment_bar),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_container),
                                        0),
                                9),
                        isDisplayed()));
        appCompatImageView.perform(click());

        //in the create a comment page add the string "test comment" to the comment content param
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.post_message),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText2.perform(scrollTo(), replaceText("test comment"), closeSoftKeyboard());

        //in the create a comment page uncheck the Anonymous option
        ViewInteraction appCompatCheckBox4 = onView(
                allOf(withId(R.id.anonymous), withText("Anonymous"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatCheckBox4.perform(scrollTo(), click());

        //in the create a comment page click the post button
        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.post_comment_btn), withText("Post"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton6.perform(scrollTo(), click());

        //press back to return to dashboard page
        pressBack();

    }

    /**
     * This is a helper functions for accessing child views.
     * @param parentMatcher the matcher that holds thes child views
     * @param position the index at which child view is to be used
     * @return
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
