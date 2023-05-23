package com.ywauran.storyapp.ui.auth.register

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.ywauran.storyapp.databinding.ActivityRegisterBinding
import com.ywauran.storyapp.ui.auth.AuthViewModel
import com.ywauran.storyapp.helper.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        hideActionBar()
        setLoading(false)

        binding.edName.addTextChangedListener(watcher())
        binding.edEmail.addTextChangedListener(watcher())
        binding.edPassword.addTextChangedListener(watcher())
        binding.edPasswordConfirm.addTextChangedListener(watcher())

        binding.btnRegister.setOnClickListener {
            register()
        }

        binding.tvLogin.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun register() {
        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()
        val confirmPassword = binding.edPasswordConfirm.text.toString()

        if (password != confirmPassword) {
            binding.edPasswordConfirm.error = "Passwords do not match"
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        authViewModel.handleRegister(name, email, password).observe(this) { registerResult ->
            when (registerResult) {
                is Result.Loading -> setLoading(true)
                is Result.Success -> {
                    setLoading(false)
                    Toast.makeText(this, "Success register", Toast.LENGTH_LONG).show()
                    finish()
                }
                else -> {
                    setLoading(false)
                    if (registerResult.message.equals("Email is already taken")) {
                        binding.edEmail.error = registerResult.message
                    }
                    Toast.makeText(this, registerResult.message ?: "Error register", Toast.LENGTH_LONG).show()
                }
            }

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

    private fun watcher() : TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = binding.edEmail.text.toString().trim()
                val password = binding.edPassword.text.toString().trim()
                val passwordConfirm = binding.edPasswordConfirm.text.toString().trim()
                val name = binding.edName.text.toString().trim()

                val allFieldsFilled = email.isNotEmpty() &&
                        password.isNotEmpty() &&
                        passwordConfirm.isNotEmpty() &&
                        name.isNotEmpty()

                val noErrors = binding.edEmail.error == null &&
                        binding.edPassword.error == null

                binding.btnRegister.isEnabled = allFieldsFilled && noErrors
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}