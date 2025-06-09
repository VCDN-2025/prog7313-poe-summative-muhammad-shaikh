package com.budgetbuddy

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.budgetbuddy.database.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

/**
 * CategoryChartActivity
 *
 * Displays a visually styled bar chart for expenses per category,
 * including dynamic axis labels, goal lines, and enhanced aesthetics.
 *
 * Author: Muhammad Shaikh
 * Enhanced with OpenAI ChatGPT (2025)
 */
class CategoryChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var db: AppDatabase

    // Visual goal thresholds
    private val minGoal = 1000f
    private val maxGoal = 10000f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_chart)

        barChart = findViewById(R.id.categoryBarChart)
        db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val expenses = db.expenseDao().getAllExpenses()
            val categories = db.categoryDao().getAllCategories()

            val grouped = expenses.groupBy { it.categoryId }
            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            grouped.entries.forEachIndexed { index, entry ->
                val total = entry.value.sumOf { it.amount }.toFloat()
                entries.add(BarEntry(index.toFloat(), total))
                val categoryName = categories.find { it.id == entry.key }?.name ?: "Unknown"
                labels.add(categoryName)
            }

            val dataSet = BarDataSet(entries, "Spending by Category").apply {
                colors = listOf(
                    Color.parseColor("#4CAF50"), // green
                    Color.parseColor("#2196F3"), // blue
                    Color.parseColor("#FF9800"), // orange
                    Color.parseColor("#9C27B0"), // purple
                    Color.parseColor("#F44336")  // red
                )
                valueTextColor = Color.BLACK
                valueTextSize = 14f
                setDrawValues(true)
            }

            val barData = BarData(dataSet)
            barData.barWidth = 0.9f

            barChart.apply {
                data = barData
                setFitBars(true)
                description.isEnabled = false
                setExtraBottomOffset(24f)
                animateY(1000)

                // X Axis
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    textSize = 13f
                    setDrawGridLines(false)
                    labelRotationAngle = -30f
                }

                // Y Axis Left
                axisLeft.apply {
                    axisMinimum = 0f
                    textSize = 13f
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                    removeAllLimitLines()

                    // Goal lines
                    addLimitLine(LimitLine(minGoal, "Min Goal").apply {
                        lineColor = Color.GREEN
                        lineWidth = 2f
                        textColor = Color.GREEN
                        textSize = 12f
                    })
                    addLimitLine(LimitLine(maxGoal, "Max Goal").apply {
                        lineColor = Color.RED
                        lineWidth = 2f
                        textColor = Color.RED
                        textSize = 12f
                    })
                }

                // Y Axis Right
                axisRight.isEnabled = false

                legend.isEnabled = true
                legend.textSize = 14f
                legend.formSize = 10f

                invalidate() // Refresh chart
            }
        }
    }
}
