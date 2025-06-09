package com.budgetbuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.database.Expense

class RecentExpensesAdapter(private var expenses: List<Expense>) :
    RecyclerView.Adapter<RecentExpensesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.expenseDescription)
        val amount: TextView = view.findViewById(R.id.expenseAmount)
        val date: TextView = view.findViewById(R.id.expenseDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        holder.description.text = expense.description
        holder.amount.text = "R ${expense.amount}"
        holder.date.text = expense.date
    }

    override fun getItemCount() = expenses.size

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
