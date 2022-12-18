package dev.novad.repoexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.novad.repoexplorer.databinding.FragmentRepoListBinding

@AndroidEntryPoint
class RepoListFragment : Fragment() {

    private var _binding: FragmentRepoListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: RepoListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiRepoListState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ViewState.Error -> TODO()
                is ViewState.Loading -> TODO()
                is ViewState.Success -> TODO()
            }
        })
        viewModel.getRepositoriesList("tetris")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
