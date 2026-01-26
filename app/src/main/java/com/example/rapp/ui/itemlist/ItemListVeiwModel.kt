package com.example.rapp.ui.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rapp.data.model.Group
import com.example.rapp.data.model.GroupWithItems
import com.example.rapp.data.model.Item
import com.example.rapp.data.repository.GroupRepository
import com.example.rapp.data.repository.ItemRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class ItemListViewModel(
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val items: LiveData<List<Item>> = itemRepository.allItems
    val groupsWithItems: LiveData<List<GroupWithItems>> = groupRepository.allGroupsWithItems

    // ==================== OPERACIONES DE GRUPO ====================

    fun addGroup(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            groupRepository.insert(name)
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.delete(group)
        }
    }

    fun toggleGroupExpanded(groupId: Long, isExpanded: Boolean) {
        viewModelScope.launch {
            groupRepository.toggleExpanded(groupId, isExpanded)
        }
    }

    fun updateGroupOrders(groups: List<Group>) {
        viewModelScope.launch {
            groupRepository.updateOrders(groups)
        }
    }

    // ==================== OPERACIONES DE ITEM ====================

    fun addItem(text: String, groupId: Long) {
        if (text.isBlank()) return
        viewModelScope.launch {
            itemRepository.insert(text, groupId)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.delete(item)
        }
    }

    fun updateItemOrders(items: List<Item>) {
        viewModelScope.launch {
            itemRepository.updateOrders(items)
        }
    }

    fun moveItemToGroup(itemId: Long, newGroupId: Long, newOrder: Int) {
        viewModelScope.launch {
            itemRepository.moveToGroup(itemId, newGroupId, newOrder)
        }
    }

    fun toggleFavorite(item: Item) {
        viewModelScope.launch {
            itemRepository.toggleFavorite(item.id, !item.isFavorite)
        }
    }

    fun setItemDueDate(item: Item, date: LocalDate?) {
        viewModelScope.launch {
            itemRepository.setDueDate(item.id, date)
        }
    }

    fun toggleItemCompleted(item: Item) {
        viewModelScope.launch {
            itemRepository.setCompleted(item.id, !item.isCompleted)
        }
    }
}