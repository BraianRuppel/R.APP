package com.example.rapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class EventType {
    BIRTHDAY,       // Cumpleaños
    ANNIVERSARY,    // Aniversario
    HOLIDAY,        // Feriado/Festividad
    ONE_TIME        // Evento único
}

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val type: EventType,
    val isRepeatingYearly: Boolean = false,  // Se repite cada año
    val color: String = "#6200EE"            // Color para identificar
)