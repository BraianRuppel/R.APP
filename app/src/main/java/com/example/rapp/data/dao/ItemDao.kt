package com.example.rapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rapp.data.model.Item
import java.time.LocalDate

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE groupId = :groupId ORDER BY `order` ASC")
    fun getItemsByGroup(groupId: Long): LiveData<List<Item>>

    @Query("SELECT * FROM items ORDER BY `order` ASC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY `order` ASC")
    fun getFavoriteItems(): LiveData<List<Item>>

    // 🆕 Obtener items por fecha
    @Query("SELECT * FROM items WHERE dueDate = :date ORDER BY `order` ASC")
    fun getItemsByDate(date: LocalDate): LiveData<List<Item>>

    // 🆕 Obtener items con fecha asignada (para mostrar indicadores en calendario)
    @Query("SELECT DISTINCT dueDate FROM items WHERE dueDate IS NOT NULL")
    fun getDatesWithItems(): LiveData<List<LocalDate>>

    // 🆕 Obtener items pendientes (no completados) por fecha
    @Query("SELECT * FROM items WHERE dueDate = :date AND isCompleted = 0 ORDER BY `order` ASC")
    fun getPendingItemsByDate(date: LocalDate): LiveData<List<Item>>

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

    // 🆕 Actualizar fecha
    @Query("UPDATE items SET dueDate = :date WHERE id = :itemId")
    suspend fun updateDueDate(itemId: Long, date: LocalDate?)

    // 🆕 Marcar como completado
    @Query("UPDATE items SET isCompleted = :isCompleted WHERE id = :itemId")
    suspend fun updateCompleted(itemId: Long, isCompleted: Boolean)

    @Transaction
    suspend fun updateItemOrders(items: List<Item>) {
        items.forEachIndexed { index, item ->
            updateOrder(item.id, index)
        }
    }
}
