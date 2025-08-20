package com.example.carreraavatar.client

import android.util.Log
import com.example.carreraavatar.model.WebSocketMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * Ejemplo de cliente móvil para conectarse al servidor Android TV
 * Este código muestra cómo un dispositivo móvil se conectaría al servidor
 */
class MobileClientExample {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    
    /**
     * Conecta al servidor Android TV usando el código de sala
     * @param serverUrl URL del servidor (ej: "ws://192.168.1.100:8080")
     * @param roomCode Código de 4 dígitos de la sala
     * @param playerName Nombre del jugador
     * @param onMessageReceived Callback para mensajes recibidos
     * @param onConnectionStatus Callback para cambios de estado de conexión
     */
    fun connectToServer(
        serverUrl: String,
        roomCode: String,
        playerName: String,
        onMessageReceived: (String) -> Unit,
        onConnectionStatus: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url(serverUrl)
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("MobileClient", "Conectado al servidor")
                onConnectionStatus("Conectado")
                
                // Enviar mensaje de unión a la sala
                val joinMessage = WebSocketMessage.JoinRoom(
                    roomCode = roomCode,
                    playerName = playerName
                )
                val json = gson.toJson(joinMessage)
                webSocket.send(json)
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("MobileClient", "Mensaje recibido: $text")
                onMessageReceived(text)
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("MobileClient", "Conexión cerrada: $reason")
                onConnectionStatus("Desconectado")
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("MobileClient", "Error de conexión: ${t.message}")
                onConnectionStatus("Error: ${t.message}")
            }
        })
    }
    
    /**
     * Desconecta del servidor
     */
    fun disconnect() {
        webSocket?.close(1000, "Cliente desconectado")
        webSocket = null
    }
    
    /**
     * Ejemplo de uso del cliente
     */
    companion object {
        fun exampleUsage() {
            val client = MobileClientExample()
            
            client.connectToServer(
                serverUrl = "ws://192.168.1.100:8080",
                roomCode = "1234",
                playerName = "Jugador1",
                onMessageReceived = { message ->
                    Log.d("MobileClient", "Mensaje: $message")
                    // Aquí procesarías los mensajes recibidos
                },
                onConnectionStatus = { status ->
                    Log.d("MobileClient", "Estado: $status")
                    // Aquí actualizarías la UI con el estado de conexión
                }
            )
            
            // Para desconectar más tarde
            // client.disconnect()
        }
    }
}
