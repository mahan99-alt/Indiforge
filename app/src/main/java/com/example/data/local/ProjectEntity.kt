package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val genre: String,
    val engine: String,
    val updatedAtMillis: Long,
    val payloadJson: String
)
