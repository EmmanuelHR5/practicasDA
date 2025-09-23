package com.example.practicas.ui.theme.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ConferenceView(navController: NavHostController, conferencias: List<Conferencia>) {
    LazyColumn(
        modifier = Modifier.padding(top=50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    )  {
        item {
            Image(
                painter = painterResource(id = R.drawable.conferencias),
                contentDescription = "Conferencias",
                modifier = Modifier.size(400.dp)
            )
        }
        items(conferencias) { conferencia ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("team_list/${conferencia.nombre}")
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column {
                        Text(text = conferencia.nombre, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}