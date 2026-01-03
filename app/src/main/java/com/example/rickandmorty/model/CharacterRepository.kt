package com.example.rickandmorty.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rickandmorty.model.db.AppDatabase
import com.example.rickandmorty.model.db.CharacterDao
import com.example.rickandmorty.model.db.CharacterInEpisode
import com.example.rickandmorty.model.db.CharacterInEpisodeDao
import com.example.rickandmorty.model.db.LocalAnswer
import com.example.rickandmorty.model.db.LocalCharacter
import com.example.rickandmorty.model.db.Page
import com.example.rickandmorty.model.db.PageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class CharacterRepository(
    val applicationContext: Context,
    val result: MutableLiveData<LocalAnswer>
) {
    private val MAX_REQUEST_COUNT: Int = 3
    private val retrofitService: RetrofitService = RetrofitService()
    private val serverAnswer: MutableLiveData<CharacterServerAnswer> = MutableLiveData()
    private var pageId: Int = 1
    private val database: AppDatabase = AppDatabase.getInstance(applicationContext)
    private val characterDao: CharacterDao = database.characterDao()
    private val pageDao: PageDao = database.pageDao()
    private val characterInEpisodeDao: CharacterInEpisodeDao = database.characterInEpisodeDao()
    private var pageFromLocalDB: Page? = Page(0, null, null)
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var requestCount: Int = 0
    private var isRefresh: Boolean = false
    private val episodeRepository: EpisodeRepository = EpisodeRepository(applicationContext)


    init {
        serverAnswer.observeForever { onServerAnswerChanged(it) }
    }

    suspend fun getCharactersOnPage(
        pageId: Int,
        name: String? = null,
        status: List<String> = listOf(),
        species: String? = null,
        type: String? = null,
        gender: List<String> = listOf()
    ) {
        this.pageId = pageId
        pageFromLocalDB = pageDao.getPageById(pageId)
        isRefresh = false

        if (pageFromLocalDB == null) {
            updateCharacterListOnPage()
        } else {
            val queryBuilder: StringBuilder =
                StringBuilder("SELECT * FROM character WHERE page = $pageId")
            val queryArgs: MutableList<String> = mutableListOf()

            if (!name.isNullOrEmpty()) {
                queryBuilder.append(" AND name LIKE ?")
                queryArgs.add("%$name%")
            }
            if (status.isNotEmpty()) {
                val placeholders = status.joinToString(", ") { "?" }
                queryBuilder.append(" AND status IN ($placeholders)")
                queryArgs.addAll(status)
            }
            if (!species.isNullOrEmpty()) {
                queryBuilder.append(" AND species LIKE ?")
                queryArgs.add("%$species%")
            }
            if (!type.isNullOrEmpty()) {
                queryBuilder.append(" AND type LIKE ?")
                queryArgs.add("%$type%")
            }
            if (gender.isNotEmpty()) {
                val placeholders = gender.joinToString(", ") { "?" }
                queryBuilder.append(" AND gender IN ($placeholders)")
                queryArgs.addAll(gender)
            }

            val query = SimpleSQLiteQuery(queryBuilder.toString(), queryArgs.toTypedArray())
            val charactersOnPage: List<LocalCharacter> =
                characterDao.getCharactersWithFilters(query)
            result.postValue(
                LocalAnswer(
                    pageFromLocalDB!!,
                    charactersOnPage
                )
            )
        }
    }


    suspend fun updateCharacterListOnPage() {
        requestCount++
        retrofitService.getCharactersOnPage(pageId, serverAnswer)
    }

    suspend fun updateViaRefresh() {
        isRefresh = true
        requestCount++
        retrofitService.getCharactersOnPage(pageId, serverAnswer)
    }

    private fun onServerAnswerChanged(serverAnswer: CharacterServerAnswer?) {
        if (serverAnswer != null) {
            requestCount = 0
            val page: Page = createPageByServerAnswer(serverAnswer)
            val characterList: List<LocalCharacter> =
                createCharacterListByServerAnswer(serverAnswer)
            val localAnswer: LocalAnswer = LocalAnswer(page, characterList)
            result.postValue(localAnswer)

            repositoryScope.launch {
                pageDao.insert(listOf(page))
                characterDao.insert(characterList)
                episodeRepository.updateEpisodeList()

                val characterInEpisodeList: MutableList<CharacterInEpisode> = mutableListOf()

                for (character in serverAnswer.results) {
                    for (episode in character.episode) {
                        characterInEpisodeList.add(
                            CharacterInEpisode(
                                character.id,
                                getEpisodeNumber(episode)
                            )
                        )
                    }
                }

                characterInEpisodeDao.insert(characterInEpisodeList)


            }
        } else {
            if (requestCount < MAX_REQUEST_COUNT) {
                repositoryScope.launch { updateCharacterListOnPage() }
            } else if (isRefresh) {
                repositoryScope.launch { getCharactersOnPage(pageId) }
            } else {
                result.postValue(null)
            }
        }
    }

    private fun createPageByServerAnswer(serverAnswer: CharacterServerAnswer): Page {
        val page: Page = Page(0, null, null)
        page.id = this.pageId
        if (serverAnswer.info.next != null) {
            page.next = getPageNumber(serverAnswer.info.next)
        }

        if (serverAnswer.info.prev != null) {
            page.prev = getPageNumber(serverAnswer.info.prev)
        }

        return page
    }

    private fun createCharacterListByServerAnswer(serverAnswer: CharacterServerAnswer): List<LocalCharacter> {
        val characterList: MutableList<LocalCharacter> = mutableListOf()
        for (character in serverAnswer.results) {
            characterList.add(character.toLocalCharacter(pageId))
        }
        return characterList
    }


    private fun getPageNumber(url: String): Int {
        for (i in url.indices.reversed()) {
            if (url[i] == '=') {
                return url.substring(i + 1).toInt()
            }
        }

        return -1
    }

    private fun getEpisodeNumber(url: String): Int {
        for (i in url.indices.reversed()) {
            if (url[i] == '/') {
                return url.substring(i + 1).toInt()
            }
        }

        return -1
    }

}