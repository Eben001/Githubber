package com.ebenezer.gana.githubber.data.networking

import com.ebenezer.gana.githubber.data.model.repository.GithubRepository
import com.ebenezer.gana.githubber.data.model.repository.GithubRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Builds Retrofit
 */

object RetrofitBuilder {

    private const val BASE_URL = "https://api.github.com/"
    private const val HEADER_AUTHORIZATION = "Authorization"

    private var okHttpClient = OkHttpClient.Builder()
    private fun buildClient() =
        okHttpClient
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY

            })
            .build()

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }


    fun buildApiService(): GithubAPI =
        buildRetrofit().create(GithubAPI::class.java)


    // Gets the token and adds it as Authentication token for every request
    fun getAuthorizedToken(token: String): GithubRepository {
        okHttpClient = OkHttpClient.Builder()

        okHttpClient.addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader(HEADER_AUTHORIZATION, "token $token")
                .build()
            chain.proceed(newRequest)
        }
        return GithubRepositoryImpl(buildApiService())

    }
}
