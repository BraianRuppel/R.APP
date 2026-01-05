package com.example.rapp.ui.itemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rapp.data.repository.GroupRepository
import com.example.rapp.data.repository.ItemRepository

class ItemListViewModelFactory(
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
            return ItemListViewModel(groupRepository, itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
