package com.ebenezer.gana.githubber.data.repository

import com.ebenezer.gana.githubber.data.model.Failure
import com.ebenezer.gana.githubber.data.model.Result
import com.ebenezer.gana.githubber.data.model.Success
import com.ebenezer.gana.githubber.data.model.response.GithubComment
import com.ebenezer.gana.githubber.data.model.response.GithubPullRequest
import com.ebenezer.gana.githubber.data.model.response.GithubRepos
import com.ebenezer.gana.githubber.data.model.response.GithubToken
import com.ebenezer.gana.githubber.data.networking.GithubAPI
import okhttp3.ResponseBody

class GithubRepositoryImpl(private val githubAPI: GithubAPI) : GithubRepository {


    override suspend fun getAuthToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<GithubToken> = try {
        val data = githubAPI.getAuthenticationToken(clientId, clientSecret, code)
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }

    override suspend fun getAllGithubRepository(): Result<List<GithubRepos>> = try {
        val data = githubAPI.getAllRepository()
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }

    override suspend fun getPullRequest(
        owner: String,
        repository: String
    ): Result<List<GithubPullRequest>> = try {
        val data = githubAPI.getPullRequest(owner, repository)
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }


    override suspend fun getComments(
        owner: String,
        repository: String,
        pullNumber: String
    ): Result<List<GithubComment>> = try {
        val data = githubAPI.getComments(owner, repository, pullNumber)
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }


    override suspend fun postComment(
        owner: String,
        repository: String,
        pullNumber: String,
        comment: GithubComment
    ): Result<ResponseBody> = try {
        val data = githubAPI.postComment(owner, repository, pullNumber, comment)
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }
}