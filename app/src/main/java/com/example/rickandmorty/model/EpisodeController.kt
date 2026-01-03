package com.example.rickandmorty.model

import com.example.rickandmorty.model.db.Episode
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodeController {

    @GET("episode")
    fun getEpisodesOnFirstPage(): Call<EpisodeServerAnswer>

    @GET("episode/{episodesIdList}")
    fun getEpisodesByIdList(@Path("episodesIdList") episodesIdList: String): Call<List<Episode>>
}