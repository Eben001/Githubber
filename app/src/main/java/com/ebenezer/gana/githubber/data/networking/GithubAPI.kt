package com.ebenezer.gana.githubber.data.networking

import com.ebenezer.gana.githubber.data.model.response.GithubComment
import com.ebenezer.gana.githubber.data.model.response.GithubPullRequest
import com.ebenezer.gana.githubber.data.model.response.GithubRepos
import com.ebenezer.gana.githubber.data.model.response.GithubToken
import okhttp3.ResponseBody
import retrofit2.http.*

interface GithubAPI {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token")
   suspend fun getAuthenticationToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): GithubToken


    @GET("user/repos")
    suspend fun getAllRepository(): List<GithubRepos>

    @GET("/repos/{owner}/{repo}/pulls")
    suspend fun getPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repository: String,
    ): List<GithubPullRequest>

    @GET("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun getComments(
        @Path("owner") owner: String,
        @Path("repo") repository: String,
        @Path("issue_number") pullNumber: String
    ): List<GithubComment>

    @POST("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun postComment(
        @Path("owner") owner: String,
        @Path("repo") repository: String,
        @Path("issue_number") pullNumber: String,
        @Body comment: GithubComment
    ): ResponseBody

}