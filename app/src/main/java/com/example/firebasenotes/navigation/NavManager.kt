package com.example.firebasenotes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebasenotes.viewModels.LoginViewModel
import com.example.firebasenotes.viewModels.NotesViewModel
import com.example.firebasenotes.views.RecuperarContrasenaView
import com.example.firebasenotes.views.login.BlankView
import com.example.firebasenotes.views.login.ForgotPasswordScreen
import com.example.firebasenotes.views.notes.HomeView
import com.example.firebasenotes.views.login.TabsView
import com.example.firebasenotes.views.notes.AddNoteView
import com.example.firebasenotes.views.notes.EditNoteView

@Composable
fun NavManager(loginVM: LoginViewModel, notesVM: NotesViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Blank" ){
        composable("Blank"){
            BlankView(navController)
        }
        composable("Login"){
            TabsView(navController, loginVM)
        }
        composable("Home"){
            HomeView(navController, notesVM)
        }
        composable("AddNoteView"){
            AddNoteView(navController, notesVM)
        }
        composable("EditNoteView/{idDoc}", arguments = listOf(
            navArgument("idDoc") { type = NavType.StringType }
        )){
            val idDoc = it.arguments?.getString("idDoc") ?: ""
            EditNoteView(navController, notesVM, idDoc)
        }
        composable("forgotPassword"){
            ForgotPasswordScreen(navController)
        }
        composable("recuperar") {
            RecuperarContrasenaView(navController)
        }


    }
}