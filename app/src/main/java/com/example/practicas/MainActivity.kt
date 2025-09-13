package com.example.practicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicas.ui.theme.PracticasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculadoraApp()
                }
            }
        }
    }
}

//Emmanuel Hernandez Rivas 22130804

@Composable
fun CalculadoraApp() {
    var display by remember { mutableStateOf("0") }
    var num1 by remember { mutableStateOf<Double?>(null) }
    var operador by remember { mutableStateOf<Char?>(null) }
    var operandoIngresado by remember { mutableStateOf(false) }
    var contadorOperadores by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Pantalla
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.End,
            maxLines = 1,
            style = TextStyle(
                color = Color.White,
                fontSize = 48.sp
            )
        )

        // Definición de los botones
        val botones = listOf(
            listOf("AC", "÷", "×"), // fila 1
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
                    // Peso visual del botón
                    val weight = when (texto) {
                        "0" -> 2.66f
                        "=","." -> 1.33f
                        "1","2","3","4","5","6","7","8","9" -> 1f
                        else -> 1.33f
                    }

                    // Colores
                    val colorFondo = when (texto) {
                        "÷", "×", "-", "+", "=" -> Color(0xFFFF9800)//naranja
                        "AC" -> Color(0xFFBDBDBD) //gris claro
                        else -> Color(0xFF333333) //Gris
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
                                        display += texto
                                        operandoIngresado = false
                                    } else {
                                        if (display == "0") display = texto else display += texto
                                    }
                                }
                                "." -> {
                                    val partes = display.split(" ")
                                    val ultimo = partes.lastOrNull() ?: ""
                                    if (!ultimo.contains(".")) {
                                        display += "."
                                    }
                                }
                                "+", "-", "×", "÷" -> {
                                    val nuevoOperador = when (texto) {
                                        "+" -> '+'
                                        "-" -> '-'
                                        "×" -> '*'
                                        "÷" -> '/'
                                        else -> null
                                    }

                                    if (operador == null) {
                                        // Primer operador
                                        num1 = display.toDoubleOrNull()
                                        operador = nuevoOperador
                                        display += " $texto "
                                        operandoIngresado = true
                                        contadorOperadores = 1
                                    } else {
                                        // Segundo operador → calcular primero
                                        val partes = display.split(" ")
                                        val num2 = partes.lastOrNull()?.toDoubleOrNull()
                                        if (num1 != null && num2 != null && operador != null) {
                                            val resultado = when (operador) {
                                                '+' -> num1!! + num2
                                                '-' -> num1!! - num2
                                                '*' -> num1!! * num2
                                                '/' -> if (num2 != 0.0) num1!! / num2 else Double.NaN
                                                else -> 0.0
                                            }

                                            // Mostrar resultado parcial
                                            display = if (resultado.isNaN()) {
                                                "Error"
                                            } else if (resultado % 1 == 0.0) {
                                                resultado.toInt().toString()
                                            } else {
                                                resultado.toString()
                                            }

                                            // Guardar estado
                                            num1 = if (resultado.isNaN()) null else resultado
                                            operador = nuevoOperador
                                            display += " $texto "
                                            operandoIngresado = true
                                            contadorOperadores = 1
                                        }
                                    }
                                }
                                "=" -> {
                                    if (num1 != null && operador != null && !operandoIngresado) {
                                        val partes = display.split(" ")
                                        val num2 = partes.lastOrNull()?.toDoubleOrNull()
                                        if (num2 != null) {
                                            val resultado = when (operador) {
                                                '+' -> num1!! + num2
                                                '-' -> num1!! - num2
                                                '*' -> num1!! * num2
                                                '/' -> if (num2 != 0.0) num1!! / num2 else Double.NaN
                                                else -> 0.0
                                            }
                                            display = if (resultado.isNaN()) {
                                                "Error"
                                            } else if (resultado % 1 == 0.0) {
                                                resultado.toInt().toString()
                                            } else {
                                                resultado.toString()
                                            }
                                            num1 = null
                                            operador = null
                                            contadorOperadores = 0
                                        }
                                    }
                                }
                                "AC" -> {
                                    display = "0"
                                    num1 = null
                                    operador = null
                                    operandoIngresado = false
                                    contadorOperadores = 0
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
