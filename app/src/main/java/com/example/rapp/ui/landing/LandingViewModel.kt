package com.example.rapp.ui.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rapp.data.model.Item
import com.example.rapp.data.repository.ItemRepository

class LandingViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    val favoriteItems: LiveData<List<Item>> = itemRepository.favoriteItems
}