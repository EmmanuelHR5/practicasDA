package com.example.practicas.ui.theme.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicas.Conferencia
import com.example.practicas.Equipo

@Composable
fun TeamDetailView(
    equipo: Equipo,
    conferencia: Conferencia
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(conferencia.colorFondo)
            .padding(horizontal = 16.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Tarjeta principal con logo y datos del equipo
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(equipo.logo),
                        contentDescription = equipo.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = equipo.nombre,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ciudad: ${equipo.ciudad}", fontSize = 20.sp, color = Color.DarkGray)
                    Text("Fundado en: ${equipo.fundacion}", fontSize = 20.sp, color = Color.DarkGray)
                    Text("Jugador histórico: ${equipo.jugadorMasImportante}", fontSize = 20.sp, color = Color.DarkGray)
                    Text("Títulos: ${equipo.trofeos}", fontSize = 20.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = equipo.descripcion,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Tarjeta del estadio
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estadio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = painterResource(equipo.estadio),
                        contentDescription = "Estadio de ${equipo.nombre}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Tarjeta del casco
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Casco",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = painterResource(equipo.casco),
                        contentDescription = "Casco de ${equipo.nombre}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
