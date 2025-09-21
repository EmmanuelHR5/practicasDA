package com.example.practicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                NavManager(navController, teams)
            }
        }
    }
}

val teams = listOf(
    Equipo("New England Patriots", "Foxborough", "1959", "Tom Brady", "6 Super Bowls", "Franquicia dominante en los 2000s.", R.drawable.new_england_patriots),
    Equipo("Pittsburgh Steelers", "Pittsburgh", "1933", "Terry Bradshaw", "6 Super Bowls", "Defensa legendaria.", R.drawable.steelers),
    Equipo("Dallas Cowboys", "Dallas", "1960", "Emmitt Smith", "5 Super Bowls", "America’s Team.", R.drawable.dallas_cowboys),
    Equipo("San Francisco 49ers", "San Francisco", "1946", "Joe Montana", "5 Super Bowls", "Dinastía de los 80s.", R.drawable._9ers),
    Equipo("Green Bay Packers", "Green Bay", "1919", "Brett Favre / Aaron Rodgers", "4 Super Bowls + NFL antiguos", "Equipo más histórico.", R.drawable.green_bay_packers),
    Equipo("Kansas City Chiefs", "Kansas City", "1960", "Patrick Mahomes", "3 Super Bowls", "Dominio reciente.", R.drawable.kansas_chiefs),
    Equipo("Seattle Seahawks", "Seattle", "1974", "Russell Wilson", "1 Super Bowl", "Gran afición (12th man).", R.drawable.seahawks),
    Equipo("Denver Broncos", "Denver", "1960", "John Elway", "3 Super Bowls", "Ofensiva poderosa en 90s.", R.drawable.broncos),
    Equipo("Baltimore Ravens", "Baltimore", "1996", "Ray Lewis", "2 Super Bowls", "Defensas dominantes.", R.drawable.ravens),
    Equipo("Indianapolis Colts", "Indianapolis", "1953", "Peyton Manning", "2 Super Bowls", "Famosos por quarterbacks legendarios.", R.drawable.colts),
    Equipo("Chicago Bears", "Chicago", "1920", "Walter Payton", "1 Super Bowl + NFL antiguos", "Franquicia fundadora.", R.drawable.chicago_bears ),
    Equipo("Miami Dolphins", "Miami", "1966", "Dan Marino", "2 Super Bowls", "Único equipo en lograr temporada perfecta (1972).", R.drawable.miami_dolphins)
)
