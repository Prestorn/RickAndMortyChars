package com.example.rickandmorty

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        clearSharedPrefs()
    }

    private fun clearSharedPrefs() {
        val sharedPreferences: SharedPreferences =
            applicationContext.getSharedPreferences("SharedPreferences", MODE_PRIVATE)
        sharedPreferences.edit { putInt("currentPageId", 1) }
        sharedPreferences.edit { putBoolean("aliveStatusCB", false) }
        sharedPreferences.edit { putBoolean("deadStatusCB", false) }
        sharedPreferences.edit { putBoolean("unknownStatusCB", false) }
        sharedPreferences.edit { putString("speciesFilterET","") }
        sharedPreferences.edit { putString("typeFilterET", "") }
        sharedPreferences.edit { putBoolean("male_gender", false) }
        sharedPreferences.edit { putBoolean("femaleGenderCB", false) }
        sharedPreferences.edit { putBoolean("genderlessGenderCB", false) }
        sharedPreferences.edit { putBoolean("unknownGenderCB", false) }
        sharedPreferences.edit { putString("characterNameFilter", "") }
    }
}