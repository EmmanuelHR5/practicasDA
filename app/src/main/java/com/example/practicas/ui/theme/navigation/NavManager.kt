package com.example.practicas.ui.theme.navigation

import MainNFLSplash
import TeamSplashScreen
import androidx.compose.runtime.*
import com.example.practicas.Conferencia
import com.example.practicas.Equipo
import com.example.practicas.ui.theme.views.*

@Composable
fun NavManager(conferencias: List<Conferencia>) {
    var currentScreen by remember { mutableStateOf("splashMain") }
    var selectedTeam by remember { mutableStateOf<Equipo?>(null) }
    var selectedConference by remember { mutableStateOf<Conferencia?>(null) }

    when (currentScreen) {
        // ---------------- Splash inicial NFL ----------------
        "splashMain" -> MainNFLSplash {
            currentScreen = "conferenceSelection"
        }

        // ---------------- Selección de conferencias ----------------
        "conferenceSelection" -> ConferenceView(
            conferencias = conferencias,
            onConferenceSelected = { conference ->
                selectedConference = conference
                currentScreen = "teamList"
            }
        )

        // ---------------- Lista de equipos ----------------
        "teamList" -> selectedConference?.let { conference ->
            TeamListView(
                conference = conference,
                onTeamSelected = { team ->
                    selectedTeam = team
                    currentScreen = "teamSplash"
                },
                onBack = { currentScreen = "conferenceSelection" }
            )
        }

        // ---------------- Splash del equipo (casco) ----------------
        "teamSplash" -> selectedTeam?.let { team ->
            TeamSplashScreen(team) {
                currentScreen = "teamDetail"
            }
        }

        // ---------------- Detalle del equipo ----------------
        "teamDetail" -> selectedTeam?.let { team ->
            TeamDetailView(
                equipo = team,
                conferencia = selectedConference!!,
                onBack = { currentScreen = "teamList" }
            )
        }

    }
}
