package com.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.database.AppDatabase
import kotlinx.coroutines.launch

/**
 * DashboardActivity
 *
 * Serves as the home screen for logged-in users, providing navigation
 * to core app features: expense tracking, reports, category charts,
 * budget goal settings, and two custom enhancements:
 * - Daily budgeting tips
 * - Theme toggle
 *
 * Author: Muhammad Shaikh
 * With guidance from ChatGPT (OpenAI, 2025)
 * Reference: https://developer.android.com/guide/components/activities/intro-activities
 */
class DashboardActivity : AppCompatActivity() {

    // UI components
    private lateinit var welcomeText: TextView
    private lateinit var dailyTipText: TextView
    private lateinit var addExpenseButton: Button
    private lateinit var viewExpensesButton: Button
    private lateinit var viewReportsButton: Button
    private lateinit var settingsButton: Button
    private lateinit var budgetGoalsButton: Button
    private lateinit var categoryChartButton: Button
    private lateinit var toggleThemeButton: Button
    private lateinit var recentExpensesRecyclerView: RecyclerView
    private lateinit var recentAdapter: RecentExpensesAdapter

    // Database reference
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply dark/light theme before view is loaded
        ThemeUtils.applyTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Bind all UI components
        welcomeText = findViewById(R.id.welcomeText)
        dailyTipText = findViewById(R.id.dailyTipText)
        addExpenseButton = findViewById(R.id.addExpenseButton)
        viewExpensesButton = findViewById(R.id.viewExpensesButton)
        viewReportsButton = findViewById(R.id.viewReportsButton)
        settingsButton = findViewById(R.id.settingsButton)
        budgetGoalsButton = findViewById(R.id.budgetGoalsButton)
        categoryChartButton = findViewById(R.id.viewCategoryChartButton)
        toggleThemeButton = findViewById(R.id.toggleThemeButton)
        recentExpensesRecyclerView = findViewById(R.id.recentExpensesRecyclerView)

        // Display welcome message
        val username = intent.getStringExtra("username")
        welcomeText.text = "Welcome, $username!"

        // 🎯 Feature 1: Show random daily budgeting tip
        val dailyTips = listOf(
            "Track every rand you spend — awareness is key.",
            "Set a weekly budget and stick to it.",
            "Avoid impulse purchases — wait 24 hours before buying.",
            "Use cash instead of cards to control your spending.",
            "Review your expenses at the end of each day.",
            "Plan your meals to avoid costly takeaways.",
            "Unsubscribe from unused services.",
            "Compare prices before making big purchases.",
            "Reward yourself for reaching saving goals.",
            "Keep receipts to stay accountable."
        )
        dailyTipText.text = "💡 Tip: ${dailyTips.random()}"

        // 🎨 Feature 2: Toggle light/dark theme
        toggleThemeButton.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            finish()
            startActivity(intent)
        }

        // Set up button navigation
        addExpenseButton.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        viewExpensesButton.setOnClickListener {
            startActivity(Intent(this, ViewExpensesActivity::class.java))
        }

        viewReportsButton.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        budgetGoalsButton.setOnClickListener {
            startActivity(Intent(this, BudgetSettingsActivity::class.java))
        }

        categoryChartButton.setOnClickListener {
            startActivity(Intent(this, CategoryChartActivity::class.java))
        }

        // Set up recent expenses list
        db = AppDatabase.getDatabase(this)
        recentExpensesRecyclerView.layoutManager = LinearLayoutManager(this)
        recentAdapter = RecentExpensesAdapter(emptyList())
        recentExpensesRecyclerView.adapter = recentAdapter

        // Load 5 most recent expenses
        lifecycleScope.launch {
            val recentExpenses = db.expenseDao().getRecentExpenses(5)
            recentAdapter.updateExpenses(recentExpenses)
        }
    }
}
