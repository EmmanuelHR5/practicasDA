package com.example.practicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraApp() {
    var display by remember { mutableStateOf("") }
    var num1 by remember { mutableStateOf<Double?>(null) }
    var operador by remember { mutableStateOf<Char?>(null) }
    var operandoIngresado by remember { mutableStateOf(false) } // indica si ya se puso un operador

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp,60.dp,0.dp,0.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Pantalla
        OutlinedTextField(
            value = display,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 32.sp),
            label = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botonera
        Column {
            val botones = listOf(
                listOf("7", "8", "9", "+"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "*"),
                listOf("0", "C", "=", "/")
            )

            botones.forEach { fila ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    fila.forEach { texto ->
                        OutlinedButton(
                            onClick = {
                                when (texto) {
                                    in "0".."9" -> {
                                        display += texto
                                    }
                                    "+", "-", "*", "/" -> {
                                        if (!operandoIngresado && display.isNotEmpty()) {
                                            num1 = display.toDoubleOrNull()
                                            operador = texto[0]
                                            display += " $texto "
                                            operandoIngresado = true
                                        }
                                        // si ya hay un operador, no hace nada
                                    }
                                    "=" -> {
                                        if (num1 != null && operador != null) {
                                            val partes = display.split(" ")
                                            val num2 = partes.lastOrNull()?.toDoubleOrNull()
                                            if (num2 != null) {
                                                display = when (operador) {
                                                    '+' -> "${num1!! + num2}"
                                                    '-' -> "${num1!! - num2}"
                                                    '*' -> "${num1!! * num2}"
                                                    '/' -> if (num2 != 0.0) "${num1!! / num2}" else "Error"
                                                    else -> ""
                                                }
                                                // Reiniciamos todo para la siguiente operación
                                                num1 = null
                                                operador = null
                                                operandoIngresado = false
                                            }
                                        }
                                    }
                                    "C" -> {
                                        display = ""
                                        num1 = null
                                        operador = null
                                        operandoIngresado = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(texto, fontSize = 22.sp)
                        }
                    }
                }
            }
        }
    }
}
