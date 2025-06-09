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

/**
 * ReportsActivity
 *
 * This activity generates a category-based summary report of expenses for a user-defined date range.
 * It retrieves expense data from RoomDB, groups it by category, calculates totals, and displays the
 * results in a RecyclerView.
 *
 * Author: Muhammad Shaikh
 * References:
 * - Kotlin coroutines with Room: https://developer.android.com/topic/libraries/architecture/coroutines
 * - RecyclerView patterns: https://developer.android.com/guide/topics/ui/layout/recyclerview
 */
class ReportsActivity : AppCompatActivity() {

    // UI components
    private lateinit var startDateInput: EditText
    private lateinit var endDateInput: EditText
    private lateinit var generateReportButton: Button
    private lateinit var reportRecyclerView: RecyclerView

    // Database and adapter
    private lateinit var db: AppDatabase
    private lateinit var adapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize RoomDB
        db = AppDatabase.getDatabase(this)

        // Bind UI elements
        startDateInput = findViewById(R.id.startDateInput)
        endDateInput = findViewById(R.id.endDateInput)
        generateReportButton = findViewById(R.id.generateReportButton)
        reportRecyclerView = findViewById(R.id.reportRecyclerView)

        // Set up RecyclerView
        adapter = ReportAdapter(emptyList())
        reportRecyclerView.layoutManager = LinearLayoutManager(this)
        reportRecyclerView.adapter = adapter

        // Trigger report generation on button click
        generateReportButton.setOnClickListener {
            val startDate = startDateInput.text.toString()
            val endDate = endDateInput.text.toString()

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Enter both start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            generateReport(startDate, endDate)
        }
    }

    /**
     * Fetches expenses from the database between the given dates,
     * groups them by category, and updates the report adapter.
     *
     * @param startDate Start of the date range (inclusive)
     * @param endDate End of the date range (inclusive)
     */
    private fun generateReport(startDate: String, endDate: String) {
        lifecycleScope.launch {
            val expenses: List<Expense> = db.expenseDao().getExpensesBetweenDates(startDate, endDate)
            val categories = db.categoryDao().getAllCategories()

            // Group expenses by categoryId and calculate total per category
            val grouped = expenses.groupBy { it.categoryId }
                .map { (categoryId, expensesInCategory) ->
                    val total = expensesInCategory.sumOf { it.amount }
                    val categoryName = categories.find { it.id == categoryId }?.name ?: "Unknown"
                    ReportItem(categoryName, total)
                }

            adapter.updateList(grouped)
        }
    }
}
