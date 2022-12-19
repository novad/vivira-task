package dev.novad.repoexplorer

import dev.novad.repoexplorer.model.Owner
import dev.novad.repoexplorer.model.RepoListResponse
import dev.novad.repoexplorer.model.Repository
import dev.novad.repoexplorer.repository.DataRepository
import javax.inject.Inject

class MockDataRepository @Inject constructor(var reposResult: Result<RepoListResponse>) :
    DataRepository {
    companion object {
        val testReposList = listOf(
            Repository(
                id = 123,
                name = "Cool repo",
                description = "My very cool repo",
                html_url = "https:github.com/repo/cool",
                owner = Owner(111, "dog", "")
            ),
            Repository(
                id = 321,
                name = "Huge repo",
                description = "This repo has a lot of lines",
                html_url = "https:github.com/repo/huge",
                owner = Owner(222, "cat", "")
            )
        )
    }

    override suspend fun getRepositoriesList(query: String, page: Int): Result<RepoListResponse> {
        return reposResult
    }
}
