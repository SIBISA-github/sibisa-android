package com.bangkit.sibisa.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registButton.setOnClickListener {
            val name = binding.registName.text?.trim().toString()
            val email = binding.registEmail.text?.trim().toString()
            val password = binding.registPassword.text?.trim().toString()

//            viewModel.register(name, email, password).observe(this){ data ->
//                if (data != null) {
//
//                }
//
//            }
        }
    }
}