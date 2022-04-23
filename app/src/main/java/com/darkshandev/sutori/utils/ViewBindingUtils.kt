package com.darkshandev.sutori.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["resname", "isExpand"], requireAll = false)
fun loadImageDrawable(view: ImageView, name: String?, isExpand: Boolean?) {
    name?.let {
        view.loadCircleImage(it, isExpand = isExpand ?: false)
    }
}