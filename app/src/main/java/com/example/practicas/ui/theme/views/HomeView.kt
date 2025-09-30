package com.example.practicas.ui.theme.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practicas.Conferencia
import com.example.practicas.Equipo

@Composable
fun TeamListView(
    conference: Conferencia,
    onTeamSelected: (Equipo) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(conference.colorFondo)
            .padding(top = 50.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = conference.nombre,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            items(conference.equipos) { equipo ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .clickable { onTeamSelected(equipo) },
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = equipo.logo),
                            contentDescription = equipo.nombre,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(equipo.nombre, style = MaterialTheme.typography.titleLarge, color = Color.Black)
                            Text(equipo.ciudad, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onBack() },
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            Text("Regresar a Conferencias")
        }
    }
}
