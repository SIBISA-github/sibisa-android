package com.bangkit.sibisa.ui.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
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
import org.json.JSONObject
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        supportActionBar?.hide()

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
                            binding.progressBar.bringToFront()
                        }
                        is NetworkResult.Success -> {
                            binding.progressBar.visibility = View.GONE

                            val pref = UserPreference(applicationContext)
                            val jwt = decodeToken(result.data!!)
                            val userID = JSONObject(jwt).getInt("id")
                            pref.setUserID(userID)
                            pref.setToken(result.data)

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        is NetworkResult.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(this, result.error.uppercase())
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

    private fun decodeToken(token: String): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return "Requires SDK 26"
        val parts = token.split(".")
        return try {
            val charset = charset("UTF-8")
            val header =
                String(Base64.getUrlDecoder().decode(parts[0].toByteArray(charset)), charset)
            val payload =
                String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset)
            header
            payload
        } catch (e: Exception) {
            "Error parsing JWT: $e"
        }

    }
}