package com.example.rapp.organizador.repository

import androidx.lifecycle.LiveData
import com.example.rapp.organizador.dao.ItemDao
import com.example.rapp.organizador.model.Item

class ItemRepository(private val dao: ItemDao) {

    val allItems: LiveData<List<Item>> = dao.getAllItems()

    suspend fun insert(item: Item) = dao.insert(item)

    suspend fun delete(item: Item) = dao.delete(item)
}
