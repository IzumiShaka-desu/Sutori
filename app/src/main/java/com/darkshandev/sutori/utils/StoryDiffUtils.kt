package com.darkshandev.sutori.utils

import androidx.recyclerview.widget.DiffUtil
import com.darkshandev.sutori.data.models.Story

class StoryDiffUtils(private val oldList: List<Story>, private val newList: List<Story>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList == newList

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val latest = newList[newItemPosition]
        return when (old.id) {
            latest.id -> true
            else -> false
        }
    }
}