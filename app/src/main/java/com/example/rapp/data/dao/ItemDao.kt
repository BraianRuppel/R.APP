package com.example.rapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rapp.data.model.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE groupId = :groupId ORDER BY `order` ASC")
    fun getItemsByGroup(groupId: Long): LiveData<List<Item>>

    @Query("SELECT * FROM items ORDER BY `order` ASC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY `order` ASC")
    fun getFavoriteItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE items SET `order` = :order WHERE id = :itemId")
    suspend fun updateOrder(itemId: Long, order: Int)

    @Query("UPDATE items SET groupId = :groupId, `order` = :order WHERE id = :itemId")
    suspend fun moveItem(itemId: Long, groupId: Long, order: Int)

    @Query("UPDATE items SET isFavorite = :isFavorite WHERE id = :itemId")
    suspend fun updateFavorite(itemId: Long, isFavorite: Boolean)

    @Transaction
    suspend fun updateItemOrders(items: List<Item>) {
        items.forEachIndexed { index, item ->
            updateOrder(item.id, index)
        }
    }
}
