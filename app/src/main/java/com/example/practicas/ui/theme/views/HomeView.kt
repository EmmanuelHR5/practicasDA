package com.example.practicas.ui.theme.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.practicas.Equipo


@Composable
fun HomeView(navController: NavHostController, equipos: List<Equipo>) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 50.dp)
    ) {
        items(equipos) { equipo ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("detalle/${equipo.nombre}")
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = equipo.logo),
                        contentDescription = equipo.nombre,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = equipo.nombre, style = MaterialTheme.typography.titleLarge)
                        Text(text = equipo.ciudad, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

