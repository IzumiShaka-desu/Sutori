package com.darkshandev.sutori.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


object AlertUtils {
    fun showPickerDialg(
        context: Context?,
        title: String?,
        items: Array<String?>?,
        callback: DialogInterface.OnClickListener?
    ) {
        if (items == null || context == null) {
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setCancelable(true)
            .setItems(items, callback)
            .create()
            .show()
    }
}