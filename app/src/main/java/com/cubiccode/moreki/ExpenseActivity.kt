package com.cubiccode.moreki

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cubiccode.moreki.databinding.ActivityExpenseBinding
import com.cubiccode.moreki.models.Expense
import com.cubiccode.moreki.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseBinding
    private lateinit var session: SessionManager

    private val categories = listOf("Food", "Transport", "Rent", "Entertainment", "Airtime/Data", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item_selected, categories)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.categorySpinner.adapter = spinnerAdapter

        binding.saveExpenseButton.setOnClickListener { saveExpense() }

        refreshExpenseList()
    }

    private fun saveExpense() {
        binding.amountError.visibility = View.GONE

        val amountStr = binding.amountInput.text.toString().trim()
        val amount = amountStr.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            binding.amountError.text = "Enter a valid amount"
            binding.amountError.visibility = View.VISIBLE
            return
        }

        val category = binding.categorySpinner.selectedItem as String
        val expense = Expense(amount, category)

        session.addExpense(expense)
        Log.d("ExpenseActivity", "Saved expense: ${session.getCurrency()}$amount - $category")
        Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()

        binding.amountInput.text.clear()
        refreshExpenseList()
    }

    private fun refreshExpenseList() {
        val expenses = session.getExpenses().sortedByDescending { it.timestamp }
        val currency = session.getCurrency()

        val adapter = ExpenseListAdapter(this, expenses, currency)
        binding.expenseListView.adapter = adapter
    }
}

/**
 * Custom adapter for expenseListView — binds each Expense into
 * list_item_expense.xml's three fields (category, date, amount).
 */
class ExpenseListAdapter(
    context: Context,
    private val expenses: List<Expense>,
    private val currency: String
) : ArrayAdapter<Expense>(context, R.layout.list_item_expense, expenses) {

    private val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_expense, parent, false)

        val expense = expenses[position]

        val categoryText = view.findViewById<TextView>(R.id.expenseCategoryText)
        val dateText = view.findViewById<TextView>(R.id.expenseDateText)
        val amountText = view.findViewById<TextView>(R.id.expenseAmountText)

        categoryText.text = expense.category
        dateText.text = dateFormat.format(expense.timestamp)
        amountText.text = "$currency${"%.2f".format(expense.amount)}"

        return view
    }
}