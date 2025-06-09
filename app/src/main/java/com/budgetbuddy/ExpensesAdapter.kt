package com.budgetbuddy

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.database.Expense
import java.io.File

class ExpensesAdapter(private var expensesList: List<Expense>) :
    RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        val viewPhotoButton: Button = itemView.findViewById(R.id.viewPhotoButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expensesList[position]
        holder.amountText.text = "R ${expense.amount}"
        holder.descriptionText.text = expense.description

        if (!expense.photoPath.isNullOrEmpty()) {
            holder.viewPhotoButton.visibility = View.VISIBLE
            holder.viewPhotoButton.setOnClickListener {
                try {
                    val file = File(expense.photoPath!!)
                    val uri: Uri = FileProvider.getUriForFile(
                        holder.itemView.context,
                        "${holder.itemView.context.packageName}.fileprovider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "image/*")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            holder.viewPhotoButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = expensesList.size

    fun updateExpenses(newExpenses: List<Expense>) {
        expensesList = newExpenses
        notifyDataSetChanged()
    }
}
