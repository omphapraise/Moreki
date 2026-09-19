package com.cubiccode.moreki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cubiccode.moreki.api.RetrofitClient
import com.cubiccode.moreki.databinding.ActivityRegisterBinding
import com.cubiccode.moreki.models.AuthResponse
import com.cubiccode.moreki.models.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener { attemptRegister() }
        binding.goToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun attemptRegister() {
        binding.emailError.visibility = View.GONE
        binding.passwordError.visibility = View.GONE

        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        var valid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailError.text = "Enter a valid email"
            binding.emailError.visibility = View.VISIBLE
            valid = false
        }

        if (password.length < 6) {
            binding.passwordError.text = "Password must be at least 6 characters"
            binding.passwordError.visibility = View.VISIBLE
            valid = false
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            valid = false
        }

        if (!valid) return

        binding.registerButton.isEnabled = false

        val call = RetrofitClient.apiService.register(RegisterRequest(name, email, password))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                binding.registerButton.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("RegisterActivity", "Registered $email via API")
                    Toast.makeText(this@RegisterActivity, "Account created! Please log in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val msg = response.body()?.message ?: "Registration failed"
                    Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                binding.registerButton.isEnabled = true
                Log.e("RegisterActivity", "API failure: ${t.message}")
                Toast.makeText(this@RegisterActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_LONG).show()
            }
        })
    }
}