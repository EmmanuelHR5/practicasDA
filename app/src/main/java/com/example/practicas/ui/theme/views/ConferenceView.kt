package com.example.practicas.ui.theme.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practicas.Conferencia
import com.example.practicas.R

@Composable
fun ConferenceView(navController: NavHostController, conferencias: List<Conferencia>) {
    // Buscamos cada conferencia por su nombre para asegurarnos de usar la correcta.
    val conferenciaAmericana = conferencias.find { it.nombre.contains("Americana") }
    val conferenciaNacional = conferencias.find { it.nombre.contains("Nacional") }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- MITAD SUPERIOR: CONFERENCIA AMERICANA ---
        if (conferenciaAmericana != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(conferenciaAmericana.colorFondo)
                    .clickable {
                        navController.navigate("team_list/${conferenciaAmericana.nombre}")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Botón con Imagen
                    Image(
                        painter = painterResource(id = R.drawable.conferencia_americana),
                        contentDescription = "Logo Conferencia Americana",
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Título de la Conferencia
                    Text(
                        text = conferenciaAmericana.nombre,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // --- MITAD INFERIOR: CONFERENCIA NACIONAL ---
        if (conferenciaNacional != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(conferenciaNacional.colorFondo)
                    .clickable {
                        navController.navigate("team_list/${conferenciaNacional.nombre}")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Botón con Imagen
                    Image(
                        painter = painterResource(id = R.drawable.conferencia_nacional),
                        contentDescription = "Logo Conferencia Nacional",
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Título de la Conferencia
                    Text(
                        text = conferenciaNacional.nombre,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}