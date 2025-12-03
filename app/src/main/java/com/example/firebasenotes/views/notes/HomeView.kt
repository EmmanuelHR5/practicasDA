package com.example.firebasenotes.views.notes

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.BiometricViewModel
import com.example.firebasenotes.viewModels.NotesViewModel
import com.example.firebasenotes.viewModels.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    notesVM: NotesViewModel,
    themeVM: ThemeViewModel
) {

    val notes by notesVM.notesData.collectAsState()
    //Solo notas que no esten borradas
    val activeNotes = notes.filter { !it.isDeleted }

    val isDark by themeVM.isDarkMode.observeAsState(initial = false)

    LaunchedEffect(Unit) {
        notesVM.fetchNotes()
    }

    // Barra de búsqueda
    var searchText by remember { mutableStateOf("") }

    // Categorías
    val categories = listOf("Todas", "General", "Personal", "Trabajo", "Escuela", "Ideas")
    var selectedCategory by remember { mutableStateOf("Todas") }

    // Solo favoritos
    var onlyFavorites by remember { mutableStateOf(false) }

    // Ordenamos por favorito primero
    val sortedNotes = activeNotes.sortedByDescending { it.favorite }

    // Filtro por categoría
    val categoryFiltered = sortedNotes.filter { note ->
        selectedCategory == "Todas" || note.category == selectedCategory
    }

    // Filtro por texto
    val searchFiltered = categoryFiltered.filter { note ->
        searchText.isBlank() ||
                note.title.contains(searchText, ignoreCase = true) ||
                note.note.contains(searchText, ignoreCase = true)
    }

    // Filtro solo favoritos
    val finalList =
        if (onlyFavorites) searchFiltered.filter { it.favorite } else searchFiltered

    // Colores del modo oscuro
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    //biometrico
    val biometricVM: BiometricViewModel = viewModel()
    val context = LocalContext.current
    val activity = context as? androidx.fragment.app.FragmentActivity
        ?: throw IllegalStateException("HomeView must be hosted in a FragmentActivity")


    val lockedCategories = listOf("Personal", "Privado")
    var unlockedCategory by remember { mutableStateOf("") }

    val unlockedCategories = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mis Notas", color = textColor)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                ),
                actions = {
                    // Ir a Mi Cuenta
                    IconButton(onClick = { navController.navigate("AccountView") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Cuenta",
                            tint = textColor
                        )
                    }

                    IconButton(onClick = {navController.navigate("TrashView")}){
                        Icon(imageVector = Icons.Default.RestoreFromTrash, contentDescription = "Papelera",
                            tint = textColor)
                    }

                    // Toggle tema
                    IconButton(onClick = { themeVM.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Cambiar tema",
                            tint = textColor
                        )
                    }

                    // Cerrar sesión
                    IconButton(
                        onClick = {
                            notesVM.signOut()
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = textColor
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

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar notas...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                singleLine = true
            )

            // Fila de categorías
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 8.dp
            ) {
                categories.forEach { cat ->

                    val isLocked = lockedCategories.contains(cat)
                    val isUnlocked = unlockedCategories.contains(cat)

                    Tab(
                        selected = selectedCategory == cat,
                        onClick = {
                            if (isLocked && !isUnlocked) {

                                biometricVM.authenticate(activity)

                                biometricVM.authSuccess.observe(activity) { success ->
                                    if (success == true) {
                                        unlockedCategories.add(cat)
                                        selectedCategory = cat
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Acceso denegado a $cat",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            } else {
                                selectedCategory = cat
                            }
                        },
                        text = {
                            Text(
                                text = if (isLocked && !isUnlocked) "$cat 🔒" else cat
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Solo favoritos", color = textColor)
                Switch(
                    checked = onlyFavorites,
                    onCheckedChange = { onlyFavorites = it }
                )
            }

            if (finalList.isEmpty()) {
                Text("No tienes notas", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(finalList) { note ->
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
