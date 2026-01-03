package com.example.rickandmorty.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE

@Dao
interface EpisodeDao {

    @Insert(onConflict = IGNORE)
    fun insert(episodes: List<Episode>)

}