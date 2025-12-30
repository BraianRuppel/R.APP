package com.example.rapp.data.repository

import androidx.lifecycle.LiveData
import com.example.rapp.data.dao.GroupDao
import com.example.rapp.data.model.Group
import com.example.rapp.data.model.GroupWithItems

class GroupRepository(private val groupDao: GroupDao) {

    val allGroups: LiveData<List<Group>> = groupDao.getAllGroups()

    val allGroupsWithItems: LiveData<List<GroupWithItems>> = groupDao.getAllGroupsWithItems()

    suspend fun insert(name: String) {
        groupDao.insert(Group(name = name))
    }

    suspend fun update(group: Group) {
        groupDao.update(group)
    }

    suspend fun delete(group: Group) {
        groupDao.delete(group)
    }

    suspend fun toggleExpanded(groupId: Long, isExpanded: Boolean) {
        groupDao.updateExpanded(groupId, isExpanded)
    }

    suspend fun updateOrders(groups: List<Group>) {
        groupDao.updateGroupOrders(groups)
    }
}