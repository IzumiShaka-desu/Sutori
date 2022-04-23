package com.darkshandev.sutori.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.formatDate
import com.darkshandev.sutori.databinding.ItemListBinding
import com.darkshandev.sutori.utils.StoryDiffUtils

class MainListAdapter(private val listener: Listener) :
    RecyclerView.Adapter<MainListAdapter.ViewHolder>() {

    private var storyList = emptyList<Story>()

    fun updateList(newList: List<Story>) {
        val diff = DiffUtil
            .calculateDiff(StoryDiffUtils(storyList, newList))
        this.storyList = newList

        diff.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.story = storyList[position]
        holder.binding.date = storyList[position].formatDate()
        holder.binding.root.setOnClickListener {
            listener.onItemClickListener(
                it,
                storyList[position],
                sharedView = holder.binding.itemData
            )
        }
    }

    override fun getItemCount(): Int = storyList.size

    interface Listener {
        fun onItemClickListener(view: View, story: Story, sharedView: View? = null)
    }
}