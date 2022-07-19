package com.ebenezer.gana.githubber.data.model.repository

import com.ebenezer.gana.githubber.data.model.Result
import com.ebenezer.gana.githubber.data.model.response.GithubComment
import com.ebenezer.gana.githubber.data.model.response.GithubPullRequest
import com.ebenezer.gana.githubber.data.model.response.GithubRepos
import com.ebenezer.gana.githubber.data.model.response.GithubToken
import okhttp3.ResponseBody

interface GithubRepository {

    suspend fun getAuthToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<GithubToken>


    suspend fun getAllGithubRepository(): Result<List<GithubRepos>>

    suspend fun getPullRequest(
        owner: String,
        repository: String
    ): Result<List<GithubPullRequest>>

    suspend fun getComments(
        owner: String, repository: String, pullNumber: String
    ): Result<List<GithubComment>>

    suspend fun postComment(
        owner: String, repository: String, pullNumber: String,
        comment: GithubComment
    ): Result<ResponseBody>


}