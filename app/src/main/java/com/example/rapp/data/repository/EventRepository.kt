package com.example.rapp.data.repository

import androidx.lifecycle.LiveData
import com.example.rapp.data.dao.EventDao
import com.example.rapp.data.model.Event
import java.time.LocalDate

class EventRepository(private val eventDao: EventDao) {

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    val repeatingEvents: LiveData<List<Event>> = eventDao.getRepeatingEvents()

    val datesWithEvents: LiveData<List<LocalDate>> = eventDao.getDatesWithEvents()

    fun getEventsByDate(date: LocalDate): LiveData<List<Event>> {
        return eventDao.getEventsByDate(date)
    }

    fun getUpcomingEvents(days: Int = 30): LiveData<List<Event>> {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(days.toLong())
        return eventDao.getEventsInRange(startDate, endDate)
    }

    suspend fun insert(event: Event): Long {
        return eventDao.insert(event)
    }

    suspend fun update(event: Event) {
        eventDao.update(event)
    }

    suspend fun delete(event: Event) {
        eventDao.delete(event)
    }
}