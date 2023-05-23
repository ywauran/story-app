package com.ywauran.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.ywauran.storyapp.ui.main.MainActivity
import com.ywauran.storyapp.databinding.ActivityLoginBinding
import com.ywauran.storyapp.ui.auth.AuthViewModel
import com.ywauran.storyapp.ui.auth.register.RegisterActivity
import com.ywauran.storyapp.helper.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        hideActionBar()
        setLoading(false)

        playAnimation()

        binding.edEmail.addTextChangedListener(watcher())
        binding.edPassword.addTextChangedListener(watcher())

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnSignin.setOnClickListener{
            login()
        }

    }

    private fun hideActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    private fun playAnimation() {
        with(binding) {
            val animationDuration = 500L
            val titleLoginAnimation = ObjectAnimator.ofFloat(tvTitleLogin, View.ALPHA, 1f).setDuration(animationDuration)
            val titleEmailAnimation = ObjectAnimator.ofFloat(tvTitleEmail, View.ALPHA, 1f).setDuration(animationDuration)
            val edEmailAnimation = ObjectAnimator.ofFloat(edEmail, View.ALPHA, 1f).setDuration(animationDuration)
            val titlePassAnimation = ObjectAnimator.ofFloat(tvTitlePassword, View.ALPHA, 1f).setDuration(animationDuration)
            val edPassAnimation = ObjectAnimator.ofFloat(edPassword, View.ALPHA, 1f).setDuration(animationDuration)
            val btnSigninAnimation = ObjectAnimator.ofFloat(btnSignin, View.ALPHA, 1f).setDuration(animationDuration)
            val titleOrAnimation = ObjectAnimator.ofFloat(tvOr, View.ALPHA, 1f).setDuration(animationDuration)
            val titleRegisterAnimation = ObjectAnimator.ofFloat(tvRegister, View.ALPHA, 1f).setDuration(animationDuration)

            AnimatorSet().apply {
                playSequentially(
                    titleLoginAnimation,
                    titleEmailAnimation,
                    edEmailAnimation,
                    titlePassAnimation,
                    edPassAnimation,
                    btnSigninAnimation,
                    titleOrAnimation,
                    titleRegisterAnimation
                )
                start()
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun login() {
        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()
        binding.btnSignin.isEnabled = false
        authViewModel.handleLogin(email, password).observe(this) { loginResult ->

            when (loginResult) {
                is Result.Loading -> {
                    setLoading(true)

                }
                is Result.Success -> {
                    setLoading(false)
                    val token = loginResult.data?.loginResult?.token
                    if (token.isNullOrBlank()) {
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                    saveAuthToken(token)
                    navigateToMainScreen()
                    finish()
                }
                else -> {
                    setLoading(false)
                    if (loginResult.message.equals("User not found")) {
                        binding.edEmail.error = loginResult.message
                    } else if (loginResult.message.equals("Invalid password")) {
                        binding.edPassword.error = loginResult.message
                    }
                    Toast.makeText(this@LoginActivity, loginResult.message ?: "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveAuthToken(token: String?) {
        PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
            .edit()
            .putString("PREF_TOKEN", token)
            .apply()
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun watcher() : TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = binding.edEmail.text.toString().trim()
                val password = binding.edPassword.text.toString().trim()

                val allFieldsFilled = email.isNotEmpty() &&
                        password.isNotEmpty()

                val noErrors = binding.edEmail.error == null &&
                        binding.edPassword.error == null

                binding.btnSignin.isEnabled = allFieldsFilled && noErrors
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}