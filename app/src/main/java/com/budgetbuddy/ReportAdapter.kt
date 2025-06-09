package com.budgetbuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private var items: List<ReportItem>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameText: TextView = itemView.findViewById(R.id.categoryNameText)
        val totalAmountText: TextView = itemView.findViewById(R.id.totalAmountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = items[position]
        holder.categoryNameText.text = item.categoryName
        holder.totalAmountText.text = "Total: R%.2f".format(item.totalAmount)
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<ReportItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
