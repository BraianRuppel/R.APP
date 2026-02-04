package com.example.rapp.data.database

import androidx.room.TypeConverter
import com.example.rapp.data.model.EventType
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    // 🆕 Converters para EventType
    @TypeConverter
    fun fromEventType(type: EventType): String {
        return type.name
    }

    @TypeConverter
    fun toEventType(typeName: String): EventType {
        return EventType.valueOf(typeName)
    }
}