package com.example.firebasenotes.onBoarding

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.firebasenotes.R
import com.example.firebasenotes.dataStore.setOnboardingSeen
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val animationRes: Int
)

@Composable
fun OnboardingScreen() {

    val pages = listOf(
        OnboardingPage(
            title = "Organiza tus ideas",
            description = "Escribe y guarda tus notas en cualquier momento.",
            animationRes = R.raw.notes
        ),
        OnboardingPage(
            title = "Categorías y colores",
            description = "Clasifica tus notas para encontrarlas más rápido.",
            animationRes = R.raw.categories_blue
        ),
        OnboardingPage(
            title = "Funciona sin Internet",
            description = "Tus notas se guardan localmente y se sincronizan después.",
            animationRes = R.raw.sync
        ),
        OnboardingPage(
            title = "Guardado automático",
            description = "Nunca pierdes cambios, se guarda mientras escribes.",
            animationRes = R.raw.save
        ),
        OnboardingPage(
            title = "Modo oscuro",
            description = "Perfecto para estudiar de noche.",
            animationRes = R.raw.dark_mode
        )
    )

    var pageIndex by remember { mutableStateOf(0) }
    val current = pages[pageIndex]

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(current.animationRes)
    )
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // botón saltar
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Saltar",
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) {
                        scope.launch {
                            setOnboardingSeen(context)
                        }
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        // animación
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(260.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = current.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = current.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Botón siguiente / comenzar
        Button(
            onClick = {
                if (pageIndex == pages.lastIndex) {
                    scope.launch {
                        setOnboardingSeen(context)
                    }
                } else {
                    pageIndex++
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (pageIndex == pages.lastIndex) "Comenzar" else "Siguiente")
        }
    }
}
