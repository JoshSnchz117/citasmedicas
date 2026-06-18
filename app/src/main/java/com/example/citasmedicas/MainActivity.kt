package com.example.citasmedicas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.citasmedicas.ui.theme.*   // ← Este import conecta con Theme.kt
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle

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
                        navController = navController,
                        nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                        telefono = backStackEntry.arguments?.getString("telefono") ?: "",
                        fecha = backStackEntry.arguments?.getString("fecha") ?: "",
                        hora = backStackEntry.arguments?.getString("hora") ?: ""
                    )
                }


                composable("listaCitas") { ListaCitasScreen(navController) }


                composable("editarCita/{id}/{nombre}/{telefono}/{fecha}/{hora}") { backStackEntry ->
                    EditarCitaScreen(
                        navController = navController,
                        id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0,
                        nombreActual = backStackEntry.arguments?.getString("nombre") ?: "",
                        telefonoActual = backStackEntry.arguments?.getString("telefono") ?: "",
                        fechaActual = backStackEntry.arguments?.getString("fecha") ?: "",
                        horaActual = backStackEntry.arguments?.getString("hora") ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioScreen(navController: NavController) {

    //  LÓGICA DE VOLLEY
    val context = LocalContext.current
    var mensajeDelDia by remember { mutableStateOf("Cargando mensaje del día...") }

// Ejecuta la petición HTTP una sola vez al abrir esta pantalla
    LaunchedEffect(Unit) {
        obtenerMensajeDelDia(context) { resultado ->
            mensajeDelDia = resultado
        }
    }
// ---------------------------------

    var nombre   by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var error    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisClaro)
    ) {
        // Header azul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AzulOscuro, AzulMedio)))
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Celeste.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = CelesteClaro,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("MediCitas", color = Blanco, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Sistema de Agendamiento Médico", color = CelesteClaro, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"$mensajeDelDia\"",
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = AzulOscuro,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nueva Cita", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AzulOscuro)
                    Text("Complete sus datos para continuar", fontSize = 13.sp, color = GrisMedio,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Celeste) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Celeste,
                            unfocusedBorderColor = GrisMedio.copy(alpha = 0.5f),
                            focusedLabelColor = Celeste
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono (10 dígitos)") },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = Celeste) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Celeste,
                            unfocusedBorderColor = GrisMedio.copy(alpha = 0.5f),
                            focusedLabelColor = Celeste
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(RojoError.copy(alpha = 0.08f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = RojoError, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(error, color = RojoError, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(AzulPrimario, Celeste)))
                            .clickable {
                                when {
                                    nombre.isBlank() -> error = "El nombre no puede estar vacío"
                                    !telefono.matches(Regex("\\d{10}")) -> error = "El teléfono debe tener exactamente 10 dígitos"
                                    else -> {
                                        error = ""
                                        navController.navigate("fechaHora/$nombre/$telefono")
                                    }
                                }
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowForward, null, tint = Blanco, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Continuar", color = Blanco, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("listaCitas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, Celeste),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Celeste)
            ) {
                Text("Ver Citas Agendadas", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun FechaHoraScreen(navController: NavController, nombre: String, telefono: String) {
    var fecha by remember { mutableStateOf("") }
    var hora  by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val context  = navController.context
    val dbHelper = remember { CitasDatabaseHelper(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisClaro)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AzulOscuro, AzulMedio)))
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Celeste.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, null, tint = CelesteClaro, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text("Seleccionar Horario", color = Blanco, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Paciente: $nombre", color = CelesteClaro, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Fecha
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CelesteSuave),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Celeste, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fecha de la cita", fontWeight = FontWeight.SemiBold, color = AzulOscuro, fontSize = 14.sp)
                        Text(
                            text = if (fecha.isNotEmpty()) fecha else "No seleccionada",
                            color = if (fecha.isNotEmpty()) Celeste else GrisMedio,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    fecha = "$day-${month + 1}-$year"
                                    Toast.makeText(context, "Fecha guardada", Toast.LENGTH_SHORT).show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Elegir", fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Card Hora
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CelesteSuave),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccessTime, null, tint = Celeste, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora de la cita", fontWeight = FontWeight.SemiBold, color = AzulOscuro, fontSize = 14.sp)
                        Text(
                            text = if (hora.isNotEmpty()) hora else "No seleccionada",
                            color = if (hora.isNotEmpty()) Celeste else GrisMedio,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    hora = String.format("%02d:%02d", h, m)
                                    Toast.makeText(context, "Hora guardada", Toast.LENGTH_SHORT).show()
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Elegir", fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            if (fecha.isNotEmpty() && hora.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(AzulPrimario, Celeste)))
                        .clickable {
                            dbHelper.insertarCita(
                                Cita(nombre = nombre, telefono = telefono, fecha = fecha, hora = hora)
                            )
                            navController.navigate("confirmacion/$nombre/$telefono/$fecha/$hora")
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Blanco, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Confirmar Cita", color = Blanco, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmacionScreen(
    navController: NavController,
    nombre: String,
    telefono: String,
    fecha: String,
    hora: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisClaro)
    ) {
        // Header verde éxito
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), VerdeExito)))
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Blanco.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Blanco, modifier = Modifier.size(44.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text("¡Cita Confirmada!", color = Blanco, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Su cita ha sido registrada con éxito", color = Blanco.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(4.dp),
                border = BorderStroke(1.dp, CelesteSuave)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Resumen de la Cita",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AzulOscuro,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Fila Paciente
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, null, tint = Celeste, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Paciente", fontSize = 11.sp, color = GrisMedio, fontWeight = FontWeight.Medium)
                            Text(nombre, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Divider(color = GrisClaro, thickness = 1.dp)

                    // Fila Teléfono
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, null, tint = Celeste, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Teléfono", fontSize = 11.sp, color = GrisMedio, fontWeight = FontWeight.Medium)
                            Text(telefono, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Divider(color = GrisClaro, thickness = 1.dp)

                    // Fila Fecha
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Celeste, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Fecha", fontSize = 11.sp, color = GrisMedio, fontWeight = FontWeight.Medium)
                            Text(fecha, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Divider(color = GrisClaro, thickness = 1.dp)

                    // Fila Hora
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, null, tint = Celeste, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Hora", fontSize = 11.sp, color = GrisMedio, fontWeight = FontWeight.Medium)
                            Text(hora, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón Volver al Inicio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(AzulPrimario, Celeste)))
                    .clickable { navController.navigate("formulario") { popUpTo(0) } }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, null, tint = Blanco, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Volver al Inicio", color = Blanco, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón Ver Citas
            OutlinedButton(
                onClick = { navController.navigate("listaCitas") { popUpTo(0) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, Celeste),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Celeste)
            ) {
                Text("Ver Todas las Citas", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ListaCitasScreen(navController: NavController) {
    val context    = navController.context
    val dbHelper   = remember { CitasDatabaseHelper(context) }
    var listaCitas by remember { mutableStateOf(dbHelper.obtenerCitas()) }

    LaunchedEffect(Unit) { listaCitas = dbHelper.obtenerCitas() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisClaro)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AzulOscuro, AzulMedio)))
                .padding(top = 48.dp, bottom = 24.dp, start = 8.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("formulario") { popUpTo(0) } }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Blanco)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Citas Agendadas", color = Blanco, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${listaCitas.size} cita(s) registrada(s)", color = CelesteClaro, fontSize = 13.sp)
                }
            }
        }

        // Estado vacío
        if (listaCitas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EventBusy, null, tint = GrisMedio, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No hay citas registradas", color = GrisMedio, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaCitas) { cita ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Blanco),
                        elevation = CardDefaults.cardElevation(3.dp),
                        border = BorderStroke(1.dp, CelesteSuave)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar con inicial
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Brush.horizontalGradient(listOf(AzulPrimario, Celeste))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cita.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                        color = Blanco,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cita.nombre, fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 15.sp)
                                    Text(cita.telefono, color = GrisMedio, fontSize = 13.sp)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            Divider(color = GrisClaro)
                            Spacer(Modifier.height(12.dp))

                            Row {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.CalendarMonth, null, tint = Celeste, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(cita.fecha, fontSize = 13.sp, color = AzulMedio, fontWeight = FontWeight.Medium)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, null, tint = Celeste, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(cita.hora, fontSize = 13.sp, color = AzulMedio, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Row {
                                OutlinedButton(
                                    onClick = {
                                        navController.navigate(
                                            "editarCita/${cita.id}/${cita.nombre}/${cita.telefono}/${cita.fecha}/${cita.hora}"
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Celeste),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Celeste)
                                ) {
                                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Editar", fontSize = 13.sp)
                                }
                                Spacer(Modifier.width(10.dp))
                                Button(
                                    onClick = {
                                        dbHelper.eliminarCita(cita.id)
                                        listaCitas = dbHelper.obtenerCitas()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RojoError)
                                ) {
                                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Eliminar", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditarCitaScreen(
    navController: NavController,
    id: Int,
    nombreActual: String,
    telefonoActual: String,
    fechaActual: String,
    horaActual: String
) {
    val context  = navController.context
    val dbHelper = remember { CitasDatabaseHelper(context) }

    var nombre   by remember { mutableStateOf(nombreActual) }
    var telefono by remember { mutableStateOf(telefonoActual) }
    var fecha    by remember { mutableStateOf(fechaActual) }
    var hora     by remember { mutableStateOf(horaActual) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisClaro)
    ) {
        // Header con botón atrás
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AzulOscuro, AzulMedio)))
                .padding(top = 48.dp, bottom = 24.dp, start = 8.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Blanco)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Editar Cita", color = Blanco, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Modifique los datos necesarios", color = CelesteClaro, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Celeste) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Celeste,
                            unfocusedBorderColor = GrisMedio.copy(alpha = 0.5f),
                            focusedLabelColor = Celeste
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = Celeste) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Celeste,
                            unfocusedBorderColor = GrisMedio.copy(alpha = 0.5f),
                            focusedLabelColor = Celeste
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Fecha (dd-mm-aaaa)") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Celeste) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Celeste,
                            unfocusedBorderColor = GrisMedio.copy(alpha = 0.5f),
                            focusedLabelColor = Celeste
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = hora,
                        onValueChange = { hora = it },
                        label = { Text("Hora (hh:mm)") },
                        leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = Celeste) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Celeste,
                            unfocusedBorderColor = GrisMedio.copy(alpha = 0.5f),
                            focusedLabelColor = Celeste
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Celeste),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Celeste)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(AzulPrimario, Celeste)))
                        .clickable {
                            if (nombre.isNotBlank() && telefono.length == 10) {
                                dbHelper.actualizarCita(Cita(id, nombre, telefono, fecha, hora))
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, null, tint = Blanco, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar", color = Blanco, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

data class Cita(val id: Int = 0, val nombre: String, val telefono: String, val fecha: String, val hora: String)


class CitasDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "CitasMedicas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE citas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                telefono TEXT,
                fecha TEXT,
                hora TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS citas")
        onCreate(db)
    }


    fun insertarCita(cita: Cita) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", cita.nombre)
            put("telefono", cita.telefono)
            put("fecha", cita.fecha)
            put("hora", cita.hora)
        }
        db.insert("citas", null, values)
        db.close()
    }

    fun obtenerCitas(): List<Cita> {
        val lista = mutableListOf<Cita>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM citas", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Cita(
                        id = cursor.getInt(0),
                        nombre = cursor.getString(1),
                        telefono = cursor.getString(2),
                        fecha = cursor.getString(3),
                        hora = cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun actualizarCita(cita: Cita) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", cita.nombre)
            put("telefono", cita.telefono)
            put("fecha", cita.fecha)
            put("hora", cita.hora)
        }
        db.update("citas", values, "id = ?", arrayOf(cita.id.toString()))
        db.close()
    }

    fun eliminarCita(id: Int) {
        val db = this.writableDatabase
        db.delete("citas", "id = ?", arrayOf(id.toString()))
        db.close()
    }
}
