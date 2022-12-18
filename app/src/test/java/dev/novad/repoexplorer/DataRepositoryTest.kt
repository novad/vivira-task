package dev.novad.repoexplorer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import dev.novad.repoexplorer.model.RepoListResponse
import dev.novad.repoexplorer.model.Repository
import dev.novad.repoexplorer.network.ApiService
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class DataRepositoryTest {

    @Test
    fun getRepositoryListSucceedsWithResult() = runTest {
        // Given an API response with 2 results
        val totalCount = 10L
        val mockedApi: ApiService = mock()
        whenever(
            mockedApi.getRepositoriesList(
                any(),
                any(),
                any()
            )
        ).thenReturn(RepoListResponse(totalCount, listOf<Repository>(mock(), mock())))

        // Get the list from the data repository
        val dataRepository = DataRepositoryImpl(mockedApi)
        val result = dataRepository.getRepositoriesList("test", 1)

        // Verify the results
        assertTrue(result.isSuccess)
        val repoList = result.getOrNull()
        assertNotNull(repoList)
        assertTrue(repoList?.items?.size == 2)
        assertTrue(repoList?.total_count == totalCount)
    }

    @Test
    fun getRepositoryListSucceedsWhenEmpty() = runTest {
        // Given an API response with an empty result
        val totalCount = 0L
        val mockedApi: ApiService = mock()
        whenever(
            mockedApi.getRepositoriesList(
                any(),
                any(),
                any()
            )
        ).thenReturn(RepoListResponse(totalCount, emptyList()))

        // Get the list from the data repository
        val dataRepository = DataRepositoryImpl(mockedApi)
        val result = dataRepository.getRepositoriesList("test", 1)

        // Verify the results with an empty result
        assertTrue(result.isSuccess)
        val repoList = result.getOrNull()
        assertNotNull(repoList)
        assertTrue(repoList?.items?.isEmpty() == true)
        assertTrue(repoList?.total_count == totalCount)
    }

    @Test
    fun getRepositoryListFails() = runTest {
        // Given an API response with an error response
        val mockedApi: ApiService = mock()
        whenever(
            mockedApi.getRepositoriesList(
                any(),
                any(),
                any()
            )
        ).thenThrow(RuntimeException("Api service error"))

        // Get the list from the data repository
        val dataRepository = DataRepositoryImpl(mockedApi)
        val result = dataRepository.getRepositoriesList("test", 1)

        // Verify the result is an error
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull() is RuntimeException)
    }
}