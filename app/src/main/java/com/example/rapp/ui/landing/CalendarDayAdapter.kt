package com.example.rapp.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.CalendarDay

class CalendarDayAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarDayAdapter.DayViewHolder>(DayDiffCallback()) {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val viewEventIndicator: View = itemView.findViewById(R.id.viewEventIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = getItem(position)
        val context = holder.itemView.context

        holder.tvDay.text = day.date.dayOfMonth.toString()

        // Estilos según el estado del día
        when {
            day.isToday && day.isSelected -> {
                holder.tvDay.background = ContextCompat.getDrawable(context, R.drawable.bg_calendar_today_selected)
                holder.tvDay.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            day.isToday -> {
                holder.tvDay.background = ContextCompat.getDrawable(context, R.drawable.bg_calendar_today)
                holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.primary))
            }
            day.isSelected -> {
                holder.tvDay.background = ContextCompat.getDrawable(context, R.drawable.bg_calendar_selected)
                holder.tvDay.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            !day.isCurrentMonth -> {
                holder.tvDay.background = null
                holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                holder.tvDay.alpha = 0.4f
            }
            else -> {
                holder.tvDay.background = null
                holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                holder.tvDay.alpha = 1f
            }
        }

        // Indicador de eventos
        holder.viewEventIndicator.visibility = if (day.hasEvents) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            onDayClick(day)
        }
    }

    class DayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}