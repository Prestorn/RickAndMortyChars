package com.example.rickandmorty.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface PageDao {

    @Query("SELECT * FROM page WHERE id = :id")
    fun getPageById(id: Int): Page?

    @Insert(onConflict = REPLACE)
    fun insert(pages: List<Page>)
}