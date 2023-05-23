package com.ywauran.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ywauran.storyapp.R
import com.ywauran.storyapp.data.remote.response.StoryResponse
import com.ywauran.storyapp.databinding.ItemStoryBinding
import com.ywauran.storyapp.helper.changeFormatDate
import javax.inject.Inject


class StoryAdapter @Inject constructor(): PagingDataAdapter<StoryResponse, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse>(){
            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    private lateinit var onItemClickListener: OnItemClickListener


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bindData(getItem(position) ?: StoryResponse())
    }

    inner class StoryViewHolder(private val view: ItemStoryBinding) : RecyclerView.ViewHolder(view.root) {
        fun bindData(story: StoryResponse) {
            view.tvDate.text = story.createdAt?.let { changeFormatDate(it) }
            view.tvDescription.text = story.description
            view.tvCreatedBy.text = story.name

            Glide.with(view.root)
                .load(story.photoUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.ivStory)

            view.cvItemStory.setOnClickListener {
                onItemClickListener.onItemClicked(story.id as String)
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClicked(id: String)
    }

}