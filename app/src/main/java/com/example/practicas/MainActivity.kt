package com.example.practicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.practicas.ui.theme.PracticasTheme
import com.example.practicas.ui.theme.navigation.NavManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticasTheme {
                val navController = rememberNavController()
                NavManager(navController, conferencias)
            }
        }
    }
}

val conferenciaA = Conferencia(
    "Conferencia Americana",
    listOf(
        Equipo("New England Patriots", "Foxborough", "1959", "Tom Brady", "6 Super Bowls", "Franquicia dominante en los 2000s.", R.drawable.patriots_png),
        Equipo("Pittsburgh Steelers", "Pittsburgh", "1933", "Terry Bradshaw", "6 Super Bowls", "Defensa legendaria.", R.drawable.steelers_png),
        Equipo("Kansas City Chiefs", "Kansas City", "1960", "Patrick Mahomes", "3 Super Bowls", "Dominio reciente.", R.drawable.kansas_png),
        Equipo("Denver Broncos", "Denver", "1960", "John Elway", "3 Super Bowls", "Ofensiva poderosa en 90s.", R.drawable.broncos_png)
    ),
    colorFondo = Color(0xFFD50A0A)
)

val conferenciaB = Conferencia(
    "Conferencia Nacional",
    listOf(
        // CORRECTOS
        Equipo("Green Bay Packers", "Green Bay", "1919", "Brett Favre / Aaron Rodgers", "4 Super Bowls + NFL antiguos", "Equipo más histórico.", R.drawable.packers_png),
        Equipo("Seattle Seahawks", "Seattle", "1974", "Russell Wilson", "1 Super Bowl", "Gran afición (12th man).", R.drawable.seahawks_png),
        Equipo("Dallas Cowboys", "Dallas", "1960", "Emmitt Smith", "5 Super Bowls", "America’s Team.", R.drawable.cowboys_png),
        Equipo("San Francisco 49ers", "San Francisco", "1946", "Joe Montana", "5 Super Bowls", "Dinastía de los 80s.", R.drawable._49ers_png)
    ),
    colorFondo = Color(0xFF013369)
)

val conferencias = listOf(conferenciaA, conferenciaB)