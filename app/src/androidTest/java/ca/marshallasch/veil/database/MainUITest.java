package ca.marshallasch.veil.database;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.marshallasch.veil.MainActivity;
import ca.marshallasch.veil.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainUITest {

    @Rule
    public ActivityTestRule<MainActivity> MainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        //get fragment manager for changing fragments
        MainActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void TestStart(){
        onView(withId(R.id.login_btn)).perform(click());
    }
}
