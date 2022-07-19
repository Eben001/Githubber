package com.ebenezer.gana.githubber.data.model.response

data class GithubComment(
    val id: String?,
    val body: String?,
) {
    override fun toString() = "$body - $id"
}