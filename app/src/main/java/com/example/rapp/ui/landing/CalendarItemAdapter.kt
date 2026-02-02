package com.example.rapp.ui.landing

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.Item

class CalendarItemAdapter(
    private val onCompletedToggle: (Item) -> Unit,
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, CalendarItemAdapter.CalendarItemViewHolder>(ItemDiffCallback()) {

    inner class CalendarItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
        val tvItemText: TextView = itemView.findViewById(R.id.tvItemText)
        val tvGroupName: TextView = itemView.findViewById(R.id.tvGroupName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_task, parent, false)
        return CalendarItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarItemViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context

        holder.cbCompleted.isChecked = item.isCompleted
        holder.cbCompleted.setOnClickListener {
            onCompletedToggle(item)
        }

        holder.tvItemText.text = item.text

        // Estilo tachado si está completado
        if (item.isCompleted) {
            holder.tvItemText.paintFlags = holder.tvItemText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvItemText.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
        } else {
            holder.tvItemText.paintFlags = holder.tvItemText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvItemText.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}