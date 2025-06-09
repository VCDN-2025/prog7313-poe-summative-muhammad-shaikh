package com.budgetbuddy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense>

    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentExpenses(limit: Int): List<Expense>

}
