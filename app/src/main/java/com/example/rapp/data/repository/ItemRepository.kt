package com.example.rapp.data.repository

import androidx.lifecycle.LiveData
import com.example.rapp.data.dao.ItemDao
import com.example.rapp.data.model.Item

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getAllItems()

    fun getItemsByGroup(groupId: Long): LiveData<List<Item>> {
        return itemDao.getItemsByGroup(groupId)
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
}