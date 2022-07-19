package com.ebenezer.gana.githubber.data.model.response

import com.google.gson.annotations.SerializedName

data class GithubToken(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("token_type")
    val tokenType: String?
)