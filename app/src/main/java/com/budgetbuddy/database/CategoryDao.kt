package com.budgetbuddy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<Category>
}
