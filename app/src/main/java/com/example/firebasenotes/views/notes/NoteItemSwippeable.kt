package com.example.firebasenotes.views.notes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.model.NotesState
import com.example.firebasenotes.viewModels.NotesViewModel
import kotlin.math.abs

@Composable
fun NoteItemSwipeable(
    note: NotesState,
    notesVM: NotesViewModel,
    navController: NavController
) {
    var offsetX by remember { mutableStateOf(0f) }

    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp)

    val backgroundColor = parseHexColor(note.colorHex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 95.dp)
            .padding(vertical = 6.dp)
    ) {

        when {
            offsetX > 40 -> {
                // Fondo verde (editar)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
            }

            offsetX < -40 -> {
                // Fondo rojo (eliminar)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFFF44336), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            }
        }

        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar nota") },
                text = { Text("¿Seguro que deseas eliminar esta nota? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        notesVM.deleteNote(note.idDoc) {}
                        showDeleteDialog = false
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }


        Card(
            modifier = Modifier
                .matchParentSize()
                .offset(x = animatedOffsetX)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 120 -> navController.navigate("EditNoteView/${note.idDoc}")
                                offsetX < -120 -> notesVM.moveToTrash(note.idDoc) {}   // 👈 ANTES llamaba deleteNote
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount
                            if (abs(offsetX) > 220) offsetX = 220f * (offsetX / abs(offsetX))
                        }
                    )
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    if (note.favorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorito",
                            tint = Color(0xFFFFC107)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = note.category,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = if (note.note.length > 60) note.note.take(60) + "..." else note.note,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = note.date,
                    fontSize = 11.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
