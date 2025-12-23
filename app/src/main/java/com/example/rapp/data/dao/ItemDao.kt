package com.example.rapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rapp.data.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
