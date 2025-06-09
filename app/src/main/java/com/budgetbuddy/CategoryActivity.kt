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
import com.budgetbuddy.database.Category
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var categoryInput: EditText
    private lateinit var addCategoryButton: Button
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        db = AppDatabase.getDatabase(this)

        categoryInput = findViewById(R.id.categoryInput)
        addCategoryButton = findViewById(R.id.addCategoryButton)
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        adapter = CategoryAdapter(listOf())
        categoryRecyclerView.adapter = adapter
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)

        addCategoryButton.setOnClickListener {
            val name = categoryInput.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, "Category name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(name = name)

            lifecycleScope.launch {
                db.categoryDao().insertCategory(category)
                Toast.makeText(applicationContext, "Category added", Toast.LENGTH_SHORT).show()
                categoryInput.text.clear()
                loadCategories()
            }
        }

        loadCategories()

    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val categories = db.categoryDao().getAllCategories()
            adapter.updateList(categories)

        }
    }
}
