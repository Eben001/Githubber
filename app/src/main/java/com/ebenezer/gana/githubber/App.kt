package com.ebenezer.gana.githubber

import android.app.Application
import com.ebenezer.gana.githubber.data.model.repository.GithubRepositoryImpl
import com.ebenezer.gana.githubber.data.networking.RetrofitBuilder

class App : Application() {

    companion object {

        private val githubService by lazy { RetrofitBuilder.buildApiService() }


        val repository by lazy {
            GithubRepositoryImpl(githubService)
        }


    }


}