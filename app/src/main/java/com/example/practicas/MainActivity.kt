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
                NavManager(conferencias)

            }
        }
    }
}

val conferenciaA = Conferencia(
    "Conferencia Americana",
    listOf(
        Equipo("New England Patriots", "Foxborough", "1959", "Tom Brady", "6 Super Bowls", "Franquicia dominante en los 2000s.", R.drawable.new_england_patriots,R.drawable.patriots_stadium,R.drawable.patriots_helmet),
        Equipo("Pittsburgh Steelers", "Pittsburgh", "1933", "Terry Bradshaw", "6 Super Bowls", "Defensa legendaria.", R.drawable.steelers,R.drawable.steelers_stadium,R.drawable.steelers_helmet),
        Equipo("Kansas City Chiefs", "Kansas City", "1960", "Patrick Mahomes", "3 Super Bowls", "Dominio reciente.", R.drawable.kansas_chiefs,R.drawable.chiefs_stadium,R.drawable.chiefs_helmet),
        Equipo("Denver Broncos", "Denver", "1960", "John Elway", "3 Super Bowls", "Ofensiva poderosa en 90s.", R.drawable.broncos,R.drawable.denver_stadium,R.drawable.broncos_helmet)
    ),
    colorFondo = Color(0xFFD50A0A)
)

val conferenciaB = Conferencia(
    "Conferencia Nacional",
    listOf(
        // CORRECTOS
        Equipo("Green Bay Packers", "Green Bay", "1919", "Brett Favre / Aaron Rodgers", "4 Super Bowls + NFL antiguos", "Equipo más histórico.", R.drawable.green_bay_packers,R.drawable.green_bay_stadio,R.drawable.greenbay_helmet),
        Equipo("Seattle Seahawks", "Seattle", "1974", "Russell Wilson", "1 Super Bowl", "Gran afición (12th man).", R.drawable.seahawks,R.drawable.seahawks_stadium,R.drawable.seahawks_helmet),
        Equipo("Dallas Cowboys", "Dallas", "1960", "Emmitt Smith", "5 Super Bowls", "America’s Team.", R.drawable.dallas_cowboys,R.drawable.cowboys_stadium,R.drawable.cowboys_helmet),
        Equipo("San Francisco 49ers", "San Francisco", "1946", "Joe Montana", "5 Super Bowls", "Dinastía de los 80s.", R.drawable._9ers,R.drawable.sf49_stadium,R.drawable._9_helmet)
    ),
    colorFondo = Color(0xFF013369)
)

val conferencias = listOf(conferenciaA, conferenciaB)