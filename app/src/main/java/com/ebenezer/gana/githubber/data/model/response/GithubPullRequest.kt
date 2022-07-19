package com.ebenezer.gana.githubber.data.model.response

import com.google.gson.annotations.SerializedName

data class GithubPullRequest(
    val id:String?,
    val title:String?,
    val number:String?,

    @SerializedName("comments_url")
    val commentUrl:String?,
    val user: GithubUser?
){
    override fun toString() = "$title - $id"
}