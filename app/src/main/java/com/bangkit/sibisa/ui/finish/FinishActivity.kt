package com.bangkit.sibisa.ui.finish

import android.graphics.drawable.ColorDrawable
import android.os.Build
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
        fromLevel = intent.getIntExtra(FROM_LEVEL, 1)

        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.setBackgroundDrawable(ColorDrawable(getColor(R.color.riviera_paradise)))
            }

            // logo for action bar
            it.setDisplayShowCustomEnabled(true)
            val view = layoutInflater.inflate(R.layout.custom_image, null)
            it.customView = view
        }

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

    private fun updateExp(){

    }

    private fun updateLevel(){

    }

    companion object {
        const val IS_SUCCESS = "success"
        const val FROM_LEVEL = "0"
    }
}