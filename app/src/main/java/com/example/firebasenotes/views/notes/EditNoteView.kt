package com.example.firebasenotes.views.notes

import android.content.Intent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun EditNoteView(
    navController: NavController,
    notesVM: NotesViewModel,
    idDoc: String
) {
    LaunchedEffect(idDoc) {
        notesVM.getNoteById(idDoc)
        notesVM.startAutoSave(idDoc)
    }

    DisposableEffect(idDoc) {
        onDispose {
            notesVM.stopAutoSave()
        }
    }

    val context = LocalContext.current
    val state = notesVM.state
    val isSaving = notesVM.isSaving

    val wordCount = remember(state.note) {
        state.note.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .size
    }

    val colorOptions = listOf(
        "#FFFFFF",
        "#FFF9C4",
        "#BBDEFB",
        "#C8E6C9",
        "#FFCDD2"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Editar Nota")
                        if (isSaving) {
                            Spacer(modifier = Modifier.width(10.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Favorito
                    IconButton(onClick = { notesVM.toggleFavorite() }) {
                        if (state.favorite) {
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

                    // Compartir
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_SUBJECT, state.title)
                            putExtra(Intent.EXTRA_TEXT, state.note)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Compartir nota")
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir"
                        )
                    }

                    // Eliminar
                    IconButton(onClick = {
                        notesVM.deleteNote(idDoc) {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                    }

                    // Guardar y salir
                    IconButton(onClick = {
                        notesVM.updateNote(idDoc) {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Guardar cambios"
                        )
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = parseHexColor(state.colorHex),
                    contentColor = Color.Black    // 👈 TEXTO NEGRO
                )
            ) {
            Column(modifier = Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = state.title,
                    onValueChange = { notesVM.onValue(it, "title") },
                    label = { Text("Título", color = Color.Black) },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
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


                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.note,
                    onValueChange = { notesVM.onValue(it, "note") },
                    label = { Text("Contenido de la nota", color = Color.Black) },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
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
                            val isSelected = colorHex == state.colorHex

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(color, CircleShape)
                                    .clickable(
                                        indication = LocalIndication.current,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        notesVM.setColor(colorHex)
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

            Text(
                text = "Palabras: $wordCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
