package com.example.rickandmorty.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.model.EpisodeRepository
import com.example.rickandmorty.model.db.Episode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterViewModel(application: Application): AndroidViewModel(application) {
    val episodeList: MutableLiveData<List<Episode>> = MutableLiveData()
    private val episodeRepository: EpisodeRepository = EpisodeRepository(application.applicationContext)

    fun loadEpisodesWithCharacter(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodeList.postValue(episodeRepository.getEpisodesWithCharacter(id))
        }
    }
}