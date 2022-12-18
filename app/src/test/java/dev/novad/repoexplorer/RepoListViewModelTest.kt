package dev.novad.repoexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import dev.novad.repoexplorer.model.RepoListResponse
import dev.novad.repoexplorer.model.Repository
import dev.novad.repoexplorer.repository.DataRepository
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class RepoListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get repository list succeeds on consecutive requests`() = runTest {
        // Given the repository returns results on the first page request,
        val mockedDataRepository: DataRepository = mock()
        whenever(mockedDataRepository.getRepositoriesList("test", 1)).thenReturn(
            Result.success(
                RepoListResponse(100, listOf(mock(), mock(), mock()))
            )
        )
        // And returns empty result for the second page,
        whenever(mockedDataRepository.getRepositoriesList("test", 2)).thenReturn(
            Result.success(
                RepoListResponse(100, emptyList())
            )
        )

        // Set observer and initialize view model
        val observer: Observer<ViewState<List<Repository>>> = mock()
        val viewModel = RepoListViewModel(mockedDataRepository)
        viewModel.uiRepoListState.observeForever(observer)

        // Verify the page is initially set to 0
        assertEquals(0, viewModel.pageNumber)
        assertNull(viewModel.uiRepoListState.value?.dataOrNull())

        // When fetching the list for the first and second page,
        viewModel.getRepositoriesList("test")
        viewModel.getRepositoriesList("test")
        argumentCaptor<ViewState<List<Repository>>>().run {
            // Verify 5 update are done:
            // one for the initial state,
            // one for the loading state, one for the successful response
            // And 2 updates for the second page call
            verify(observer, times(5)).onChanged(capture())
            assertTrue(firstValue is ViewState.Success)
            assertTrue(secondValue is ViewState.Loading)
            assertTrue(thirdValue is ViewState.Success)
            assertEquals(3, thirdValue.dataOrNull()?.size)

            // Verify the current page is set to 1
            assertEquals(1, viewModel.pageNumber)
            // Verify the last update is an empty list
            assertEquals(0, lastValue.dataOrNull()?.size)
        }
    }

    @Test
    fun `repository list succeeds on first call and fails on second request`() = runTest {
        // Given the repository returns results on the first page request,
        val mockedDataRepository: DataRepository = mock()
        whenever(mockedDataRepository.getRepositoriesList("test", 1)).thenReturn(
            Result.success(RepoListResponse(100, listOf(mock(), mock(), mock())))
        )
        // And returns an error result for the second page,
        whenever(mockedDataRepository.getRepositoriesList("test", 2)).thenThrow(RuntimeException())

        // Set observer and initialize view model
        val observer: Observer<ViewState<List<Repository>>> = mock()
        val viewModel = RepoListViewModel(mockedDataRepository)
        viewModel.uiRepoListState.observeForever(observer)

        // Verify the page is initially set to 0
        assertEquals(0, viewModel.pageNumber)
        assertNull(viewModel.uiRepoListState.value?.dataOrNull())

        // When fetching the list 2 times,
        viewModel.getRepositoriesList("test")
        viewModel.getRepositoriesList("test")
        argumentCaptor<ViewState<List<Repository>>>().run {
            // Verify 4 update are done:
            // one for the initial state,
            // one for the loading state, one for the successful response
            // And 1 update for the second page call which returns an error
            verify(observer, times(4)).onChanged(capture())
            assertTrue(firstValue is ViewState.Success)
            assertTrue(secondValue is ViewState.Loading)
            assertTrue(thirdValue is ViewState.Success)
            assertEquals(3, thirdValue.dataOrNull()?.size)

            // Verify the current page is set to 1
            assertEquals(1, viewModel.pageNumber)
            // Verify the already fetched values remain
            assertEquals(3, thirdValue.dataOrNull()?.size)
        }
    }

    @Test
    fun `repository list fails`() = runTest {
        // Given the repository returns an error on the request,
        val mockedDataRepository: DataRepository = mock()
        whenever(
            mockedDataRepository.getRepositoriesList(
                any(),
                any()
            )
        ).thenReturn(Result.failure(RuntimeException()))

        // Set observer and initialize view model
        val observer: Observer<ViewState<List<Repository>>> = mock()
        val viewModel = RepoListViewModel(mockedDataRepository)
        viewModel.uiRepoListState.observeForever(observer)

        // When fetching the list of repositories
        viewModel.getRepositoriesList("test")
        argumentCaptor<ViewState<List<Repository>>>().run {
            // Verify 2 update are done:
            // One for the initial state,
            // one for the loading state, one for the error response
            verify(observer, times(3)).onChanged(capture())
            assertTrue(firstValue is ViewState.Success)
            assertTrue(secondValue is ViewState.Loading)
            assertTrue(thirdValue is ViewState.Error)
            // Verify the already fetched values remain empty
            assertNull(thirdValue.dataOrNull())
            assertTrue((thirdValue as ViewState.Error).throwable is java.lang.RuntimeException)
        }
    }
}
