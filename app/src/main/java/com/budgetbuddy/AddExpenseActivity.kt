package com.budgetbuddy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.budgetbuddy.database.AppDatabase
import com.budgetbuddy.database.Category
import com.budgetbuddy.database.Expense
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * AddExpenseActivity
 *
 * Allows the user to add a new expense entry with details including amount,
 * description, date, category, and an optional photo. Uses RoomDB for data persistence
 * and internal cache storage for storing images.
 *
 * Author: Muhammad Shaikh
 * With guidance from: OpenAI ChatGPT (2025)
 * References:
 * - Android Documentation: https://developer.android.com/guide/topics/providers/document-provider
 * - FileProvider (AndroidX): https://developer.android.com/reference/androidx/core/content/FileProvider
 */
class AddExpenseActivity : AppCompatActivity() {

    // UI Components
    private lateinit var amountInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addPhotoButton: Button
    private lateinit var saveExpenseButton: Button

    // Database instance
    private lateinit var db: AppDatabase

    // List of categories loaded from DB
    private var categoryList: List<Category> = listOf()

    // File path of selected image
    private var selectedImagePath: String? = null

    companion object {
        // Request code for image selection
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        // Bind views to layout elements
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        dateInput = findViewById(R.id.dateInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        addPhotoButton = findViewById(R.id.addPhotoButton)
        saveExpenseButton = findViewById(R.id.saveExpenseButton)

        // Set click listeners
        saveExpenseButton.setOnClickListener {
            saveExpense()
        }

        // Open gallery to pick photo
        addPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Load category list into spinner
        loadCategoriesIntoSpinner()
    }

    /**
     * Loads all categories from RoomDB and populates the spinner dropdown.
     */
    private fun loadCategoriesIntoSpinner() {
        lifecycleScope.launch {
            categoryList = db.categoryDao().getAllCategories()
            if (categoryList.isEmpty()) {
                Toast.makeText(this@AddExpenseActivity, "Please add categories first.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val categoryNames = categoryList.map { it.name }
            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }
    }

    /**
     * Validates input and inserts a new Expense into the database.
     */
    private fun saveExpense() {
        val amountText = amountInput.text.toString()
        val description = descriptionInput.text.toString()
        val date = dateInput.text.toString()

        // Ensure all required fields are filled
        if (amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Amount must be a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected category
        val selectedIndex = categorySpinner.selectedItemPosition
        val selectedCategoryId = categoryList.getOrNull(selectedIndex)?.id ?: 1

        // Construct Expense object
        val expense = Expense(
            amount = amount,
            description = description,
            date = date,
            categoryId = selectedCategoryId,
            photoPath = selectedImagePath
        )

        // Insert into database using coroutine
        lifecycleScope.launch {
            db.expenseDao().insertExpense(expense)
            Toast.makeText(applicationContext, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Called after user selects a photo. Copies the photo to internal cache
     * and stores the file path.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val inputStream = contentResolver.openInputStream(data.data!!)
            val file = File(cacheDir, "images")
            file.mkdirs()

            // Save image to a unique filename in internal cache
            val imageFile = File(file, "expense_${System.currentTimeMillis()}.jpg")
            val outputStream = imageFile.outputStream()

            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()

            selectedImagePath = imageFile.absolutePath
            Toast.makeText(this, "Photo saved locally!", Toast.LENGTH_SHORT).show()
        }
    }
}
