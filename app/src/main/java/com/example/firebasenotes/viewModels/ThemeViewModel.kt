package com.example.firebasenotes.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasenotes.datastore.ThemePreference
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val pref = ThemePreference(application)

    val isDarkMode = pref.isDarkMode.asLiveData()

    fun toggleTheme() {
        viewModelScope.launch {
            val current = isDarkMode.value ?: false
            pref.setDarkMode(!current)
        }
    }
}
