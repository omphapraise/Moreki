package com.cubiccode.moreki

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cubiccode.moreki.api.RetrofitClient
import com.cubiccode.moreki.databinding.ActivitySettingsBinding
import com.cubiccode.moreki.models.AuthResponse
import com.cubiccode.moreki.models.UpdateProfileRequest
import com.cubiccode.moreki.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SessionManager

    private val currencies = listOf("R", "$", "€", "£")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        // Load saved values
        binding.nameInput.setText(session.getCurrentUserName())
        binding.emailText.text = session.getCurrentUserEmail()

        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item_selected, currencies)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.currencySpinner.adapter = spinnerAdapter
        binding.currencySpinner.setSelection(currencies.indexOf(session.getCurrency()).coerceAtLeast(0))

        binding.notificationsSwitch.isChecked = session.getNotificationsEnabled()

        val savedBudget = session.getMonthlyBudget()
        binding.budgetInput.setText(if (savedBudget > 0) savedBudget.toString() else "")

        binding.saveSettingsButton.setOnClickListener { saveChanges() }
    }

    private fun saveChanges() {
        binding.nameError.visibility = View.GONE
        binding.budgetError.visibility = View.GONE

        val newName = binding.nameInput.text.toString().trim()
        val budgetStr = binding.budgetInput.text.toString().trim()
        val budgetValue = budgetStr.toDoubleOrNull()

        var valid = true

        if (newName.isEmpty()) {
            binding.nameError.text = "Name cannot be empty"
            binding.nameError.visibility = View.VISIBLE
            valid = false
        }

        if (budgetValue == null || budgetValue <= 0) {
            binding.budgetError.text = "Enter a valid budget amount"
            binding.budgetError.visibility = View.VISIBLE
            valid = false
        }

        if (!valid) return

        val email = session.getCurrentUserEmail() ?: return

        binding.saveSettingsButton.isEnabled = false

        val call = RetrofitClient.apiService.updateProfile(UpdateProfileRequest(email, newName))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                binding.saveSettingsButton.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    session.updateUserName(newName)

                    val selectedCurrency = binding.currencySpinner.selectedItem as String
                    session.setCurrency(selectedCurrency)

                    session.setNotificationsEnabled(binding.notificationsSwitch.isChecked)
                    session.setMonthlyBudget(budgetValue!!)

                    Toast.makeText(this@SettingsActivity, "Settings saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@SettingsActivity, "Couldn't save changes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                binding.saveSettingsButton.isEnabled = true
                Toast.makeText(this@SettingsActivity, "Couldn't reach the server", Toast.LENGTH_LONG).show()
            }
        })
    }
}