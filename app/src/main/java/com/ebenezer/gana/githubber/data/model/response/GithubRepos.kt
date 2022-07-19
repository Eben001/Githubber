package com.ebenezer.gana.githubber.data.model.response

data class GithubRepos(
    val name:String?,
    val url:String?,
    val owner:GithubUser
){
    override fun toString() = "$name - $url"
}