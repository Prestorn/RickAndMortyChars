package com.example.rickandmorty.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface CharacterInEpisodeDao {

    @Insert(onConflict = REPLACE)
    fun insert(characterInEpisode: List<CharacterInEpisode>)

    @Query(
        "SELECT episode.id, episode.name " +
                "FROM character_episode_join INNER JOIN episode ON " +
                "character_episode_join.episodeId = episode.id " +
                "WHERE character_episode_join.characterId = :characterId"
    )
    fun getAllEpisodesWithCharacter(characterId: Int): List<Episode>

}