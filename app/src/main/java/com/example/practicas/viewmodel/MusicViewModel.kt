package com.example.practicas.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicas.data.*
import com.example.practicas.deezer.DeezerClient
import com.example.practicas.model.*
import com.example.practicas.oAuth.TokenStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MusicRepository(application.applicationContext)
    private val dataStore = application.applicationContext.favoritesDataStore

    //preview de canciones
    private val _deezerPreviewUrl = MutableStateFlow<String?>(null)
    val deezerPreviewUrl = _deezerPreviewUrl.asStateFlow()

    // ========================
    // FAVORITOS
    // ========================

    private val _likedTracks = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val likedTracks: StateFlow<List<SpotifyTrack>> = _likedTracks

    init {
        viewModelScope.launch {
            dataStore.data.collect { file ->
                _likedTracks.value = file.tracks
            }
            loadImBored()
        }
    }

    fun toggleFavorite(track: SpotifyTrack) {
        val safeTrack = sanitizeTrack(track)

        val updated = if (isFavorite(track.id))
            _likedTracks.value.filterNot { it.id == track.id }
        else listOf(safeTrack) + _likedTracks.value

        saveFavorites(updated)
    }

    fun removeFavorite(track: SpotifyTrack) {
        saveFavorites(_likedTracks.value.filterNot { it.id == track.id })
    }

    fun isFavorite(trackId: String): Boolean =
        _likedTracks.value.any { it.id == trackId }

    private fun saveFavorites(list: List<SpotifyTrack>) {
        viewModelScope.launch {
            dataStore.updateData { FavoritesFile(list) }
        }
    }

    // ========================
    // PLAYLISTS
    // ========================

    private val _playlistTracks = MutableStateFlow<List<SpotifyPlaylistTrackItem>>(emptyList())
    val playlistTracks: StateFlow<List<SpotifyPlaylistTrackItem>> = _playlistTracks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    // ========================
    // BUSQUEDA
    // ========================

    private val _searchResults = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val searchResults: StateFlow<List<SpotifyTrack>> = _searchResults

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

    // ========================
    // TRACK DETAIL + LETRAS
    // ========================

    private val _selectedTrack = MutableStateFlow<SpotifyTrack?>(null)
    val selectedTrack: StateFlow<SpotifyTrack?> = _selectedTrack

    private val _lyricsOriginal = MutableStateFlow("")
    val lyricsOriginal: StateFlow<String> = _lyricsOriginal

    private val _lyricsTranslated = MutableStateFlow("")
    val lyricsTranslated: StateFlow<String> = _lyricsTranslated

    private val _isLoadingLyrics = MutableStateFlow(false)
    val isLoadingLyrics: StateFlow<Boolean> = _isLoadingLyrics

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectTrack(context: Context, track: SpotifyTrack) {
        _selectedTrack.value = track
        _lyricsOriginal.value = "_loading_"
        _lyricsTranslated.value = "_loading_"
        loadLyrics(track)
    }

    fun clearSelectedTrack() {
        _selectedTrack.value = null
        _lyricsOriginal.value = ""
        _lyricsTranslated.value = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadLyrics(track: SpotifyTrack) {
        val ctx = getApplication<Application>().applicationContext
        val lyricsRepo = LyricsRepository(ctx)

        viewModelScope.launch {
            _isLoadingLyrics.value = true
            try {
                val original = lyricsRepo.getLyrics(track)
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

    // ========================
    // PERFIL /me
    // ========================

    private val _userProfile = MutableStateFlow<SpotifyUserProfile?>(null)
    val userProfile: StateFlow<SpotifyUserProfile?> = _userProfile

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = repo.getCurrentUserProfile()
                Log.d("PROFILE", "Perfil cargado correctamente.")
                _userProfile.value = profile
            } catch (e: Exception) {
                Log.e("PROFILE", "Error /me: ${e.message}")
            }
        }
    }

    fun logout() {
        TokenStore.clear()
        _userProfile.value = null
    }

    // ========================
    // deezer PREVIEW
    // ========================
    fun loadDeezerPreviewForTrack(trackName: String, artistName: String?) {
        viewModelScope.launch {
            try {
                val cleanName = normalizeTrackName(trackName)
                val query1 = if (!artistName.isNullOrBlank()) "$cleanName $artistName" else cleanName

                var preview = trySearchDeezer(query1)

                if (preview.isNullOrBlank()) {
                    preview = trySearchDeezer(cleanName)
                }

                if (preview.isNullOrBlank() && !artistName.isNullOrBlank()) {

                    preview = trySearchFuzzy(cleanName, artistName)
                }

                if (preview.isNullOrBlank()) {
                    _deezerPreviewUrl.value = null
                } else {
                    _deezerPreviewUrl.value = preview
                        .replace("\n", "")
                        .replace("\r", "")
                        .trim()

                }

            } catch (e: Exception) {
                _deezerPreviewUrl.value = null
            }
        }
    }

    private suspend fun trySearchDeezer(query: String): String? {
        return try {
            val resp = DeezerClient.api.searchTrack(query)
            resp.data.firstOrNull()?.preview
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun trySearchFuzzy(cleanName: String, artist: String): String? {
        return try {
            val resp = DeezerClient.api.searchTrack(artist)
            val match = resp.data.firstOrNull { it.title.equals(cleanName, ignoreCase = true) }
            match?.preview
        } catch (e: Exception) {
            null
        }
    }




    // ========================
    // ESTADÍSTICAS EXTENDIDAS
    // ========================

    private val _playlistCount = MutableStateFlow(0)
    val playlistCount: StateFlow<Int> = _playlistCount

    private val _dominantGenre = MutableStateFlow("Indefinido")
    val dominantGenre: StateFlow<String> = _dominantGenre

    private val _followers = MutableStateFlow(0)
    val followers: StateFlow<Int> = _followers

    fun loadExtraProfileData() {
        viewModelScope.launch {

            try {
                val profile = _userProfile.value
                _followers.value = profile?.followers?.total ?: 0

                val playlistResult = repo.getUserPlaylists(limit = 50)
                _playlistCount.value = playlistResult.total

                // ---- TOP ARTISTS ----
                val topArtists = repo.getTopArtists()

                val allGenres = topArtists.flatMap { it.genres }

                val dominant = if (allGenres.isNotEmpty()) {
                    allGenres.groupingBy { it }
                        .eachCount()
                        .maxByOrNull { it.value }
                        ?.key
                } else {
                    inferGenreSmart(topArtists.map { it.name })
                }

                _dominantGenre.value = dominant ?: "Indefinido"

            } catch (e: Exception) {
                _dominantGenre.value = "Indefinido"
            }
        }
    }



    private fun inferGenreSmart(names: List<String>): String {

        val patterns = listOf(
            "reggaeton" to "Reggaeton / Urbano",
            "trap" to "Trap Latino",
            "mex" to "Regional Mexicano",
            "corr" to "Corridos",
            "rock" to "Rock",
            "metal" to "Metal",
            "pop" to "Pop",
            "hip" to "Hip-Hop / Rap",
            "rap" to "Hip-Hop / Rap",
            "kpop" to "K-pop",
            "edm" to "EDM / Electrónica"
        )


        val nameString = names.joinToString(" ").lowercase()

        // 1️coincidencias directas
        for ((key, value) in patterns) {
            if (nameString.contains(key)) return value
        }

        // 2️coincidencias parciales mejoradas
        val urb = listOf("bunny", "feid", "karol", "rauw", "anuel")
        if (urb.any { nameString.contains(it) }) return "Reggaeton / Urbano"

        val mexico = listOf("peso", "pluma", "natanael", "junior h", "firme", "requinto")
        if (mexico.any { nameString.contains(it) }) return "Regional Mexicano"

        val pop = listOf("dua", "ariana", "olivia", "swift")
        if (pop.any { nameString.contains(it) }) return "Pop"

        val rock = listOf("muse", "queen", "metallica")
        if (rock.any { nameString.contains(it) }) return "Rock / Metal"

        // fallback
        return "Indefinido"
    }
    private fun sanitizeTrack(t: SpotifyTrack): SpotifyTrack {
        return t.copy(
            artists = t.artists.filterNotNull().map { art ->
                SpotifyArtist(
                    id = art.id,
                    name = art.name,
                    genres = art.genres ?: emptyList(),
                    images = art.images ?: emptyList()
                )
            },
            album = t.album.copy(
                artists = t.album.artists?.filterNotNull()?.map { art ->
                    SpotifyArtist(
                        id = art.id,
                        name = art.name,
                        genres = art.genres ?: emptyList(),
                        images = art.images ?: emptyList()
                    )
                } ?: emptyList(),
                images = t.album.images ?: emptyList()
            )
        )
    }

    private fun normalizeTrackName(name: String): String {
        return name
            .replace(Regex("\\(.*?\\)"), "")   // quita (feat..), (Remaster), etc.
            .replace(Regex("feat\\.?|ft\\.?", RegexOption.IGNORE_CASE), "")
            .replace(Regex("-.*"), "")        // quita cosas después de "-"
            .trim()
    }



}
