package com.ebenezer.gana.githubber.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebenezer.gana.githubber.App.Companion.repository
import com.ebenezer.gana.githubber.R
import com.ebenezer.gana.githubber.data.model.Failure
import com.ebenezer.gana.githubber.data.model.Success
import com.ebenezer.gana.githubber.data.model.response.GithubComment
import com.ebenezer.gana.githubber.data.model.response.GithubPullRequest
import com.ebenezer.gana.githubber.data.model.response.GithubRepos
import com.ebenezer.gana.githubber.data.networking.RetrofitBuilder.getAuthorizedToken
import com.ebenezer.gana.githubber.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> = _token

    private val _error = MutableLiveData<UiText>()
    val error: LiveData<UiText> = _error

    private val _githubRepo = MutableLiveData<List<GithubRepos>>()
    val githubRepo: LiveData<List<GithubRepos>> = _githubRepo

    private val _pullRequests = MutableLiveData<List<GithubPullRequest>>()
    val pullRequests: LiveData<List<GithubPullRequest>> = _pullRequests

    private val _comments = MutableLiveData<List<GithubComment>>()
    val comments: LiveData<List<GithubComment>> = _comments

    private val _isCommentPosted = MutableLiveData<Boolean>()
    val isCommentPosted: LiveData<Boolean> = _isCommentPosted


    /**
     * A function to create a comment
     * @param token authentication token
     * @param repository the name of the repository
     * @param pullNumber the number that identifies the issue.
     * @param content the contents of the comment.
     *
     */
    fun postComment(
        token: String,
        repository: GithubRepos,
        pullNumber: String?,
        content: GithubComment
    ) = try {
        if (repository.owner.login != null && repository.name != null && pullNumber != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = getAuthorizedToken(token)
                    .postComment(
                        repository.owner.login,
                        repository.name, pullNumber, content
                    )
                when (result) {

                    is Success -> withContext(Dispatchers.Main) {
                        _isCommentPosted.value = true
                    }
                    else -> withContext(Dispatchers.Main) {
                        _isCommentPosted.value = false
                    }

                }

            }
        } else {
            _error.value = UiText.StringResource(R.string.err_postin_comments)
        }
    } catch (error: Throwable) {
        error.printStackTrace()
    }

    /**
     * A function for retrieving list of comments for an issue
     * @param token authentication token
     * @param owner the account owner of the repository
     * @param repository the name of the repository
     * @param pullNumber the number that identifies the issue
     */
    fun loadComments(token: String, owner: String?, repository: String?, pullNumber: String?) =
        try {
            if (owner != null && repository != null && pullNumber != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = getAuthorizedToken(token)
                        .getComments(owner, repository, pullNumber)
                    when (result) {
                        is Success -> {
                            withContext(Dispatchers.Main) {
                                _comments.value = result.data
                            }
                        }
                        else -> {
                            withContext(Dispatchers.Main) {
                                _error.value =
                                    UiText.StringResource(R.string.err_unable_to_load_comment)
                            }
                        }
                    }
                }
            } else {
                _error.value = UiText.StringResource(R.string.err_unable_to_load_comment)
            }
        } catch (error: Throwable) {
            error.printStackTrace()
        }


    /**
     * Gets list of pull request for the selected repository
     * @param token The authentication token
     * @param owner The account owner of the repository
     * @param repository The name of the repository
     * @exception Exception returns error message for this exception
     */
    fun loadPullRequests(token: String, owner: String?, repository: String?) = try {
        if (owner != null && repository != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = getAuthorizedToken(token)
                    .getPullRequest(owner, repository)

                when (result) {
                    is Success -> {
                        withContext(Dispatchers.Main) {
                            _pullRequests.value = result.data
                        }

                    }
                    is Failure -> {
                        when (result.error) {
                            is Exception -> {
                                withContext(Dispatchers.Main) {
                                    _error.value =
                                        UiText.StringResource(R.string.err_failed_to_load_pull)
                                }
                            }
                        }

                    }
                }

            }
        } else {
            _error.value = UiText.StringResource(R.string.err_empty_repo)
        }

    } catch (error: Throwable) {
        error.printStackTrace()
    }

    /**
     * Function to get list of repository for an authenticated user
     * @param token The authentication token
     * @exception Exception returns error message for this exception
     *
     */
    fun loadRepository(token: String) = try {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getAuthorizedToken(token)
                .getAllGithubRepository()

            when (result) {
                is Success -> {
                    withContext(Dispatchers.Main) {
                        _githubRepo.value = result.data
                    }
                }
                is Failure -> {
                    when (result.error) {
                        is Exception -> {
                            withContext(Dispatchers.Main) {
                                _error.value =
                                    UiText.StringResource(R.string.err_failed_to_load_github_repo)
                            }
                        }
                    }

                }
            }
        }
    } catch (error: Throwable) {
        error.printStackTrace()
    }

    /**
     * Gets token that will be used for every call
     * @param clientId The OAuth app client key for which to create the token.  Gotten from github
     * @param clientSecret The OAuth app client secret for which to create the token. Gotten from github
     * @param code Code received after the user grants access permission
     * @exception Exception returns error message for this exception
     */
    fun getToken(clientId: String, clientSecret: String, code: String) = try {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.getAuthToken(clientId, clientSecret, code)) {
                is Success -> {
                    withContext(Dispatchers.Main) {
                        _token.value = result.data.accessToken
                    }
                }

                is Failure -> {
                    when (result.error) {
                        is Exception -> {
                            _error.value = UiText.StringResource(R.string.err_failed_to_load_token)

                        }
                    }
                }

            }
        }

    } catch (error: Throwable) {
        error.printStackTrace()
    }

}