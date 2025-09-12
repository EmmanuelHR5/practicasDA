package com.example.practicas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Tablas de ISR
                val ingMinimo = listOf(
                    0.01,
                    368.11,
                    3124.36,
                    5490.76,
                    6382.81,
                    7641.91,
                    15412.81,
                    24292.66,
                    46378.51,
                    61838.11,
                    185514.31
                )
                val ingMaximo = listOf(
                    368.10,
                    3124.35,
                    5490.75,
                    6382.80,
                    7641.90,
                    15412.80,
                    24292.65,
                    46378.50,
                    61838.10,
                    185514.30,
                    Double.MAX_VALUE
                )

                val cuotaFija = listOf(
                    0.00,
                    7.05,
                    183.45,
                    441.00,
                    583.65,
                    809.25,
                    2469.15,
                    4557.75,
                    11183.40,
                    16130.55,
                    58180.35
                )

                val excedente = listOf(
                    1.92,
                    6.40,
                    10.88,
                    16.00,
                    17.92,
                    21.36,
                    23.52,
                    30.00,
                    32.00,
                    34.00,
                    35.00
                )

                MainScreen(ingMinimo, ingMaximo, cuotaFija, excedente)
            }
        }
    }
}

@Composable
fun MainScreen(
    ingMinimo: List<Double>,
    ingMaximo: List<Double>,
    cuotaFija: List<Double>,
    excedente: List<Double>
) {
    var ingreso by remember { mutableStateOf("") }
    var limInf by remember { mutableStateOf("") }
    var limSup by remember { mutableStateOf("") }
    var isr by remember { mutableStateOf("") }
    var sueldoNeto by remember { mutableStateOf("") }

    // Nuevas variables para mostrar cuota fija y % excedente
    var cuotaFijaCalculada by remember { mutableStateOf("") }
    var excedenteCalculado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Image(
                painter = painterResource(R.drawable.memes),
                contentDescription = null,
                modifier = Modifier.defaultMinSize(minWidth = 400.dp, minHeight = 150.dp)
            )
        }

       Spacer(modifier = Modifier.height(30.dp))

        Row {
            Texto("Ingreso Quincenal", Color.Blue)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            TextField(
                value = ingreso,
                onValueChange = { ingreso = it },
                textStyle = TextStyle(fontSize = 24.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Button(

                onClick = {
                    val ingresoLimpio = ingreso.trim().replace(",", "")
                    val ingresoDouble = ingresoLimpio.toDoubleOrNull() ?: 0.0

                    var indice = -1
                    for (j in ingMinimo.indices) {
                        if (ingresoDouble >= ingMinimo[j] && ingresoDouble <= ingMaximo[j]) {
                            indice = j
                            break
                        }
                    }

                    if (indice != -1) {
                        val exced = ingresoDouble - ingMinimo[indice]
                        val impuesto = cuotaFija[indice] + (exced * (excedente[indice] / 100.0))

                        limInf = "%.2f".format(ingMinimo[indice])
                        limSup = if (ingMaximo[indice] == Double.MAX_VALUE) "-" else "%.2f".format(ingMaximo[indice])
                        isr = "%.2f".format(impuesto)
                        sueldoNeto = "%.2f".format(ingresoDouble - impuesto)

                        // Guardar excedente y cuota fija
                        excedenteCalculado = "%.2f".format(exced)
                        cuotaFijaCalculada = "%.2f".format(cuotaFija[indice])
                    } else {
                        limInf = "-"
                        limSup = "-"
                        isr = "0.00"
                        sueldoNeto = "%.2f".format(ingresoDouble)
                        excedenteCalculado = "0.00"
                        cuotaFijaCalculada = "0.00"
                    }
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Calcular",
                fontSize = 24.sp)

            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Resultados en TextField solo lectura
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Textfield(value = limInf, label = "Límite Inferior", color = Color.Blue, fontSize = 30)
            Spacer(modifier = Modifier.height(10.dp))
            Textfield(value = limSup, label = "Límite Superior", color = Color.Cyan, fontSize = 30)
            Spacer(modifier = Modifier.height(10.dp))

            Textfield(value = isr, label = "ISR", color = Color.Red, fontSize = 30)
            Spacer(modifier = Modifier.height(10.dp))
            Textfield(value = sueldoNeto, label = "Sueldo Neto", color = Color.Green, fontSize = 30)
        }
    }
}

@Composable
fun Texto(texto: String, colorLetra: Color) {
    Text(
        text = texto,
        color = colorLetra,
        fontSize = 30.sp,
    )
}
@Composable
fun Textfield(
    value: String,
    label: String,
    color: Color ,
    fontSize: Int
) {
    TextField(
        value = value,
        onValueChange = {},
        label = { Text(label, fontSize = fontSize.sp, color = color) },
        readOnly = true,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = fontSize.sp,
            color = color
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
