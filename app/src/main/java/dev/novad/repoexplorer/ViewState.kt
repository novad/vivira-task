package dev.novad.repoexplorer

sealed class ViewState<out T> {
    class Loading<T>(val data: T?) : ViewState<T>()
    class Success<T>(val data: T?) : ViewState<T>()
    class Error<T>(val throwable: Throwable? = null, val data: T?) : ViewState<T>()

    fun dataOrNull(): T? {
        return when (this) {
            is Success -> data
            is Error -> data
            is Loading -> data
        }
    }
}
