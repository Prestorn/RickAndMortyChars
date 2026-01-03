package com.example.rickandmorty.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page")
data class Page(
    @PrimaryKey
    var id: Int,
    var next: Int?,
    var prev: Int?
)