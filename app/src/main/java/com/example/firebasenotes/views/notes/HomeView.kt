package com.example.firebasenotes.views.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasenotes.components.CardNote
import com.example.firebasenotes.viewModels.NotesViewModel

// ... imports
import androidx.compose.foundation.lazy.items // Asegúrate de importar esto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(navController: NavController, notesVM: NotesViewModel) {

    val notes by notesVM.notesData.collectAsState()

    LaunchedEffect(Unit) {
        notesVM.fetchNotes()
    }

    //ORDENAMOS POR FAVORITOS (true = primero)
    val sortedNotes = notes.sortedByDescending { it.favorite }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas") },
                actions = {
                    IconButton(
                        onClick = {
                            notesVM.signOut()
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AddNoteView") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { pad ->

        Column(modifier = Modifier.padding(pad)) {

            if (sortedNotes.isEmpty()) {
                Text("No tienes notas", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {

                    //Usamos la lista ya ordenada
                    items(sortedNotes) { note ->
                        NoteItemSwipeable(
                            note = note,
                            notesVM = notesVM,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
