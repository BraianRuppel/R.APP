package com.example.rapp.organizador.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rapp.organizador.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
