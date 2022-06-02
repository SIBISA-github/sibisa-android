package com.bangkit.sibisa.utils

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

fun showLoading(isLoading: Boolean, progressBar: ProgressBar) {
    if (isLoading) {
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.GONE
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}