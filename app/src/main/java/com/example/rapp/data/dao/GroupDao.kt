package com.example.rapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rapp.data.model.Group
import com.example.rapp.data.model.GroupWithItems

@Dao
interface GroupDao {

    @Transaction
    @Query("SELECT * FROM `groups` ORDER BY `order` ASC")
    fun getAllGroupsWithItems(): LiveData<List<GroupWithItems>>

    @Query("SELECT * FROM `groups` ORDER BY `order` ASC")
    fun getAllGroups(): LiveData<List<Group>>

    @Insert
    suspend fun insert(group: Group): Long

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun delete(group: Group)

    @Query("UPDATE groups SET isExpanded = :isExpanded WHERE id = :groupId")
    suspend fun updateExpanded(groupId: Long, isExpanded: Boolean)

    @Query("UPDATE groups SET `order` = :order WHERE id = :groupId")
    suspend fun updateOrder(groupId: Long, order: Int)

    @Transaction
    suspend fun updateGroupOrders(groups: List<Group>) {
        groups.forEachIndexed { index, group ->
            updateOrder(group.id, index)
        }
    }
}