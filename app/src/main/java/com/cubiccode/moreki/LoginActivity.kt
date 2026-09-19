package com.cubiccode.moreki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cubiccode.moreki.api.RetrofitClient
import com.cubiccode.moreki.databinding.ActivityLoginBinding
import com.cubiccode.moreki.models.AuthResponse
import com.cubiccode.moreki.models.LoginRequest
import com.cubiccode.moreki.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        if (session.isLoggedIn()) {
            goToDashboard()
            return
        }

        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        binding.emailError.visibility = View.GONE
        binding.passwordError.visibility = View.GONE

        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        var valid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailError.text = "Enter a valid email"
            binding.emailError.visibility = View.VISIBLE
            valid = false
        }

        if (password.isEmpty()) {
            binding.passwordError.text = "Password is required"
            binding.passwordError.visibility = View.VISIBLE
            valid = false
        }

        if (!valid) return

        binding.loginButton.isEnabled = false

        val call = RetrofitClient.apiService.login(LoginRequest(email, password))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                binding.loginButton.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    Log.d("LoginActivity", "Login success for $email via API")

                    // Save session locally so the rest of the app (Dashboard, Expense, etc.) keeps working unchanged
                    session.setLoggedInUserFromApi(body.email ?: email, body.name ?: "")
                    goToDashboard()
                } else {
                    val msg = response.body()?.message ?: "Incorrect email or password"
                    Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                binding.loginButton.isEnabled = true
                Log.e("LoginActivity", "API failure: ${t.message}")
                Toast.makeText(this@LoginActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}