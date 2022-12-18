package dev.novad.repoexplorer.model

data class RepoListResponse(val total_count: Long, val items: List<Repository>)
