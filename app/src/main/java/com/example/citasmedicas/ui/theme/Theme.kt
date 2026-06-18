package com.example.citasmedicas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AzulOscuro    = Color(0xFF6F9ADC)
val AzulMedio     = Color(0xFF1A3A5C)
val AzulPrimario  = Color(0xFF1565C0)
val Celeste       = Color(0xFF2196F3)
val CelesteClaro  = Color(0xFF64B5F6)
val CelesteSuave  = Color(0xFFE3F2FD)
val Blanco        = Color(0xFFFFFFFF)
val GrisClaro     = Color(0xFFF5F7FA)
val GrisMedio     = Color(0xFF78909C)
val RojoError     = Color(0xFFE53935)
val VerdeExito    = Color(0xFF43A047)

private val HospitalColorScheme = lightColorScheme(
    primary        = Celeste,
    onPrimary      = Blanco,
    secondary      = AzulMedio,
    onSecondary    = Blanco,
    background     = GrisClaro,
    onBackground   = AzulOscuro,
    surface        = Blanco,
    onSurface      = AzulOscuro,
    error          = RojoError,
    onError        = Blanco
)

@Composable
fun CitasmedicasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HospitalColorScheme,
        typography  = Typography,
        content     = content
    )
}