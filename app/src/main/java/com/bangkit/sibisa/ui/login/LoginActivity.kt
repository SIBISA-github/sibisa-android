package com.bangkit.sibisa.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}