import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.practicas.Equipo
import kotlinx.coroutines.delay

@Composable
fun TeamSplashScreen(team: Equipo, onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500) // 1.5 segundos
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = team.casco),
            contentDescription = team.nombre,
            modifier = Modifier.size(150.dp)
        )
    }
}
