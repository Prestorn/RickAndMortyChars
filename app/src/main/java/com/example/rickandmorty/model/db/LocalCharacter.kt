package com.example.rickandmorty.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character")
data class LocalCharacter(
    @PrimaryKey
    val id: Int,
    val page: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: String,
    val location: String,
    val image: String,
    val url: String,
    val created: String
)