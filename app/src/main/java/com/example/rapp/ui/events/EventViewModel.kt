package com.example.rapp.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.rapp.data.model.Event
import com.example.rapp.data.model.EventType
import com.example.rapp.data.repository.EventRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    val allEvents: LiveData<List<Event>> = eventRepository.allEvents

    val upcomingEvents: LiveData<List<Event>> = eventRepository.getUpcomingEvents(30)

    private val _selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    val eventsForSelectedDate: LiveData<List<Event>> = _selectedDate.switchMap { date ->
        eventRepository.getEventsByDate(date)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addEvent(
        title: String,
        description: String,
        date: LocalDate,
        type: EventType,
        isRepeatingYearly: Boolean,
        color: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            eventRepository.insert(
                Event(
                    title = title,
                    description = description,
                    date = date,
                    type = type,
                    isRepeatingYearly = isRepeatingYearly,
                    color = color
                )
            )
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.update(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.delete(event)
        }
    }
}