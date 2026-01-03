package com.example.rickandmorty.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterController {

    @GET("character/")
    fun getCharactersOnPage(@Query("page") pageNumber: Int): Call<CharacterServerAnswer>
}