package com.bangkit.sibisa.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.databinding.ActivityRegisterBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.ui.login.LoginActivity
import com.bangkit.sibisa.utils.showToast

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(this))[RegisterViewModel::class.java]

        binding.registButton.setOnClickListener {
            val name = binding.registName.text?.trim().toString()
            val username = binding.registUsername.text?.trim().toString()
            val email = binding.registEmail.text?.trim().toString()
            val pass = binding.registPassword.text?.trim().toString()

            viewModel.register(name = name, email = email, password = pass, username = username)
                .observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is NetworkResult.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is NetworkResult.Success -> {
                                binding.progressBar.visibility = View.GONE

                                Toast.makeText(this, "Register successful", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, LoginActivity::class.java))
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