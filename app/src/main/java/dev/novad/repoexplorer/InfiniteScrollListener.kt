package dev.novad.repoexplorer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class InfiniteScrollListener(private val linearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var isLoading = false

    companion object {
        private const val THRESHOLD = 5
    }

    abstract fun loadMore()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) { // ignore scrolling up
            return
        }
        if (isLoading) {
            return
        }
        val visibleItemCount = recyclerView.childCount
        val currentItemCount = linearLayoutManager.itemCount

        if (currentItemCount > visibleItemCount &&
            currentItemCount - visibleItemCount <=
            (linearLayoutManager.findFirstVisibleItemPosition() + THRESHOLD)
        ) {
            loadMore()
            isLoading = true
        }
    }

    fun onLoadingFinished() {
        isLoading = false
    }
}
