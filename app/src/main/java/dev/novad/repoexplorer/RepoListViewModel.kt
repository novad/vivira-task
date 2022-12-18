package dev.novad.repoexplorer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.novad.repoexplorer.model.Repository
import dev.novad.repoexplorer.repository.DataRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoListViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {

    private val mutableUiRepoList =
        MutableLiveData<ViewState<List<Repository>>>(ViewState.Success(null))
    val uiRepoListState = mutableUiRepoList

    var pageNumber = 0
        get() = field
        private set(value) {
            field = value
        }

    fun getRepositoriesList(query: String) {
        mutableUiRepoList.value = ViewState.Loading(uiRepoListState.value?.dataOrNull())
        viewModelScope.launch {
            val result = repository.getRepositoriesList(query, page = pageNumber + 1)
            mutableUiRepoList.postValue(
                if (result.isSuccess) {
                    val newRepositories = result.getOrNull()?.items ?: emptyList()
                    if (newRepositories.isNotEmpty()) {
                        pageNumber++
                    }
                    ViewState.Success(newRepositories)
                } else {
                    ViewState.Error(result.exceptionOrNull(), uiRepoListState.value?.dataOrNull())
                }
            )
        }
    }
}
