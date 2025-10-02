import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay
import com.example.practicas.R

@Composable
fun MainNFLSplash(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // duración del splash
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo de la NFL
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.nfl_logo),
                contentDescription = "NFL Logo",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animación Lottie debajo del logo
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.greenball)
            )
            val progress by animateLottieCompositionAsState(composition)

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(400.dp) //
            )
        }
    }
}
