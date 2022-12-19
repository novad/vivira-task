package dev.novad.repoexplorer

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.novad.repoexplorer.Matchers.Companion.atPosition
import dev.novad.repoexplorer.MockDataRepository.Companion.testReposList
import dev.novad.repoexplorer.di.Bindings
import dev.novad.repoexplorer.model.RepoListResponse
import dev.novad.repoexplorer.repository.DataRepository
import org.hamcrest.core.IsNot.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@UninstallModules(Bindings::class)
@HiltAndroidTest
class RepoListFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var mockedDataRepository: DataRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun repoListCorrectlyDisplayed() {
        // When the data repository returns a repo list successfully,
        mockedDataRepository =
            MockDataRepository(Result.success(RepoListResponse(2, testReposList)))

        launchActivity<MainActivity>().use {
            // Verify the progress bar and list are hidden at start
            onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
            onView(withId(R.id.repositoriesRecyclerView)).check(matches(not(isDisplayed())))
            // Then verify the empty list text is displayed
            onView(withId(R.id.empty_state_text)).check(matches(isDisplayed()))
            // Verify the edit text and button are enabled and visible
            onView(withId(R.id.editText)).check(matches(isDisplayed()))
            onView(withId(R.id.editText)).check(matches(isEnabled()))
            onView(withId(R.id.fab)).check(matches(isDisplayed()))
            onView(withId(R.id.fab)).check(matches(isEnabled()))

            // Input text and tab the search button
            onView(withId(R.id.editText)).perform(replaceText("test"))
            onView(withId(R.id.fab)).perform(click())

            // Verify the list of repos is visible and contains the expected elements
            onView(withId(R.id.repositoriesRecyclerView)).check(matches(isDisplayed()))
            onView(withId(R.id.repositoriesRecyclerView)).check(matches(hasChildCount(testReposList.size)))

            testReposList.forEachIndexed { index, repo ->
                onView(withId(R.id.repositoriesRecyclerView)).check(
                    matches(atPosition(index, hasDescendant(withText(repo.name))))
                )
                onView(withId(R.id.repositoriesRecyclerView)).check(
                    matches(atPosition(index, hasDescendant(withText(repo.html_url))))
                )
                onView(withId(R.id.repositoriesRecyclerView)).check(
                    matches(atPosition(index, hasDescendant(withText(repo.description))))
                )
                onView(withId(R.id.repositoriesRecyclerView)).check(
                    matches(atPosition(index, hasDescendant(withText(repo.owner.login))))
                )
            }
        }
    }

    @Test
    fun emptyList() {
        // When the data repository returns a repo list successfully,
        mockedDataRepository =
            MockDataRepository(Result.failure(RuntimeException()))

        launchActivity<MainActivity>().use {
            // Input text and tab the search button
            onView(withId(R.id.editText)).perform(replaceText("test"))
            onView(withId(R.id.fab)).perform(click())

            // Verify the empty state text is visible and the snackbar is displayed
            onView(withId(R.id.repositoriesRecyclerView)).check(matches(not(isDisplayed())))
            onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.empty_result_text)))
        }
    }
}
