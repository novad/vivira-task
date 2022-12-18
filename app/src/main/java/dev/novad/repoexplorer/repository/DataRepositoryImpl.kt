package dev.novad.repoexplorer

import dev.novad.repoexplorer.model.RepoListResponse
import dev.novad.repoexplorer.network.ApiService
import dev.novad.repoexplorer.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepositoryImpl(private val apiService: ApiService) : DataRepository {

    companion object {
        private const val PER_PAGE_VALUE = 30
    }

    override suspend fun getRepositoriesList(query: String, page: Int): Result<RepoListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRepositoriesList(
                    query = query,
                    perPage = PER_PAGE_VALUE,
                    page = page
                )
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
