package com.example.rapp.organizador.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rapp.organizador.dao.ItemDao
import com.example.rapp.organizador.model.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
