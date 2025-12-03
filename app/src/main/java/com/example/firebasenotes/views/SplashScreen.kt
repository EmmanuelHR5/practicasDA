package com.example.firebasenotes.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.firebasenotes.R

@Composable
fun SplashScreen(
    navController: NavController,
    onboardingSeen: Boolean
){

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.document)
    )


    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1
    )

    // 👇 Esto evita doble navegación
    var hasNavigated by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(300.dp)
        )

        Text(
            "FirebaseNotes",
            color = Color.White,
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        )
    }

    // Navegación segura al finalizar la animación
    LaunchedEffect(progress) {
        if (progress == 1f && !hasNavigated) {
            hasNavigated = true
            navController.navigate("Login") {
                popUpTo("Splash") { inclusive = true }
            }
        }
    }
}
