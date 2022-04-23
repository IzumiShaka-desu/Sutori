package com.darkshandev.sutori.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.darkshandev.sutori.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker


fun ImageView.loadCircleImage(url: String, isExpand: Boolean = false) {
    val defaultOption = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .error(R.drawable.ic_baseline_broken_image_24)
    val requestBuilder: RequestBuilder<Drawable> = Glide.with(context)
        .asDrawable().sizeMultiplier(0.5f)
    if (url.isEmpty()) {
        Glide.with(context)
            .setDefaultRequestOptions(defaultOption)
            .load(
                ColorDrawable(ContextCompat.getColor(context, R.color.grey))
            ).thumbnail(requestBuilder)
            .into(this)
    } else {
        val builder = Glide.with(context)
            .setDefaultRequestOptions(defaultOption)
            .load(
                url
            ).thumbnail(requestBuilder)

        if (isExpand) {
            builder.into(this)
        } else {
            builder.centerCrop().into(this)

        }
    }
}

fun Marker.loadIcon(context: Context, url: String?) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .error(R.drawable.ic_baseline_pin_drop_24) // to show a default icon in case of any errors
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return resource?.let {
                    BitmapDescriptorFactory.fromBitmap(it)
                }?.let {
                    setIcon(it); true
                } ?: false
            }
        }).submit()
}