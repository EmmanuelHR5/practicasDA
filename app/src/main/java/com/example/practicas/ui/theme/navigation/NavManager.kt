package com.example.practicas.ui.theme.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practicas.Conferencia
import com.example.practicas.Equipo
import com.example.practicas.conferencias
import com.example.practicas.ui.theme.views.HomeView
import com.example.practicas.ui.theme.views.SplashScreen
import com.example.practicas.ui.theme.views.TeamDetailView


@Composable
fun NavManager(navController: NavHostController, conferencias: List<Conferencia>) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeView(navController, conferencias) }
        composable("detalle/{nombre}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")

            // Buscar el equipo dentro de todas las conferencias
            val equipo = conferencias.flatMap { it.equipos }.find { it.nombre == nombre }

            equipo?.let { TeamDetailView(navController, it) }
        }
    }
}

