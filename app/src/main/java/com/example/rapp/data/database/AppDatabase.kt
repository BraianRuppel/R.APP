package com.example.rapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rapp.data.dao.ItemDao
import com.example.rapp.data.model.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
