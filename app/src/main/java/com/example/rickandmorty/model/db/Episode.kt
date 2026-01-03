package com.example.rickandmorty.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode")
data class Episode (
    @PrimaryKey
    val id: Int,
    val name: String
)