package com.example.practicas.ui.theme.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.practicas.Conferencia
import com.example.practicas.R

@Composable
fun TeamListView(navController: NavHostController, conferencia: Conferencia) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp, bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {


            item {
                Text(
                    text = conferencia.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            items(conferencia.equipos) { equipo ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("detalle/${equipo.nombre}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
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
                            Text(text = equipo.nombre, style = MaterialTheme.typography.titleLarge)
                            Text(text = equipo.ciudad, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Botón para navegar a la selección de conferencias
        Button(
            onClick = {
                navController.navigate("conference_selection") {
                    // Evita que se apilen múltiples pantallas de conferencias
                    launchSingleTop = true
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Regresar a Conferencias")
        }
    }
}