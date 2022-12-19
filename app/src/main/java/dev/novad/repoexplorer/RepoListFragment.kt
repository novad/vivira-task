package dev.novad.repoexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.novad.repoexplorer.databinding.FragmentRepoListBinding
import dev.novad.repoexplorer.uicommons.ViewState

@AndroidEntryPoint
class RepoListFragment : Fragment() {

    private var _binding: FragmentRepoListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: RepoListViewModel by viewModels()

    private val reposAdapter = RepoAdapter()
    private lateinit var scrollListener: InfiniteScrollListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiRepoListState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ViewState.Error -> {
                    binding.apply {
                        progressBar.isVisible = false
                        repositoriesRecyclerView.isEnabled = true
                        fab.isEnabled = true
                        emptyStateText.isVisible = false

                    }
                }
                is ViewState.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        repositoriesRecyclerView.isEnabled = false
                        fab.isEnabled = false
                        emptyStateText.isVisible = false
                    }
                }
                is ViewState.Success -> {
                    scrollListener.onLoadingFinished()

                    binding.apply {
                        progressBar.isVisible = false
                        repositoriesRecyclerView.isEnabled = true
                        fab.isEnabled = true

                        when (it.data.isNullOrEmpty()) {
                            true -> {
                                emptyStateText.isVisible = true
                                repositoriesRecyclerView.isVisible = false
                            }
                            false -> {
                                emptyStateText.isVisible = false
                                repositoriesRecyclerView.isVisible = true
                                reposAdapter.setNewRepoList(it.data)
                            }
                        }
                    }
                }
            }
        })
        binding.repositoriesRecyclerView.adapter = reposAdapter
        binding.fab.setOnClickListener {
            val searchText = binding.editText.editableText.toString()
            reposAdapter.clear()
            if (searchText.isEmpty()) {
                showEmptyQueryError()
                return@setOnClickListener
            }
            viewModel.getRepositoriesListForQuery(searchText)
        }

        scrollListener = object :
            InfiniteScrollListener(binding.repositoriesRecyclerView.layoutManager as LinearLayoutManager) {
            override fun loadMore() {
                viewModel.getRepositoriesList()
            }
        }
        binding.repositoriesRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun showEmptyQueryError() {
        view?.let {
            Snackbar.make(it, getString(R.string.empty_result_text), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
