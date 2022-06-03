package com.bangkit.sibisa.ui.finish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.sibisa.R
import kotlin.properties.Delegates

class FinishActivity : AppCompatActivity() {
    private var isSuccess by Delegates.notNull<Boolean>()
    private var fromLevel by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        isSuccess = intent.getBooleanExtra(IS_SUCCESS, true)
        fromLevel = intent.getIntExtra(IS_SUCCESS, 1)

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