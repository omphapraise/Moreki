package com.cubiccode.moreki.utils

import android.content.Context
import android.content.SharedPreferences
import com.cubiccode.moreki.models.User
import com.cubiccode.moreki.models.Expense
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("moreki_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun registerUser(user: User): Boolean {
        if (prefs.contains("user_email_${user.email}")) return false // already exists
        prefs.edit()
            .putString("user_email_${user.email}", user.email)
            .putString("user_name_${user.email}", user.name)
            .putString("user_pass_${user.email}", user.password)
            .apply()
        return true
    }

    fun validateLogin(email: String, password: String): Boolean {
        val storedPass = prefs.getString("user_pass_$email", null) ?: return false
        return storedPass == password
    }

    fun setLoggedInUser(email: String) {
        val name = prefs.getString("user_name_$email", "")
        prefs.edit()
            .putString("current_user_email", email)
            .putString("current_user_name", name)
            .apply()
    }

    // =========================
    // API-backed login
    // =========================

    fun setLoggedInUserFromApi(email: String, name: String) {
        prefs.edit()
            .putString("current_user_email", email)
            .putString("current_user_name", name)
            .apply()
    }

    fun getCurrentUserName(): String? =
        prefs.getString("current_user_name", null)

    fun getCurrentUserEmail(): String? =
        prefs.getString("current_user_email", null)

    fun logout() {
        prefs.edit()
            .remove("current_user_email")
            .remove("current_user_name")
            .apply()
    }

    fun isLoggedIn(): Boolean = getCurrentUserEmail() != null

    // =========================
    // Settings
    // =========================

    fun updateUserName(newName: String) {
        val email = getCurrentUserEmail() ?: return
        prefs.edit()
            .putString("user_name_$email", newName)
            .putString("current_user_name", newName)
            .apply()
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString("currency_pref", currency).apply()
    }

    fun getCurrency(): String = prefs.getString("currency_pref", "R") ?: "R"

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun getNotificationsEnabled(): Boolean =
        prefs.getBoolean("notifications_enabled", true) // default ON

    fun setMonthlyBudget(amount: Double) {
        prefs.edit().putFloat("monthly_budget", amount.toFloat()).apply()
    }

    fun getMonthlyBudget(): Double =
        prefs.getFloat("monthly_budget", 5000f).toDouble()

    // =========================
    // Expense Storage
    // =========================

    fun addExpense(expense: Expense) {
        val email = getCurrentUserEmail() ?: return
        val current = getExpenses().toMutableList()
        current.add(expense)
        val json = gson.toJson(current)
        prefs.edit().putString("expenses_$email", json).apply()
    }

    fun getExpenses(): List<Expense> {
        val email = getCurrentUserEmail() ?: return emptyList()
        val json = prefs.getString("expenses_$email", null) ?: return emptyList()
        val type = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(json, type)
    }
}