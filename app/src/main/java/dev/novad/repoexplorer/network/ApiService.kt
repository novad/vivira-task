package dev.novad.repoexplorer.network

import dev.novad.repoexplorer.model.RepoListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/repositories")
    suspend fun getRepositoriesList(
        @Query("q") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): RepoListResponse
}
