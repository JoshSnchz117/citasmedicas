package com.example.citasmedicas
import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

// Función que solicita el JSON y maneja la respuesta mediante un callback
fun obtenerMensajeDelDia(context: Context, alObtenerMensaje: (String) -> Unit) {
    // 1. Inicializar la cola de peticiones de Volley
    val queue = Volley.newRequestQueue(context)

    // URL asignada para las pruebas del parcial
    val urlReal = "https://dummyjson.com/quotes/random"

    // 2. Crear la petición del objeto JSON (Método GET)
    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, urlReal, null,
        { response ->
            try {
                // Extraer el valor de la clave "quote" del JSON recibido
                val mensaje = response.getString("quote")
                alObtenerMensaje(mensaje)
            } catch (e: Exception) {
                alObtenerMensaje("Error leyendo el mensaje del servidor.")
            }
        },
        { error ->
            // Control de errores: Mensaje por defecto en caso de fallo o falta de internet
            alObtenerMensaje("Recuerda cuidar tu salud todos los días.")
        }
    )

    // 3. Agregar la petición a la cola para su ejecución inmediata
    queue.add(jsonObjectRequest)
}

