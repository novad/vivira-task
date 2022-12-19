package dev.novad.repoexplorer


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.novad.repoexplorer.databinding.ItemRepositoryBinding
import dev.novad.repoexplorer.model.Repository

class RepoAdapter : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    private val repoItems = ArrayList<Repository>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding =
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)
    }

    override fun getItemCount() = repoItems.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repoItems[position])
    }

    fun setNewRepoList(newListRepoItems: List<Repository>) {
        val previousSize = repoItems.size
        repoItems.addAll(newListRepoItems)
        notifyItemRangeInserted(previousSize, newListRepoItems.size)
    }

    fun clear() {
        repoItems.clear()
        notifyDataSetChanged()
    }

    class RepoViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repository) {
            binding.apply {
                tvRepoName.text = repo.name
                tvRepoOwner.text = repo.owner.login
                tvRepoUrl.text = repo.html_url
                tvDescription.text = repo.description
                Glide.with(binding.root)
                    .load(repo.owner.avatar_url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_faces)
                    .into(imageView)
            }
        }
    }
}
