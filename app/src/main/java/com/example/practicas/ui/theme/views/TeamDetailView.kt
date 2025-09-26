package com.example.practicas.ui.theme.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practicas.Conferencia
import com.example.practicas.Equipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailView(navController: NavHostController, equipo: Equipo, conferencia: Conferencia) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(equipo.nombre) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("team_list/${conferencia.nombre}") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(conferencia.colorFondo.copy(alpha = 0.8f))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = equipo.logo),
                        contentDescription = equipo.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ciudad: ${equipo.ciudad}", fontSize = 18.sp)
                    Text(text = "Fundación: ${equipo.fundacion}", fontSize = 18.sp)
                    Text(text = "Jugador destacado: ${equipo.jugadorMasImportante}", fontSize = 18.sp)
                    Text(text = "Trofeos: ${equipo.trofeos}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = equipo.descripcion, fontSize = 18.sp, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}