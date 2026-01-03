package com.example.rickandmorty.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface CharacterDao {

    @Insert(onConflict = REPLACE)
    fun insert(characters: List<LocalCharacter>)

    @RawQuery
    fun getCharactersWithFilters(query: SimpleSQLiteQuery): List<LocalCharacter>

    @Query("SELECT * FROM character WHERE id = :id")
    fun getCharacterById(id: Int): LocalCharacter
}