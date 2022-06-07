package com.bangkit.sibisa.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.sibisa.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        CoroutineScope(Dispatchers.Main).launch {
            waitThenToNextActivity()
        }
    }

    private suspend fun waitThenToNextActivity() {
        delay(3000L)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}