package com.example.practicas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.practicas.model.SpotifyTrack
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class FavoritesFile(
    val tracks: List<SpotifyTrack> = emptyList()
)

object FavoritesSerializer : Serializer<FavoritesFile> {

    override val defaultValue: FavoritesFile
        get() = FavoritesFile()

    override suspend fun readFrom(input: InputStream): FavoritesFile {
        return try {
            Json.decodeFromString(
                FavoritesFile.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            FavoritesFile()
        }
    }

    override suspend fun writeTo(t: FavoritesFile, output: OutputStream) {
        output.write(
            Json.encodeToString(FavoritesFile.serializer(), t)
                .encodeToByteArray()
        )
    }
}

val Context.favoritesDataStore: DataStore<FavoritesFile> by dataStore(
    fileName = "favorites.json",
    serializer = FavoritesSerializer
)
