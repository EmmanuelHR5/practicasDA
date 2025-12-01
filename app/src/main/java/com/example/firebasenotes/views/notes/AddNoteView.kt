package com.example.firebasenotes.views.notes

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteView(navController: NavController, notesVM: NotesViewModel) {

    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FFFFFF") }
    var isFavorite by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val wordCount = remember(note) {
        note.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .size
    }

    val colorOptions = listOf(
        "#FFFFFF",  // blanco
        "#FFF9C4",  // amarillo suave
        "#BBDEFB",  // azul claro
        "#C8E6C9",  // verde claro
        "#FFCDD2"   // rojo suave
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Nota", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        if (isFavorite) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Quitar de favoritos",
                                tint = Color(0xFFFFC107)
                            )
                        } else {
                            Icon(
                                Icons.Outlined.StarBorder,
                                contentDescription = "Marcar como favorito"
                            )
                        }
                    }
                }
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = parseHexColor(selectedColor)
                )
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Contenido de la nota") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Paleta de colores
                    Text(
                        text = "Color de la nota",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        colorOptions.forEach { colorHex ->
                            val color = parseHexColor(colorHex)
                            val isSelected = colorHex == selectedColor

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(color, CircleShape)
                                    .clickable(
                                        indication = LocalIndication.current,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        selectedColor = colorHex
                                    }
                                    .then(
                                        if (isSelected) Modifier
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = CircleShape
                                            )
                                        else Modifier
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contador de palabras
            Text(
                text = "Palabras: $wordCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    Log.d("ADD_BUTTON", "Botón presionado")

                    if (title.isBlank() || note.isBlank()) {
                        Log.e("ADD_BUTTON", "Campos vacíos")
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    Log.d("ADD_CALL", "Llamando a saveNewNote...")

                    notesVM.saveNewNote(
                        title = title,
                        note = note,
                        colorHex = selectedColor,
                        isFavorite = isFavorite,
                        onSuccess = {
                            Log.d("ADD_SUCCESS", "onSuccess ejecutado correctamente")

                            Toast.makeText(context, "Nota añadida", Toast.LENGTH_SHORT).show()

                            Log.d("NAVIGATION", "Intentando navegar a Home...")
                            navController.navigate("Home") {
                                popUpTo("Home") { inclusive = true }
                            }
                        },
                        onFailure = { errorMsg ->
                            Log.e("ADD_FAILURE", "Error al guardar: $errorMsg")
                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Añadir Nota")
            }
        }
    }
}
