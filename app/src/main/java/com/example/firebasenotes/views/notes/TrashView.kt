package com.example.firebasenotes.views.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashView(
    navController: NavController,
    notesVM: NotesViewModel
) {

    val notes by notesVM.notesData.collectAsState()

    // Solo notas en papelera
    val trashNotes = notes.filter { it.isDeleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Papelera") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {

            if (trashNotes.isEmpty()) {
                Text(
                    "No hay notas en la papelera",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(trashNotes) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = parseHexColor(note.colorHex)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(note.category, style = MaterialTheme.typography.bodySmall)

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        notesVM.restoreNote(note.idDoc)
                                    }) {
                                        Icon(
                                            Icons.Default.Restore,
                                            contentDescription = "Restaurar"
                                        )
                                    }
                                    IconButton(onClick = {
                                        notesVM.deleteNoteForever(note.idDoc)
                                    }) {
                                        Icon(
                                            Icons.Default.DeleteForever,
                                            contentDescription = "Eliminar definitivamente"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
