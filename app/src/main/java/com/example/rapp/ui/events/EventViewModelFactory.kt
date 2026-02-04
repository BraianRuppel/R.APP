package com.example.rapp.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rapp.data.repository.EventRepository

class EventViewModelFactory(
    private val eventRepository: EventRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}