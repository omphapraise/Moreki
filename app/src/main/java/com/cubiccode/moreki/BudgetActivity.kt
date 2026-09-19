package com.cubiccode.moreki

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cubiccode.moreki.databinding.ActivityBudgetBinding
import com.cubiccode.moreki.utils.SessionManager

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        loadBudget()
    }

    private fun loadBudget() {
        val expenses = session.getExpenses()
        val currency = session.getCurrency()
        val monthlyBudget = session.getMonthlyBudget()

        if (expenses.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.totalSpentText.text = "${currency}0.00"
            binding.categoryListView.adapter = null
            updateBudgetSummary(0.0, currency, monthlyBudget)
            return
        }

        binding.emptyStateText.visibility = View.GONE

        val total = expenses.sumOf { it.amount }
        binding.totalSpentText.text = "$currency${"%.2f".format(total)}"
        updateBudgetSummary(total, currency, monthlyBudget)

        // Group by category and sum, sorted highest spend first
        val byCategory = expenses
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { (_, sum) -> sum }

        val adapter = CategoryListAdapter(this, byCategory, currency)
        binding.categoryListView.adapter = adapter
    }

    private fun updateBudgetSummary(total: Double, currency: String, monthlyBudget: Double) {
        binding.budgetLimitText.text = "Budget: $currency${"%.2f".format(monthlyBudget)}"

        val remaining = monthlyBudget - total
        if (remaining >= 0) {
            binding.remainingText.text = "Remaining: $currency${"%.2f".format(remaining)}"
            binding.remainingText.setTextColor(ContextCompat.getColor(this, R.color.primary))
        } else {
            binding.remainingText.text = "Over by: $currency${"%.2f".format(-remaining)}"
            binding.remainingText.setTextColor(ContextCompat.getColor(this, R.color.error_red))
        }
    }

    override fun onResume() {
        super.onResume()
        loadBudget() // refresh if user logged a new expense, or changed currency/budget, and came back
    }
}

/**
 * Custom adapter for categoryListView — binds each (category, total) pair
 * into list_item_category.xml's two fields.
 */
class CategoryListAdapter(
    context: Context,
    private val categories: List<Pair<String, Double>>,
    private val currency: String
) : ArrayAdapter<Pair<String, Double>>(context, R.layout.list_item_category, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_category, parent, false)

        val (category, amount) = categories[position]

        val nameText = view.findViewById<TextView>(R.id.categoryNameText)
        val amountText = view.findViewById<TextView>(R.id.categoryAmountText)

        nameText.text = category
        amountText.text = "$currency${"%.2f".format(amount)}"

        return view
    }
}