package com.example.rapp.ui.landing

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.Event
import com.example.rapp.data.model.EventType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class UpcomingEventAdapter(
    private val onEventClick: (Event) -> Unit
) : ListAdapter<Event, UpcomingEventAdapter.UpcomingEventViewHolder>(EventDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es"))

    inner class UpcomingEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewColorIndicator: View = itemView.findViewById(R.id.viewColorIndicator)
        val ivEventIcon: ImageView = itemView.findViewById(R.id.ivEventIcon)
        val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvEventDate: TextView = itemView.findViewById(R.id.tvEventDate)
        val tvDaysUntil: TextView = itemView.findViewById(R.id.tvDaysUntil)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_event, parent, false)
        return UpcomingEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {
        val event = getItem(position)
        val today = LocalDate.now()

        // Color indicator
        try {
            holder.viewColorIndicator.setBackgroundColor(Color.parseColor(event.color))
        } catch (e: Exception) {
            holder.viewColorIndicator.setBackgroundColor(Color.parseColor("#6200EE"))
        }

        // Icono según tipo
        val iconRes = when (event.type) {
            EventType.BIRTHDAY -> R.drawable.ic_cake
            EventType.ANNIVERSARY -> R.drawable.ic_favorite_filled
            EventType.HOLIDAY -> R.drawable.ic_celebration
            EventType.ONE_TIME -> R.drawable.ic_event
        }
        holder.ivEventIcon.setImageResource(iconRes)

        // Título
        holder.tvEventTitle.text = event.title

        // Fecha
        val eventDate = if (event.isRepeatingYearly) {
            getNextOccurrence(event.date)
        } else {
            event.date
        }
        holder.tvEventDate.text = eventDate.format(dateFormatter)

        // Mostrar años que cumple
        if (event.type == EventType.BIRTHDAY && event.isRepeatingYearly) {
            val age = eventDate.year - event.date.year
            holder.tvAge.visibility = View.VISIBLE
            holder.tvAge.text = "• $age años"
        } else if (event.type == EventType.ANNIVERSARY && event.isRepeatingYearly) {
            val years = eventDate.year - event.date.year
            holder.tvAge.visibility = View.VISIBLE
            holder.tvAge.text = "• $years años"
        } else {
            holder.tvAge.visibility = View.GONE
        }

        // Días hasta el evento
        val daysUntil = ChronoUnit.DAYS.between(today, eventDate)
        holder.tvDaysUntil.text = when {
            daysUntil == 0L -> "¡Hoy!"
            daysUntil == 1L -> "Mañana"
            else -> "En $daysUntil días"
        }

        holder.itemView.setOnClickListener {
            onEventClick(event)
        }
    }

    private fun getNextOccurrence(date: LocalDate): LocalDate {
        val today = LocalDate.now()
        var nextDate = date.withYear(today.year)
        if (nextDate.isBefore(today)) {
            nextDate = nextDate.plusYears(1)
        }
        return nextDate
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}