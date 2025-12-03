package com.example.firebasenotes.navigation

import TabsView
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebasenotes.viewModels.LoginViewModel
import com.example.firebasenotes.viewModels.NotesViewModel
import com.example.firebasenotes.viewModels.ThemeViewModel
import com.example.firebasenotes.views.AccountView
import com.example.firebasenotes.views.RecuperarContrasenaView
import com.example.firebasenotes.views.SplashScreen
import com.example.firebasenotes.views.login.ForgotPasswordScreen
import com.example.firebasenotes.views.notes.HomeView
import com.example.firebasenotes.views.notes.AddNoteView
import com.example.firebasenotes.views.notes.EditNoteView
import com.example.firebasenotes.views.notes.TrashView

@Composable
fun NavManager(
    loginVM: LoginViewModel,
    notesVM: NotesViewModel,
    themeVM: ThemeViewModel,
    startInSplash: Boolean,
    onboardingSeen: Boolean
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (startInSplash) "Splash" else "Login"
    ) {

        // SPLASH
        composable("Splash") {
            SplashScreen(
                navController = navController,
                onboardingSeen = onboardingSeen
            )
        }

        // LOGIN / REGISTER
        composable("Login") {
            TabsView(navController, loginVM)
        }

        // HOME
        composable("Home") {
            HomeView(navController, notesVM, themeVM)
        }

        // AGREGAR NOTA
        composable("AddNoteView") {
            AddNoteView(navController, notesVM)
        }

        // EDITAR NOTA
        composable(
            "EditNoteView/{idDoc}",
            arguments = listOf(navArgument("idDoc") { type = NavType.StringType })
        ) {
            val idDoc = it.arguments?.getString("idDoc") ?: ""
            EditNoteView(navController, notesVM, idDoc)
        }

        // RECUPERAR CONTRASEÑA
        composable("forgotPassword") {
            ForgotPasswordScreen(navController)
        }

        composable("recuperar") {
            RecuperarContrasenaView(navController)
        }

        // CUENTA
        composable("AccountView") {
            AccountView(navController, notesVM)
        }

        // PAPELERA
        composable("TrashView") {
            TrashView(navController, notesVM)
        }
    }
}
