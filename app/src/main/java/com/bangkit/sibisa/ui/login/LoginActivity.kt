package com.bangkit.sibisa.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.MainActivity
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityLoginBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.utils.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(this))[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text?.trim().toString()
            val pass = binding.passwordEditText.text?.trim().toString()

            viewModel.login(email, pass).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is NetworkResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is NetworkResult.Success -> {
                            binding.progressBar.visibility = View.GONE

                            val pref = UserPreference(applicationContext)
                            pref.setToken(result.data!!)

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        is NetworkResult.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(this, result.error)
                        }
                    }
                }
            }
        }
    }
}