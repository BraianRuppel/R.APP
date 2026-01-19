package com.example.rapp.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.CalendarDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class WeekPagerAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<WeekPagerAdapter.WeekViewHolder>() {

    private var selectedDate: LocalDate = LocalDate.now()
    private var isMonthView: Boolean = false

    // Número de páginas (semanas/meses) hacia atrás y adelante
    companion object {
        const val TOTAL_PAGES = 1000
        const val START_POSITION = 500
    }

    inner class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvDays: RecyclerView = itemView.findViewById(R.id.rvDays)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_page, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val offset = position - START_POSITION
        val days = if (isMonthView) {
            getMonthDays(offset)
        } else {
            getWeekDays(offset)
        }

        val adapter = CalendarDayAdapter { day ->
            selectedDate = day.date
            onDayClick(day)
            notifyDataSetChanged()
        }

        holder.rvDays.layoutManager = GridLayoutManager(holder.itemView.context, 7)
        holder.rvDays.adapter = adapter
        adapter.submitList(days)
    }

    override fun getItemCount(): Int = TOTAL_PAGES

    fun setMonthView(isMonth: Boolean) {
        isMonthView = isMonth
        notifyDataSetChanged()
    }

    fun isMonthView(): Boolean = isMonthView

    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
        notifyDataSetChanged()
    }

    private fun getWeekDays(weekOffset: Int): List<CalendarDay> {
        val today = LocalDate.now()
        val startOfWeek = today
            .plusWeeks(weekOffset.toLong())
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        return (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            CalendarDay(
                date = date,
                isCurrentMonth = date.month == today.month,
                isToday = date == today,
                isSelected = date == selectedDate,
                hasEvents = false // TODO: Conectar con eventos reales
            )
        }
    }

    private fun getMonthDays(monthOffset: Int): List<CalendarDay> {
        val today = LocalDate.now()
        val targetMonth = today.plusMonths(monthOffset.toLong())
        val firstDayOfMonth = targetMonth.withDayOfMonth(1)
        val lastDayOfMonth = targetMonth.with(TemporalAdjusters.lastDayOfMonth())

        // Encontrar el domingo antes del primer día del mes
        val startDate = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        // Calcular cuántos días mostrar (siempre 6 semanas = 42 días para consistencia)
        val days = mutableListOf<CalendarDay>()
        var currentDate = startDate

        repeat(42) {
            days.add(
                CalendarDay(
                    date = currentDate,
                    isCurrentMonth = currentDate.month == targetMonth.month,
                    isToday = currentDate == today,
                    isSelected = currentDate == selectedDate,
                    hasEvents = false
                )
            )
            currentDate = currentDate.plusDays(1)
        }

        return days
    }

    fun getDateForPosition(position: Int): LocalDate {
        val offset = position - START_POSITION
        return if (isMonthView) {
            LocalDate.now().plusMonths(offset.toLong())
        } else {
            LocalDate.now().plusWeeks(offset.toLong())
        }
    }
}