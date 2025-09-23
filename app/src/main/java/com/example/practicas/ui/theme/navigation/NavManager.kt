package com.example.practicas.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.practicas.Conferencia
import com.example.practicas.ui.theme.views.ConferenceView
import com.example.practicas.ui.theme.views.SplashScreen
import com.example.practicas.ui.theme.views.TeamDetailView
import com.example.practicas.ui.theme.views.TeamListView

@Composable
fun NavManager(navController: NavHostController, conferencias: List<Conferencia>) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("conference_selection") { ConferenceView(navController, conferencias) }
        composable("team_list/{conferenceName}") { backStackEntry ->
            val conferenceName = backStackEntry.arguments?.getString("conferenceName")
            val conference = conferencias.find { it.nombre == conferenceName }
            conference?.let {
                TeamListView(navController, it)
            }
        }
        composable("detalle/{teamName}") { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName")
            val team = conferencias.flatMap { it.equipos }.find { it.nombre == teamName }
            val conference = conferencias.find { conf -> conf.equipos.any { it.nombre == teamName } }
            if (team != null && conference != null) {
                TeamDetailView(navController, team, conference)
            }
        }
    }
}