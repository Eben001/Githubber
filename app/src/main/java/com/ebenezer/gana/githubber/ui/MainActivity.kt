package com.ebenezer.gana.githubber.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.ebenezer.gana.githubber.BuildConfig
import com.ebenezer.gana.githubber.R
import com.ebenezer.gana.githubber.data.model.response.GithubComment
import com.ebenezer.gana.githubber.data.model.response.GithubPullRequest
import com.ebenezer.gana.githubber.data.model.response.GithubRepos
import com.ebenezer.gana.githubber.databinding.ActivityMainBinding
import com.ebenezer.gana.githubber.prefsStore.TokenPreferences
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenPrefs: TokenPreferences
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenPrefs = TokenPreferences(this)
        observeViewModels()
        setOnClickListener()
        initializeSpinners()

        binding.repositoriesSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //Load pullRequests for the selected repository
                    if (parent?.selectedItem is GithubRepos) {
                        val currentRepository = parent.selectedItem as GithubRepos
                        tokenPrefs.tokenPrefs.asLiveData().observe(this@MainActivity) {
                            it?.let { token ->
                                viewModel.loadPullRequests(
                                    token,
                                    currentRepository.owner.login,
                                    currentRepository.name
                                )
                            }

                        }
                    }

                }

            }

        binding.prsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Load comment for the selected pull request item
                if (parent?.selectedItem is GithubPullRequest) {
                    val githubPR = parent.selectedItem as GithubPullRequest
                    val currentRepository = binding.repositoriesSpinner.selectedItem as GithubRepos

                    //use token from prefsStore
                    tokenPrefs.tokenPrefs.asLiveData().observe(this@MainActivity) { token ->
                        token?.let {
                            viewModel.loadComments(
                                it, githubPR.user?.login,
                                currentRepository.name,
                                githubPR.number
                            )
                        }

                    }
                }

            }
        }
    }

    private fun initializeSpinners() {

        binding.repositoriesSpinner.isEnabled = false
        binding.repositoriesSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf(resources.getString(R.string.no_repo_available))
        )

        binding.prsSpinner.isEnabled = false
        binding.prsSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            arrayListOf(resources.getString(R.string.select_a_repo))
        )

        binding.commentsSpinner.isEnabled = false
        binding.commentsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf(resources.getString(R.string.select_pull_request))
        )
    }

    private fun observeViewModels() {
        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this@MainActivity, errorMessage.asString(this), Toast.LENGTH_SHORT).show()
        }

        viewModel.isCommentPosted.observe(this) { isCommentPosted ->
            if (isCommentPosted) {
                binding.commentEditText.text?.clear()
                Toast.makeText(this@MainActivity, resources.getString(R.string.text_comment_created), Toast.LENGTH_SHORT).show()
                tokenPrefs.tokenPrefs.asLiveData().observe(this) { token ->
                    token?.let {
                        val currentRepository =
                            binding.repositoriesSpinner.selectedItem as GithubRepos
                        val currentPullRequest =
                            binding.prsSpinner.selectedItem as GithubPullRequest
                        viewModel.loadComments(
                            it, currentRepository.owner.login,
                            currentRepository.name, currentPullRequest.number
                        )
                    }

                }
            } else {
                Toast.makeText(this@MainActivity, resources.getString(R.string.failed_to_create_comment), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.comments.observe(this) { comments ->
            if (!comments.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                    comments
                )
                binding.commentsSpinner.adapter = spinnerAdapter
                binding.commentsSpinner.isEnabled = true
                binding.commentEditText.isEnabled = true
                binding.postCommentButton.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf(resources.getString(R.string.no_comment_for_pull_request))
                )
                binding.commentsSpinner.adapter = spinnerAdapter
                binding.commentEditText.isEnabled = false
                binding.postCommentButton.isEnabled = false
            }
        }

        viewModel.pullRequests.observe(this) { pullRequestList ->
            if (!pullRequestList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    pullRequestList
                )
                binding.prsSpinner.adapter = spinnerAdapter
                binding.prsSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf(resources.getString(R.string.repository_has_no_pulls))
                )
                binding.prsSpinner.adapter = spinnerAdapter
                binding.prsSpinner.isEnabled = false
                binding.commentsSpinner.isEnabled = false
                binding.commentEditText.isEnabled = false
                binding.postCommentButton.isEnabled = false
            }
        }

        viewModel.githubRepo.observe(this) { reposList ->
            if (!reposList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    reposList
                )
                binding.repositoriesSpinner.adapter = spinnerAdapter
                binding.repositoriesSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf(resources.getString(R.string.user_has_no_repository))
                )
                binding.repositoriesSpinner.adapter = spinnerAdapter
                binding.repositoriesSpinner.isEnabled = true

            }
        }

        viewModel.token.observe(this) { token ->
            if (token != null && token.isNotEmpty()) {
                lifecycleScope.launch { tokenPrefs.saveToken(token) }
                binding.loadReposButton.isEnabled = true
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.auth_success),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(this@MainActivity, resources.getString(R.string.auth_failed), Toast.LENGTH_SHORT)
                    .show()


            }
        }
    }

    private fun setOnClickListener() {
        binding.authenticateButton.setOnClickListener {
            val oathUrl = getString(R.string.oauthUrl)
            val clientId = BuildConfig.CLIENT_ID
            val callbackUrl = getString(R.string.callbackUrl)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("$oathUrl?client_id=$clientId&scope=repo&redirect_uri=$callbackUrl")
            )
            startActivity(intent)
        }

        binding.loadReposButton.setOnClickListener {
            //Load token from the preference Store
            tokenPrefs.tokenPrefs.asLiveData().observe(this) { token ->
                token?.let {
                    viewModel.loadRepository(it)
                }
            }
        }

        binding.postCommentButton.setOnClickListener {
            val comment = binding.commentEditText.text.toString()
            if (comment.isNotEmpty()) {
                val currentRepository = binding.repositoriesSpinner.selectedItem as GithubRepos
                val currentPullRequest = binding.prsSpinner.selectedItem as GithubPullRequest

                //Load token from the preferenceStore
                tokenPrefs.tokenPrefs.asLiveData().observe(this) { token ->
                    token?.let {
                        viewModel.postComment(
                            it,
                            currentRepository,
                            currentPullRequest.number,
                            GithubComment(null, comment)
                        )
                    }
                }

            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.enter_a_comment),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Gets the request code whether or not if the user granted or denied permission.
     * The permission code is used to make request to get the access token
     */
    override fun onResume() {
        super.onResume()
        val uri = intent.data
        val callbackUrl = getString(R.string.callbackUrl)
        if (uri != null && uri.toString().startsWith(callbackUrl)) {
            val code = uri.getQueryParameter("code")
            code?.let {
                val clientId = BuildConfig.CLIENT_ID
                val clientSecret = BuildConfig.CLIENT_SECRET
                viewModel.getToken(clientId, clientSecret, code)

            }
        }
    }
}