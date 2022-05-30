package com.bangkit.sibisa.ui.register

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
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

        supportActionBar?.hide()

        setupUI()

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

    private fun setupUI() {
        val text = resources.getString(R.string.login_from_register_cta)
        val spannableString = SpannableString(text)
        val boldSpan = StyleSpan(Typeface.BOLD)
        val clickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }

        val startIndex = text.indexOf("here")
        val endIndex = startIndex + "here".length

        spannableString.setSpan(boldSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        with(binding.loginCtaTextView) {
            this.text = spannableString
            this.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}