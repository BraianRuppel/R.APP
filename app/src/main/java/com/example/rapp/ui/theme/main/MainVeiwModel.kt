package com.example.rapp.ui.theme.main

import androidx.lifecycle.*
import com.example.rapp.organizador.model.Item
import com.example.rapp.organizador.repository.ItemRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ItemRepository) : ViewModel() {

    val items: LiveData<List<Item>> = repository.allItems

    fun addItem(name: String) {
        viewModelScope.launch {
            repository.insert(Item(name = name))
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
