package com.example.rickandmorty.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rickandmorty.model.db.Episode
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    private val connectionURL: String = "https://rickandmortyapi.com/api/"
    private val TIMEOUT_SECONDS: Long = 5

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(connectionURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val characterController: CharacterController =
        retrofit.create(CharacterController::class.java)
    private val episodeController: EpisodeController =
        retrofit.create(EpisodeController::class.java)

    fun getCharactersOnPage(pageNumber: Int, result: MutableLiveData<CharacterServerAnswer>) {
        val call: Call<CharacterServerAnswer> = characterController.getCharactersOnPage(pageNumber)
        call.enqueue(object : Callback<CharacterServerAnswer> {

            override fun onResponse(
                call: Call<CharacterServerAnswer?>,
                response: Response<CharacterServerAnswer?>
            ) {
                if (response.isSuccessful) {
                    val answer: CharacterServerAnswer? = response.body()
                    result.value = answer
                }
            }

            override fun onFailure(
                call: Call<CharacterServerAnswer?>,
                t: Throwable
            ) {
                Log.e(
                    "Request is failed",
                    "${t.cause}\n\n${t.message}\n\n${t.stackTrace.joinToString("\n")}"
                )
                Log.i("CALL IS EXECUTED", "${call.isExecuted}")
                result.value = null
            }
        })

    }

    suspend fun getAllEpisodes(): List<Episode> {
        try {
            val answer: Response<EpisodeServerAnswer?> =
                episodeController.getEpisodesOnFirstPage().execute()
            val episodesCount: Int = answer.body()!!.info.count
            val episodesIds = (1..episodesCount).joinToString(",")

            return episodeController.getEpisodesByIdList(episodesIds).execute().body()!!
        } catch (e: Exception) {
            Log.e(
                "Request is failed",
                "${e.cause}\n\n${e.message}\n\n${e.stackTrace.joinToString("\n")}"
            )
            return listOf()
        }
    }

}
