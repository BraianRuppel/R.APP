package com.example.rapp

import android.app.Application
import androidx.room.Room
import com.example.rapp.data.database.AppDatabase
import com.example.rapp.data.repository.ItemRepository

class MainRapp : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: ItemRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database"
        ).build()
        repository = ItemRepository(database.itemDao())
    }
}
