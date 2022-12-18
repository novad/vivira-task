package dev.novad.repoexplorer.model

data class Repository(
    val id: Long,
    val name: String,
    val description: String,
    val html_url: String,
    val owner: Owner
)
