package com.example.rickandmorty.model.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "character_episode_join",
    primaryKeys = ["characterId", "episodeId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalCharacter::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Episode::class,
            parentColumns = ["id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CharacterInEpisode(
    val characterId: Int,
    val episodeId: Int
)