package com.example.rapp.ui.events

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

class EventAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onEventLongClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es"))

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewColorIndicator: View = itemView.findViewById(R.id.viewColorIndicator)
        val ivEventIcon: ImageView = itemView.findViewById(R.id.ivEventIcon)
        val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvEventDate: TextView = itemView.findViewById(R.id.tvEventDate)
        val tvEventDescription: TextView = itemView.findViewById(R.id.tvEventDescription)
        val tvDaysUntil: TextView = itemView.findViewById(R.id.tvDaysUntil)
        val ivRepeating: ImageView = itemView.findViewById(R.id.ivRepeating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        val context = holder.itemView.context
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

        // Título y descripción
        holder.tvEventTitle.text = event.title
        holder.tvEventDate.text = event.date.format(dateFormatter)

        if (event.description.isNotBlank()) {
            holder.tvEventDescription.visibility = View.VISIBLE
            holder.tvEventDescription.text = event.description
        } else {
            holder.tvEventDescription.visibility = View.GONE
        }

        // Indicador de repetición
        holder.ivRepeating.visibility = if (event.isRepeatingYearly) View.VISIBLE else View.GONE

        // Días hasta el evento
        val eventDate = if (event.isRepeatingYearly) {
            getNextOccurrence(event.date)
        } else {
            event.date
        }

        val daysUntil = ChronoUnit.DAYS.between(today, eventDate)
        holder.tvDaysUntil.text = when {
            daysUntil < 0 -> "Pasado"
            daysUntil == 0L -> "¡Hoy!"
            daysUntil == 1L -> "Mañana"
            else -> "En $daysUntil días"
        }

        // Clicks
        holder.itemView.setOnClickListener {
            onEventClick(event)
        }

        holder.itemView.setOnLongClickListener {
            onEventLongClick(event)
            true
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