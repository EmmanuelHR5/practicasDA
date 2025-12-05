package com.example.practicas.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicas.apple.AppleMusicClient
import com.example.practicas.apple.AppleTokenProvider
import com.example.practicas.data.*
import com.example.practicas.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = MusicRepository(application.applicationContext)
    private val dataStore = application.applicationContext.favoritesDataStore

    // --------------------
    // FAVORITOS (NUEVO)
    // --------------------
    private val _likedTracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val likedTracks: StateFlow<List<SpotifyTrack>> = _likedTracks

    init {
        // Cargar favoritos iniciales desde DataStore
        viewModelScope.launch {
            dataStore.data.collect { file ->
                _likedTracks.value = file.tracks
            }
        }
    }

    fun toggleFavorite(track: SpotifyTrack) {
        val current = _likedTracks.value

        val updated =
            if (current.any { it.id == track.id })
                current.filterNot { it.id == track.id }
            else
                listOf(track) + current  // agrega al inicio

        saveFavorites(updated)
    }

    fun removeFavorite(track: SpotifyTrack) {
        saveFavorites(_likedTracks.value.filterNot { it.id == track.id })
    }

    fun isFavorite(trackId: String): Boolean {
        return _likedTracks.value.any { it.id == trackId }
    }

    private fun saveFavorites(list: List<SpotifyTrack>) {
        viewModelScope.launch {
            dataStore.updateData { FavoritesFile(tracks = list) }
        }
    }

    // --------------------
    // PLAYLISTS
    // --------------------

    private val _playlistTracks = MutableStateFlow<List<SpotifyPlaylistTrackItem>>(emptyList())
    val playlistTracks: StateFlow<List<SpotifyPlaylistTrackItem>> = _playlistTracks

    private val _searchResults = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val searchResults: StateFlow<List<SpotifyTrack>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedTrack = MutableStateFlow<SpotifyTrack?>(null)
    val selectedTrack: StateFlow<SpotifyTrack?> = _selectedTrack

    private val _lyricsOriginal = MutableStateFlow("")
    val lyricsOriginal: StateFlow<String> = _lyricsOriginal

    private val _lyricsTranslated = MutableStateFlow("")
    val lyricsTranslated: StateFlow<String> = _lyricsTranslated

    private val _isLoadingLyrics = MutableStateFlow(false)
    val isLoadingLyrics: StateFlow<Boolean> = _isLoadingLyrics

    private val _userProfile = MutableStateFlow<SpotifyUserProfile?>(null)
    val userProfile: StateFlow<SpotifyUserProfile?> = _userProfile

    var currentPlaylistTitle: String = "IDK IM BORED"
        private set

    fun loadImBored() {
        currentPlaylistTitle = "PLAYLIST IM BORED"
        loadPlaylist(SpotifyConfig.PLAYLIST_idk_imbored)
    }

    fun loadNostalgicHits() {
        currentPlaylistTitle = "NOSTALGIC HITS"
        loadPlaylist(SpotifyConfig.PLAYLIST_NOSTALGIC_HITS)
    }

    fun loadGymTraining() {
        currentPlaylistTitle = "GYM TRAINING"
        loadPlaylist(SpotifyConfig.PLAYLIST_GYM_TRAINING)
    }

    fun loadShesIsJustAGirl() {
        currentPlaylistTitle = "SHE IS JUST A GIRL"
        loadPlaylist(SpotifyConfig.PLAYLIST_SHE_IS_JUST_A_GIRL)
    }

    fun loadFavorito() {
        currentPlaylistTitle = "FAVORITO"
        loadPlaylist(SpotifyConfig.PLAYLIST_FAVORITO)
    }

    private fun loadPlaylist(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _playlistTracks.value = repo.getPlaylistTracks(id)
                clearSelectedTrack()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --------------------
    // SEARCH
    // --------------------

    fun search(query: String) {
        if (query.isBlank()) return clearSearch()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _searchResults.value = repo.searchTracks(query)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    // --------------------
    // TRACK DETAIL + LYRICS
    // --------------------

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectTrack(track: SpotifyTrack) {
        viewModelScope.launch {
            val full = repo.getTrackDetail(track.id)
            _selectedTrack.value = full
            loadLyrics(full)
        }
    }

    fun clearSelectedTrack() {
        _selectedTrack.value = null
        _lyricsOriginal.value = ""
        _lyricsTranslated.value = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadLyrics(track: SpotifyTrack) {
        val ctx = getApplication<Application>().applicationContext
        val lyricsRepo = LyricsRepository(ctx)

        viewModelScope.launch {
            _isLoadingLyrics.value = true
            _lyricsOriginal.value = "_loading_"
            _lyricsTranslated.value = "_loading_"

            try {
                val original = withContext(Dispatchers.Main) {
                    lyricsRepo.getLyrics(track)
                }
                _lyricsOriginal.value = original

                val translated = withContext(Dispatchers.IO) {
                    repo.translateWithDeepL(original, "ES")
                }
                _lyricsTranslated.value = translated

            } finally {
                _isLoadingLyrics.value = false
            }
        }
    }

    // --------------------
    // PROFILE
    // --------------------

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = repo.getCurrentUserProfile()
        }
    }

    suspend fun fetchApplePreview(trackName: String, artist: String): String? {
        return try {
            val token = "Bearer ${AppleTokenProvider.getToken()}"
            val response = AppleMusicClient.api.searchSong(
                token = token,
                query = "$trackName $artist"
            )

            response.results.songs?.data?.firstOrNull()
                ?.attributes?.previewUrl

        } catch (e: Exception) {
            null
        }
    }

}
