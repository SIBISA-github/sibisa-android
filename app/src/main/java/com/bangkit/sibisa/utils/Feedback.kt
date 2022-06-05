package com.bangkit.sibisa.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import com.bangkit.sibisa.R

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

fun showHelpDialog(context: Context, titleText: String, textText: String) {
    val dialog = Dialog(context)

    dialog.setContentView(R.layout.custom_alert_dialog)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val title = dialog.findViewById<TextView>(R.id.alertTitle)
    title?.text = titleText

    val text = dialog.findViewById<TextView>(R.id.alertText)
    text?.text = textText

    val button = dialog.findViewById<Button>(R.id.alertOK)
    button.setOnClickListener {
        dialog.dismiss()
    }

    val iconButton = dialog.findViewById<ImageView>(R.id.alertClose)
    iconButton.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}