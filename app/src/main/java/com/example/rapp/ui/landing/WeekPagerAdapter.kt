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
    private var datesWithEvents: Set<LocalDate> = emptySet()

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

    fun setDatesWithEvents(dates: List<LocalDate>) {
        datesWithEvents = dates.toSet()
        notifyDataSetChanged()
    }

    private fun getWeekDays(weekOffset: Int): List<CalendarDay> {
        val today = LocalDate.now()
        val startOfWeek = today
            .plusWeeks(weekOffset.toLong())
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val midWeek = startOfWeek.plusDays(3) // Jueves de esa semana
        val referenceMonth = midWeek.month

        return (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            CalendarDay(
                date = date,
                isCurrentMonth = date.month == referenceMonth,
                isToday = date == today,
                isSelected = date == selectedDate,
                hasEvents = datesWithEvents.contains(date)
            )
        }
    }

    private fun getMonthDays(monthOffset: Int): List<CalendarDay> {
        val today = LocalDate.now()
        val targetMonth = today.plusMonths(monthOffset.toLong())
        val firstDayOfMonth = targetMonth.withDayOfMonth(1)

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
                    hasEvents = datesWithEvents.contains(currentDate)
                )
            )
            currentDate = currentDate.plusDays(1)
        }

        return days
    }

    // Obtener la fecha representativa de una posición
    fun getDateForPosition(position: Int): LocalDate {
        val offset = position - START_POSITION
        return if (isMonthView) {
            // En vista mensual: el primer día del mes objetivo
            LocalDate.now().plusMonths(offset.toLong()).withDayOfMonth(1)
        } else {
            // En vista semanal: el jueves de esa semana (representa mejor el mes)
            val today = LocalDate.now()
            val startOfWeek = today
                .plusWeeks(offset.toLong())
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            startOfWeek.plusDays(3) // Jueves
        }
    }

    // Obtener la posición para una fecha específica
    fun getPositionForDate(date: LocalDate): Int {
        val today = LocalDate.now()
        return if (isMonthView) {
            val monthsDiff = (date.year - today.year) * 12 + (date.monthValue - today.monthValue)
            START_POSITION + monthsDiff
        } else {
            val startOfTargetWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val startOfCurrentWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val weeksDiff = ((startOfTargetWeek.toEpochDay() - startOfCurrentWeek.toEpochDay()) / 7).toInt()
            START_POSITION + weeksDiff
        }
    }
}