package com.example.practicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun CalculadoraApp() {
    var display by remember { mutableStateOf("0") }
    var num1 by remember { mutableStateOf<Double?>(null) }
    var operador by remember { mutableStateOf<Char?>(null) }
    var operandoIngresado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Pantalla
        Text(
            text = display,
            color = Color.White,
            fontSize = 48.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.End,
            maxLines = 1
        )

        // Definición de los botones de la calculadora
        val botones = listOf(
            listOf("AC", "÷", "×"), // Fila de funciones simplificada
            listOf("7", "8", "9", "-"),
            listOf("4", "5", "6", "+"),
            listOf("1", "2", "3", "="),
            listOf("0", ".")
        )

        botones.forEach { fila ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fila.forEach { texto ->
                    // Determina el peso del botón para el layout
                    val weight = when (texto) {
                        "0" -> 2f // El botón '0' ocupa el doble de espacio
                        "1","2","3","4","5","6","7","8","9","." -> 1f
                        "=" -> 2f
                        else -> 1.33f // Ajuste para que 3 botones llenen el espacio de 4
                    }

                    // Asigna colores según la función del botón
                    val colorFondo = when (texto) {
                        "÷", "×", "-", "+", "=" -> Color(0xFFFF9800) // Naranja para operadores
                        "AC" -> Color(0xFFBDBDBD) // Gris claro para AC
                        else -> Color(0xFF333333) // Gris oscuro para números
                    }
                    val colorTexto = if (texto == "AC") Color.Black else Color.White

                    BotonCalc(
                        texto = texto,
                        colorFondo = colorFondo,
                        colorTexto = colorTexto,
                        modifier = Modifier.weight(weight),
                        onClick = {
                            when (texto) {
                                in "0".."9" -> {
                                    if (operandoIngresado) {
                                        display = texto
                                        operandoIngresado = false
                                    } else {
                                        if (display == "0") display = texto else display += texto
                                    }
                                }
                                "." -> {
                                    if (!display.contains(".")) {
                                        display += "."
                                    }
                                }
                                "+", "-", "×", "÷" -> {
                                    num1 = display.toDoubleOrNull()
                                    operador = when (texto) {
                                        "+" -> '+'
                                        "-" -> '-'
                                        "×" -> '*'
                                        "÷" -> '/'
                                        else -> null
                                    }
                                    operandoIngresado = true
                                }
                                "=" -> {
                                    if (num1 != null && operador != null && !operandoIngresado) {
                                        val num2 = display.toDoubleOrNull()
                                        if (num2 != null) {
                                            val resultado = when (operador) {
                                                '+' -> num1!! + num2
                                                '-' -> num1!! - num2
                                                '*' -> num1!! * num2
                                                '/' -> if (num2 != 0.0) num1!! / num2 else Double.NaN
                                                else -> 0.0
                                            }
                                            display = if (resultado.isNaN()) "Error" else if (resultado % 1 == 0.0) resultado.toInt().toString() else resultado.toString()
                                            num1 = null
                                            operador = null
                                        }
                                    }
                                }
                                "AC" -> {
                                    display = "0"
                                    num1 = null
                                    operador = null
                                    operandoIngresado = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun BotonCalc(
    texto: String,
    colorFondo: Color,
    colorTexto: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
        shape = CircleShape,
        modifier = modifier
            .height(80.dp)
    ) {
        Text(
            texto,
            fontSize = 32.sp,
            color = colorTexto
        )
    }
}