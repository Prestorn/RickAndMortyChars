package com.example.rickandmorty.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.model.CharacterRepository
import com.example.rickandmorty.model.EpisodeRepository
import com.example.rickandmorty.model.db.LocalAnswer
import com.example.rickandmorty.model.db.Page
import com.example.rickandmorty.model.db.LocalCharacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterListViewModel(application: Application) : AndroidViewModel(application) {

    val characterList: MutableLiveData<List<LocalCharacter>> = MutableLiveData()
    val serverAnswer: MutableLiveData<LocalAnswer> = MutableLiveData()
    var scrollPosition: Int = 0
    private val characterRepository: CharacterRepository =
        CharacterRepository(application.applicationContext, serverAnswer)
    private val episodeRepository: EpisodeRepository =
        EpisodeRepository(application.applicationContext)

    var currentPage: Page = Page(1, 2, null)

    init {
        serverAnswer.observeForever { answer -> onServerAnswerChanged(answer) }
    }

    fun updateCharacterListOnPage() {
        viewModelScope.launch(Dispatchers.IO) {
            characterRepository.updateViaRefresh()
        }
    }

    fun loadCharactersOnPage(
        pageId: Int,
        name: String? = null,
        statuses: List<Boolean> = listOf(),
        species: String? = null,
        type: String? = null,
        genders: List<Boolean> = listOf()
    ) {
        val statusesList: MutableList<String> = mutableListOf()
        if (statuses.isNotEmpty()) {
            if (statuses[0]) {
                statusesList.add("Alive")
            }
            if (statuses[1]) {
                statusesList.add("Dead")
            }
            if (statuses[2]) {
                statusesList.add("unknown")
            }
        }

        val genderList: MutableList<String> = mutableListOf()
        if (genders.isNotEmpty()) {
            if (genders[0]) {
                genderList.add("Male")
            }
            if (genders[1]) {
                genderList.add("Female")
            }
            if (genders[2]) {
                genderList.add("Genderless")
            }
            if (genders[3]) {
                genderList.add("unknown")
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            characterRepository.getCharactersOnPage(
                pageId = pageId,
                name = name,
                status = statusesList,
                species = species,
                type = type,
                gender = genderList
            )
        }
    }

    private fun onServerAnswerChanged(answer: LocalAnswer?) {
        if (answer != null) {
            currentPage = answer.page.copy()
            characterList.value = answer.characters
        } else {
            currentPage.next = currentPage.id + 1
            currentPage.prev = currentPage.id - 1
            characterList.value = listOf()
        }
    }

}