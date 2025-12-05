package com.example.practicas.model

import kotlinx.serialization.Serializable

// ---------------- PLAYLISTS ----------------

@Serializable
data class SpotifyPlaylistTracksResponse(
    val href: String,
    val items: List<SpotifyPlaylistTrackItem>,
    val limit: Int,
    val next: String? = null,
    val offset: Int,
    val previous: String? = null,
    val total: Int
)

@Serializable
data class SpotifyPlaylistTrackItem(
    val added_at: String? = null,
    val track: SpotifyTrack
)

// ---------------- SEARCH ----------------

@Serializable
data class SpotifySearchResponse(
    val tracks: SpotifyTracksPage
)

@Serializable
data class SpotifyTracksPage(
    val items: List<SpotifyTrack>
)

// ---------------- TRACK ----------------

@Serializable
data class SpotifyTrack(
    val id: String,
    val name: String,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum,
    val duration_ms: Int,
    val popularity: Int,
    val preview_url: String? = null // ✔ corregido (sin coma sobrante)
)

// ---------------- ARTIST ----------------

@Serializable
data class SpotifyArtist(
    val id: String,
    val name: String
)

// ---------------- ALBUM ----------------

@Serializable
data class SpotifyAlbum(
    val album_type: String,
    val artists: List<SpotifyArtist>,
    val available_markets: List<String> = emptyList(),
    val id: String,
    val images: List<SpotifyImage> = emptyList(),
    val name: String,
    val release_date: String,
    val release_date_precision: String? = null,
    val total_tracks: Int? = null,
    val type: String,
    val external_urls: Map<String, String> = emptyMap()
)

// ---------------- USER PROFILE ----------------

@Serializable
data class SpotifyUserProfile(
    val display_name: String? = null,
    val email: String? = null,
    val country: String? = null,
    val product: String? = null,
    val images: List<SpotifyImage> = emptyList(),
    val followers: SpotifyFollowers? = null
)

// ---------------- LYRICS ----------------

@Serializable
data class LyricsResponse(
    val lyrics: String
)

// ---------------- DEEPL ----------------

@Serializable
data class DeepLResponse(
    val translations: List<DeepLTranslation>
)

@Serializable
data class DeepLTranslation(
    val text: String
)

// ---------------- SUPPORT ----------------

@Serializable
data class SpotifyFollowers(
    val total: Int
)

@Serializable
data class SpotifyImage(
    val url: String,
    val height: Int? = null,
    val width: Int? = null
)

// ---------------- GENIUS ----------------

@Serializable
data class GeniusSearchResponse(
    val response: GeniusResponse
)

@Serializable
data class GeniusResponse(
    val hits: List<GeniusHit>
)

@Serializable
data class GeniusHit(
    val result: GeniusResult
)

@Serializable
data class GeniusResult(
    val id: Int,
    val full_title: String,
    val url: String,
    val song_art_image_url: String?
)

@Serializable
data class GeniusSongApiResponse(
    val response: GeniusSongApiWrapper
)

@Serializable
data class GeniusSongApiWrapper(
    val song: GeniusSongApi
)

@Serializable
data class GeniusSongApi(
    val id: Int,
    val title: String,
    val url: String,
    val lyrics: String? = null,
    val description_plain: String? = null,
    val embed_content: String? = null,
    val lyrics_markdown: String? = null
)
