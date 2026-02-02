package com.example.rapp.ui.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.rapp.data.model.Item
import com.example.rapp.data.repository.ItemRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class LandingViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    val favoriteItems: LiveData<List<Item>> = itemRepository.favoriteItems

    // Fechas que tienen items asignados
    val datesWithItems: LiveData<List<LocalDate>> = itemRepository.datesWithItems

    // Fecha seleccionada
    private val _selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    // Items del día seleccionado (se actualiza automáticamente cuando cambia selectedDate)
    val itemsForSelectedDate: LiveData<List<Item>> = _selectedDate.switchMap { date ->
        itemRepository.getItemsByDate(date)
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