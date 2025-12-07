package com.example.practicas.data

import com.example.practicas.model.SpotifyPlaylistResponse
import com.example.practicas.model.SpotifyPlaylistTracksResponse
import com.example.practicas.model.SpotifySearchResponse
import com.example.practicas.model.SpotifyTopArtistsResponse
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.model.SpotifyUserProfile
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface SpotifyApi {

    @GET("playlists/{id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("id") playlistId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): SpotifyPlaylistTracksResponse


    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20
    ): SpotifySearchResponse

    @GET("me")
    suspend fun getCurrentUser(): SpotifyUserProfile

    @GET("tracks/{id}")
    suspend fun getTrackDetail(
        @Path("id") trackId: String
    ): SpotifyTrack

    @GET
    suspend fun getNextPage(
        @Url url: String
    ): SpotifyPlaylistTracksResponse

    @GET("me/playlists")
    suspend fun getUserPlaylists(
        @Query("limit") limit: Int,
        @Header("Authorization") token: String
    ): SpotifyPlaylistResponse

    @GET("me/top/artists")
    suspend fun getTopArtists(
        @Header("Authorization") token: String
    ): SpotifyTopArtistsResponse


}

