package com.example.practicas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
                    PantallaPrincipal()
                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal() {
    val context = LocalContext.current
    var valorA by remember { mutableStateOf("") }
    var valorB by remember { mutableStateOf("") }
    var Resultado by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row() {
            TextField(
                modifier = Modifier.padding(top = 16.dp),
                value = valorA,
                onValueChange = { valorA = it },
            )
        }

        Row() {
            OutlinedTextField(
                value = valorB,
                onValueChange = { valorB = it }
            )
        }

        Row() {
            OutlinedButton(onClick = {
                val a = valorA.toInt()
                val b = valorB.toInt()
                val c = a + b
                Resultado = c.toString()

            }) {
                Text(text = "Sumar")
            }

            OutlinedButton(onClick = {
                valorA = ""
                valorB = ""
                Resultado = ""
            }) {
                Text(text = "Borrar")
            }
        }

        Row() {
            OutlinedTextField(
                value = Resultado,
                label = { Text("Resultado") },
                onValueChange = { Resultado = it }

                )
            }
        }
    }





