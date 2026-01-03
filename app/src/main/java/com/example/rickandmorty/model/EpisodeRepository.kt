package com.example.rickandmorty.model

import android.content.Context
import com.example.rickandmorty.model.db.AppDatabase
import com.example.rickandmorty.model.db.CharacterInEpisodeDao
import com.example.rickandmorty.model.db.Episode
import com.example.rickandmorty.model.db.EpisodeDao


class EpisodeRepository(val applicationContext: Context) {
    private val retrofitService: RetrofitService = RetrofitService()
    private val database: AppDatabase = AppDatabase.getInstance(applicationContext)
    private val episodeDao: EpisodeDao = database.episodeDao()
    private val characterInEpisodeDao: CharacterInEpisodeDao = database.characterInEpisodeDao()

    suspend fun updateEpisodeList() {
        val episodes: List<Episode> = retrofitService.getAllEpisodes()
        if (episodes.isNotEmpty()) {
            episodeDao.insert(episodes)
        }
    }

    suspend fun getEpisodesWithCharacter(id: Int): List<Episode> {
        return characterInEpisodeDao.getAllEpisodesWithCharacter(id)
    }

}