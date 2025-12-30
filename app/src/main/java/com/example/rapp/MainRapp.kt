package com.example.rapp

import android.app.Application
import androidx.room.Room
import com.example.rapp.data.database.AppDatabase
import com.example.rapp.data.repository.ItemRepository
import com.example.rapp.data.repository.GroupRepository

class MainRapp : Application() {
    lateinit var database: AppDatabase
    lateinit var itemRepository: ItemRepository
    lateinit var groupRepository: GroupRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database"
        ).build()
        itemRepository = ItemRepository(database.itemDao())
        groupRepository = GroupRepository(database.groupDao())
    }
}
