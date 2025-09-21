package com.example.practicas.ui.theme.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practicas.Equipo
import com.example.practicas.ui.theme.views.HomeView
import com.example.practicas.ui.theme.views.SplashScreen
import com.example.practicas.ui.theme.views.TeamDetailView


@Composable
fun NavManager(navController: NavHostController, equipos: List<Equipo>) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeView(navController, equipos) }
        composable("detalle/{nombre}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")
            val equipo = equipos.find { it.nombre == nombre }
            equipo?.let { TeamDetailView(navController, it) }

        }
    }
}



