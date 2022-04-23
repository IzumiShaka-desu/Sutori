package com.darkshandev.sutori.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.formatDate
import com.darkshandev.sutori.databinding.ItemListBinding

class StoryPagedListAdapter(private val listener: Listener) :
    PagingDataAdapter<Story, StoryPagedListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.binding.story = story
            holder.binding.date = story.formatDate()
            holder.binding.root.setOnClickListener {
                listener.onItemClickListener(
                    it,
                    story,
                    sharedView = holder.binding.itemData
                )
            }
        }
    }

    inner class MyViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    interface Listener {
        fun onItemClickListener(view: View, story: Story, sharedView: View? = null)
    }
}