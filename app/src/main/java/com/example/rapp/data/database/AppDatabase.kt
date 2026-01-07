package com.example.rapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rapp.data.dao.GroupDao
import com.example.rapp.data.dao.ItemDao
import com.example.rapp.data.model.Group
import com.example.rapp.data.model.Item

@Database(
    entities = [Group::class, Item::class],
    version = 3,  // Incrementar versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Se utiliza para migraciones en desarrollo, pero no deberia utilizarce en PROD.
                    .fallbackToDestructiveMigration()  // Para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
