package com.darkshandev.sutori

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.darkshandev.sutori.RecyclerViewItemMatcher.atPosition
import com.darkshandev.sutori.presentation.MainActivity
import com.darkshandev.sutori.presentation.adapter.StoryPagedListAdapter
import com.google.android.material.tabs.TabLayout
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    var idlingResourceRule = IdlingResourceRule()
    private lateinit var username: String

    @Before
    fun setUp() {
        username = GeneralUtils.getRandomUsername()
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testEndToEnd() {
        // signup test
        Espresso.onView(withId(R.id.signupWelcomeButton)).perform(click())
        ViewInteractor.fillEditText(R.id.nameEditText, username)
        ViewInteractor.fillEditText(R.id.emailEditText, "$username@mail.com")
        Espresso.onView(withId(R.id.passwordEditText)).perform(click())
        Espresso.onView(withId(R.id.passwordEditText)).perform(click())
        ViewInteractor.fillEditText(R.id.passwordEditText, "123456789")
        Espresso.onView(withId(R.id.signupButton)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.signupButton)).perform(click(), click())
        ///login test
        ViewInteractor.fillEditText(R.id.emailEditText, "$username@mail.com")
        Espresso.onView(withId(R.id.passwordEditText)).perform(click())
        Espresso.onView(withId(R.id.passwordEditText)).perform(click())
        ViewInteractor.fillEditText(R.id.passwordEditText, "123456789")
        Espresso.onView(withId(R.id.loginButton)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.loginButton)).perform(click(), click())
        //main list test
        Espresso.onView(withId(R.id.rv_main))
            .perform(
                RecyclerViewActions
                    .scrollToPosition<StoryPagedListAdapter.MyViewHolder>(5)
            ).check(matches(isDisplayed()))
        Espresso.onView(AllOf.allOf(withId(R.id.rv_main), isDisplayed()))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5))
            .check(matches(atPosition(5, IsNot.not(withText("")))))
            .perform(
                click()
            )
        //detail access test
        Espresso.onView(withId(R.id.imageDetailView)).check(matches((isCompletelyDisplayed())))
        Espresso.onView(withId(R.id.imageDetailView)).check(
            matches(isDisplayed())
        ).perform(click())
        Espresso.pressBack()
        Espresso.pressBack()
        //map tab test
        Espresso.onView(withId(R.id.tab_home)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.tab_home)).perform(ViewInteractor.selectTabAtPosition(1))
        Espresso.onView(withId(R.id.map)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.map)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.map))
            .perform(swipeLeft(), swipeLeft(), swipeLeft(), swipeLeft())
        Espresso.onView(withId(R.id.tab_home)).perform(ViewInteractor.selectTabAtPosition(0))
        //add post test
        Espresso.onView(withId(R.id.fab_home)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.fab_home)).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withId(R.id.switchCamera)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.switchCamera)).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withId(R.id.captureImage)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.captureImage)).check(matches(isDisplayed())).perform(click())

        Espresso.onView(withId(R.id.preview_post)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.descriptionText)).check(matches(isDisplayed())).perform(click())
        ViewInteractor.fillEditText(
            R.id.descriptionText,
            "integration testing - Espresso - $username"
        )
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.preview_post)).perform(swipeDown(), swipeDown())
        Espresso.onView(withId(R.id.sendButton))
            .check(matches(isCompletelyDisplayed()))
            .perform(click())
        Espresso.onView(withId(R.id.rv_main)).check(matches(isCompletelyDisplayed()))
            .perform(
                RecyclerViewActions
                    .scrollToPosition<StoryPagedListAdapter.MyViewHolder>(0)
            ).check(matches(isDisplayed()))
        //setting and logout test
        Espresso.onView(withId(R.id.action_setting)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.action_setting)).perform(click())
        Espresso.onView(withId(R.id.logoutButton)).check(matches(isCompletelyDisplayed()))
        Espresso.onView(withId(R.id.logoutButton)).check(matches(isDisplayed())).perform(
            click()
        )

    }
}

object RecyclerViewItemMatcher {
    fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View?>): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}

object ViewInteractor {
    fun fillEditText(viewId: Int, text: String) {
        Espresso.onView(withId(viewId)).perform(clearText(), typeText(text), pressImeActionButton())

    }

    fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() =
                allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()
                tabAtIndex.select()
            }
        }
    }
}

object GeneralUtils {
    fun getRandomUsername(): String {
        return Calendar.getInstance().time.time.toString()
    }
}