package com.example.rickandmorty.model.db

data class LocalAnswer(
    val page: Page,
    val characters: List<LocalCharacter>
)