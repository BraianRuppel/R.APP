package com.example.rapp

import android.app.Application
import androidx.room.Room
import com.example.rapp.organizador.database.AppDatabase
import com.example.rapp.organizador.repository.ItemRepository

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
