package com.bangkit.sibisa.ui.custom

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.CustomAlertDialogBinding


class CustomAlertDialog constructor(context: Context?) : AlertDialog(context) {
    private lateinit var binding: CustomAlertDialogBinding
    private fun initialize() {
        binding = CustomAlertDialogBinding.inflate(layoutInflater)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setView(binding.root)
    }

    interface DialogActionListener {
        fun onAction(viewId: View?)
    }

//    fun setOnActionListener(dialogActionListener: DialogActionListener) {
//        dialogActionListener = dialogActionListener
//    }

    init {
        initialize()
    }
}