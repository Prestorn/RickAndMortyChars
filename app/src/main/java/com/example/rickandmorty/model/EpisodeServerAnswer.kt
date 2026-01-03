package com.example.rickandmorty.model

import com.example.rickandmorty.model.db.Episode

data class EpisodeServerAnswer(
    val info: Info,
    val results: List<Episode>
) {
    data class Info(
        val count: Int,
        val pages: Int,
        val next: String?,
        val prev: String?
    )
}