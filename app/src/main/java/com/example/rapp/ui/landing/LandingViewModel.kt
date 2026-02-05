package com.example.rapp.ui.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.rapp.data.model.Item
import com.example.rapp.data.model.Event
import com.example.rapp.data.repository.ItemRepository
import com.example.rapp.data.repository.EventRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class LandingViewModel(
    private val itemRepository: ItemRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    val favoriteItems: LiveData<List<Item>> = itemRepository.favoriteItems

    // Fechas que tienen items asignados
    val datesWithItems: LiveData<List<LocalDate>> = itemRepository.datesWithItems

    // Fechas con eventos
    private val datesWithEvents: LiveData<List<LocalDate>> = eventRepository.datesWithEvents

    // Combinar fechas de items y eventos para el calendario
    val allDatesWithContent: LiveData<Set<LocalDate>> = MediatorLiveData<Set<LocalDate>>().apply {
        var itemDates = emptyList<LocalDate>()
        var eventDates = emptyList<LocalDate>()

        fun update() {
            val combined = mutableSetOf<LocalDate>()
            combined.addAll(itemDates)
            // Para eventos repetibles, agregar las fechas de este año y el próximo
            eventDates.forEach { date ->
                combined.add(date)
                // También agregar la fecha ajustada al año actual
                val thisYear = date.withYear(LocalDate.now().year)
                val nextYear = date.withYear(LocalDate.now().year + 1)
                combined.add(thisYear)
                combined.add(nextYear)
            }
            value = combined
        }

        addSource(datesWithItems) { dates ->
            itemDates = dates
            update()
        }

        addSource(datesWithEvents) { dates ->
            eventDates = dates
            update()
        }
    }

    // Eventos próximos (7 días)
    val upcomingEvents: LiveData<List<Event>> = eventRepository.getUpcomingEvents(7)

    // Todos los eventos (para calcular próximos con repetición)
    val allEvents: LiveData<List<Event>> = eventRepository.allEvents

    // Fecha seleccionada
    private val _selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    // Items del día seleccionado (se actualiza automáticamente cuando cambia selectedDate)
    val itemsForSelectedDate: LiveData<List<Item>> = _selectedDate.switchMap { date ->
        itemRepository.getItemsByDate(date)
    }

    // Eventos del día seleccionado
    val eventsForSelectedDate: LiveData<List<Event>> = _selectedDate.switchMap { date ->
        eventRepository.getEventsByDate(date)
    }

    // Cambiar fecha seleccionada
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // Toggle completado
    fun toggleItemCompleted(item: Item) {
        viewModelScope.launch {
            itemRepository.setCompleted(item.id, !item.isCompleted)
        }
    }
}