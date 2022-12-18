package dev.novad.repoexplorer.repository

import dev.novad.repoexplorer.model.RepoListResponse

interface DataRepository {
    suspend fun getRepositoriesList(query: String, page: Int): Result<RepoListResponse>
}
