package com.bangkit.sibisa.ui.login

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
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.ui.MainActivity
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityLoginBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.ui.register.RegisterActivity
import com.bangkit.sibisa.utils.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupUI()

        viewModel = ViewModelProvider(this, ViewModelFactory(this))[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.usernameEditText.text?.trim().toString()
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
                            showToast(this, "Login error, please try again")
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        val text = resources.getString(R.string.register_from_login_cta)
        val spannableString = SpannableString(text)
        val boldSpan = StyleSpan(Typeface.BOLD)
        val clickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
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