package com.example.rapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class GroupWithItems(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val items: List<Item>
)