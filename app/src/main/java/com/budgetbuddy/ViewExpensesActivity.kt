package com.budgetbuddy

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.database.AppDatabase
import com.budgetbuddy.database.Expense
import kotlinx.coroutines.launch

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var expensesAdapter: ExpensesAdapter
    private lateinit var db: AppDatabase

    // New views for filtering
    private lateinit var startDateFilter: EditText
    private lateinit var endDateFilter: EditText
    private lateinit var filterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        // Initialize database and views
        db = AppDatabase.getDatabase(this)
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView)
        startDateFilter = findViewById(R.id.startDateFilter)
        endDateFilter = findViewById(R.id.endDateFilter)
        filterButton = findViewById(R.id.filterButton)

        expensesRecyclerView.layoutManager = LinearLayoutManager(this)
        expensesAdapter = ExpensesAdapter(listOf())
        expensesRecyclerView.adapter = expensesAdapter

        fetchExpenses() // Load all by default

        filterButton.setOnClickListener {
            val start = startDateFilter.text.toString()
            val end = endDateFilter.text.toString()

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please enter both start and end dates.", Toast.LENGTH_SHORT).show()
            } else {
                filterExpensesByDate(start, end)
            }
        }
    }

    private fun fetchExpenses() {
        lifecycleScope.launch {
            val expensesList = db.expenseDao().getAllExpenses()

            // Debug log each expense
            for (expense in expensesList) {
                android.util.Log.d(
                    "EXPENSE_DEBUG",
                    "ID: ${expense.id}, Amount: ${expense.amount}, Desc: ${expense.description}, Date: ${expense.date}"
                )
            }

            expensesAdapter.updateExpenses(expensesList)
        }
    }

    private fun filterExpensesByDate(startDate: String, endDate: String) {
        lifecycleScope.launch {
            val filteredExpenses: List<Expense> =
                db.expenseDao().getExpensesBetweenDates(startDate, endDate)

            expensesAdapter.updateExpenses(filteredExpenses)
        }
    }
}
