package com.bangkit.sibisa.ui.finish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.sibisa.R

class FinishActivity : AppCompatActivity() {
    private val isSuccess = intent.getBooleanExtra(IS_SUCCESS, true)
    private val fromLevel = intent.getIntExtra(IS_SUCCESS, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        setupUI()
    }

    private fun setupUI() {
        if (isSuccess) {
            // send API update exp
            // display congrats page
        } else {
            // display failed page
        }
    }

    companion object {
        const val IS_SUCCESS = "success"
        const val FROM_LEVEL = "0"
    }
}