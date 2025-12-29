package com.example.rapp.data.repository

import androidx.lifecycle.LiveData
import com.example.rapp.data.dao.ItemDao
import com.example.rapp.data.model.Item

class ItemRepository(private val dao: ItemDao) {

    val allItems: LiveData<List<Item>> = dao.getAllItems()

    suspend fun insert(item: Item) = dao.insert(item)

    suspend fun delete(item: Item) = dao.delete(item)
}
