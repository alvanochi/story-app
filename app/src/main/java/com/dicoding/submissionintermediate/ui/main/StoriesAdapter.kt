package com.dicoding.submissionintermediate.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submissionintermediate.data.retrofit.response.ListStoryItem
import com.dicoding.submissionintermediate.databinding.ItemStoryBinding
import com.dicoding.submissionintermediate.ui.detail.DetailActivity
import com.dicoding.submissionintermediate.ui.detail.DetailActivity.Companion.ID_STORY


class StoriesAdapter: ListAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {
    inner class MyViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(listStories: ListStoryItem){
            Glide.with(binding.ivItemPhoto.context)
                .load(listStories.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = listStories.name
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra(ID_STORY, listStories.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.ivItemPhoto, "photo"),
                        Pair(binding.tvItemName, "name"),
                    )

                binding.root.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemStory = getItem(position)
        holder.bind(itemStory)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
