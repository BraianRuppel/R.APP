package com.example.rapp.data.repository

import androidx.lifecycle.LiveData
import com.example.rapp.data.dao.ItemDao
import com.example.rapp.data.model.Item
import java.time.LocalDate

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getAllItems()
    val favoriteItems: LiveData<List<Item>> = itemDao.getFavoriteItems()
    val datesWithItems: LiveData<List<LocalDate>> = itemDao.getDatesWithItems()

    fun getItemsByGroup(groupId: Long): LiveData<List<Item>> {
        return itemDao.getItemsByGroup(groupId)
    }

    fun getItemsByDate(date: LocalDate): LiveData<List<Item>> {
        return itemDao.getItemsByDate(date)
    }

    fun getPendingItemsByDate(date: LocalDate): LiveData<List<Item>> {
        return itemDao.getPendingItemsByDate(date)
    }

    suspend fun insert(text: String, groupId: Long) {
        itemDao.insert(Item(text = text, groupId = groupId))
    }

    suspend fun update(item: Item) {
        itemDao.update(item)
    }

    suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

    suspend fun updateOrders(items: List<Item>) {
        itemDao.updateItemOrders(items)
    }

    suspend fun moveToGroup(itemId: Long, newGroupId: Long, newOrder: Int) {
        itemDao.moveItem(itemId, newGroupId, newOrder)
    }

    suspend fun toggleFavorite(itemId: Long, isFavorite: Boolean) {
        itemDao.updateFavorite(itemId, isFavorite)
    }

    suspend fun setDueDate(itemId: Long, date: LocalDate?) {
        itemDao.updateDueDate(itemId, date)
    }

    suspend fun setCompleted(itemId: Long, isCompleted: Boolean) {
        itemDao.updateCompleted(itemId, isCompleted)
    }
}