package com.example.rickandmorty.model

import com.example.rickandmorty.model.db.LocalCharacter

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String

) {
    data class Origin(
        val name: String,
        val url: String
    )

    data class Location(
        val id: Int,
        val name: String,
        val type: String,
        val dimension: String,
        val residents: List<String>,
        val url: String,
        val created: String
    )

    fun toLocalCharacter(page: Int): LocalCharacter {
        return LocalCharacter(
            id = this.id,
            page = page,
            name = this.name,
            status = this.status,
            species = this.species,
            type = this.type,
            gender = this.gender,
            origin = this.origin.name,
            location = this.location.name,
            image = this.image,
            url = this.url,
            created = this.created
        )
    }

}