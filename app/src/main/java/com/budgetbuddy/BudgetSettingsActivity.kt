package com.budgetbuddy

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetSettingsActivity : AppCompatActivity() {

    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var saveGoalsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_settings)

        minGoalInput = findViewById(R.id.minGoalInput)
        maxGoalInput = findViewById(R.id.maxGoalInput)
        saveGoalsButton = findViewById(R.id.saveGoalsButton)

        loadGoals()

        saveGoalsButton.setOnClickListener {
            val min = minGoalInput.text.toString().toDoubleOrNull()
            val max = maxGoalInput.text.toString().toDoubleOrNull()

            if (min == null || max == null) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putFloat("minGoal", min.toFloat())
                .putFloat("maxGoal", max.toFloat())
                .apply()

            Toast.makeText(this, "Budget goals saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadGoals() {
        val prefs = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
        val min = prefs.getFloat("minGoal", 0f)
        val max = prefs.getFloat("maxGoal", 0f)

        if (min > 0f) minGoalInput.setText(min.toString())
        if (max > 0f) maxGoalInput.setText(max.toString())
    }
}
