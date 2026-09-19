package com.cubiccode.moreki

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cubiccode.moreki.databinding.ActivityDashboardBinding
import com.cubiccode.moreki.utils.ScoreCalculator
import com.cubiccode.moreki.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val name = session.getCurrentUserName().takeIf { !it.isNullOrEmpty() } ?: "there"
        binding.welcomeText.text = "Welcome back, $name"

        // ✅ Navigate to Expense screen
        binding.logExpenseButton.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        // ✅ Navigate to Budget Overview screen
        binding.budgetOverviewButton.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }

        // ✅ Navigate to Settings screen
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.logoutText.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh name and score every time we come back to the Dashboard
        val name = session.getCurrentUserName().takeIf { !it.isNullOrEmpty() } ?: "there"
        binding.welcomeText.text = "Welcome back, $name"

        val score = ScoreCalculator.calculateScore(session.getExpenses(), session.getMonthlyBudget())
        binding.scoreText.text = "$score / 100"
    }
}