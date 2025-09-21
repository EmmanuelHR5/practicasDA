package com.example.practicas.ui.theme.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practicas.Equipo


@Composable
fun TeamDetailView(navController: NavHostController, equipo: Equipo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = equipo.logo),
            contentDescription = equipo.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium, fontSize = 20.sp)
        Text(text = "Ciudad: ${equipo.ciudad}",fontSize = 20.sp)
        Text(text = "Fundación: ${equipo.fundacion}",fontSize = 20.sp)
        Text(text = "Jugador destacado: ${equipo.jugadorMasImportante}",fontSize = 20.sp)
        Text(text = "Trofeos: ${equipo.trofeos}",fontSize = 20.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = equipo.descripcion,fontSize = 20.sp)

        Spacer(modifier = Modifier.height(30.dp))

        // Botón para regresar
        Button(onClick = { navController.navigate("home") }) {
            Text("Regresar a Home")
        }
    }
}
