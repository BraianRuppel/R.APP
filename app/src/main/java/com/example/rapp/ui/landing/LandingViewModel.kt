package com.example.rapp.ui.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rapp.data.model.Item
import com.example.rapp.data.repository.ItemRepository
import java.time.LocalDate

class LandingViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    val favoriteItems: LiveData<List<Item>> = itemRepository.favoriteItems

    // Fechas que tienen items asignados
    val datesWithItems: LiveData<List<LocalDate>> = itemRepository.datesWithItems

    // Obtener items por fecha
    fun getItemsByDate(date: LocalDate): LiveData<List<Item>> {
        return itemRepository.getItemsByDate(date)
    }
}