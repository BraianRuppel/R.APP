package com.example.rapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rapp.data.model.Event
import java.time.LocalDate

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE date = :date")
    fun getEventsByDate(date: LocalDate): LiveData<List<Event>>

    // Eventos próximos (los siguientes 30 días)
    @Query("SELECT * FROM events WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getEventsInRange(startDate: LocalDate, endDate: LocalDate): LiveData<List<Event>>

    // Para eventos repetibles, buscar por mes y día (ignorando el año)
    @Query("SELECT * FROM events WHERE isRepeatingYearly = 1")
    fun getRepeatingEvents(): LiveData<List<Event>>

    @Query("SELECT DISTINCT date FROM events")
    fun getDatesWithEvents(): LiveData<List<LocalDate>>

    @Insert
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)
}