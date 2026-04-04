package com.example.citasmedicas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "formulario") {
                composable("formulario") { FormularioScreen(navController) }
                composable("fechaHora/{nombre}/{telefono}") { backStackEntry ->
                    val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
                    val telefono = backStackEntry.arguments?.getString("telefono") ?: ""
                    FechaHoraScreen(navController, nombre, telefono)
                }
                composable("confirmacion/{nombre}/{telefono}/{fecha}/{hora}") { backStackEntry ->
                    ConfirmacionScreen(
                        nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                        telefono = backStackEntry.arguments?.getString("telefono") ?: "",
                        fecha = backStackEntry.arguments?.getString("fecha") ?: "",
                        hora = backStackEntry.arguments?.getString("hora") ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono (10 dígitos)") })
            Spacer(modifier = Modifier.height(16.dp))
            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(onClick = {
                if (nombre.isBlank()) {
                    error = "El nombre no puede estar vacío"
                } else if (!telefono.matches(Regex("\\d{10}"))) {
                    error = "El teléfono debe tener 10 dígitos"
                } else {
                    error = ""
                    navController.navigate("fechaHora/$nombre/$telefono")
                }
            }) {
                Text("Continuar")
            }
        }
    }
}

@Composable
fun FechaHoraScreen(navController: NavController, nombre: String, telefono: String) {
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val context = navController.context

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Hola, $nombre", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        fecha = "$dayOfMonth-${month + 1}-$year"
                        Toast.makeText(context, "Fecha guardada", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text("Seleccionar Fecha")
            }
            if (fecha.isNotEmpty()) {
                Text("Fecha: $fecha", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        hora = String.format("%02d:%02d", hourOfDay, minute)
                        Toast.makeText(context, "Hora guardada", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }) {
                Text("Seleccionar Hora")
            }
            if (hora.isNotEmpty()) {
                Text("Hora: $hora", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (fecha.isNotEmpty() && hora.isNotEmpty()) {
                Button(onClick = {
                    navController.navigate("confirmacion/$nombre/$telefono/$fecha/$hora")
                }) {
                    Text("Confirmar Cita")
                }
            }
        }
    }
}

@Composable
fun ConfirmacionScreen(nombre: String, telefono: String, fecha: String, hora: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Confirmación de Cita", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
            Text("Teléfono: $telefono", style = MaterialTheme.typography.bodyLarge)
            Text("Fecha: $fecha", style = MaterialTheme.typography.bodyLarge)
            Text("Hora: $hora", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
