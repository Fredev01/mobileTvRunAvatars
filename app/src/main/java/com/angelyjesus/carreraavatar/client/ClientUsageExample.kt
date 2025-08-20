package com.angelyjesus.carreraavatar.client

import android.util.Log
import com.angelyjesus.carreraavatar.model.WebSocketMessage
import com.google.gson.Gson

/**
 * Ejemplo completo de cómo usar el cliente móvil para conectarse al servidor Android TV
 * 
 * Este archivo muestra:
 * 1. Cómo conectarse al servidor usando la IP real
 * 2. Cómo manejar los mensajes recibidos
 * 3. Cómo procesar diferentes tipos de respuestas
 * 4. Cómo manejar errores y desconexiones
 */
class ClientUsageExample {
    
    private val client = MobileClientExample()
    private val gson = Gson()
    
    /**
     * Ejemplo completo de conexión y manejo de mensajes
     * @param tvIpAddress IP real de la Android TV (mostrada en pantalla)
     * @param roomCode Código de 4 dígitos mostrado en la TV
     * @param playerName Nombre del jugador
     */
    fun connectToGameServer(
        tvIpAddress: String,
        roomCode: String,
        playerName: String
    ) {
        val serverUrl = "ws://$tvIpAddress:8080"
        
        Log.d("ClientExample", "Conectando a: $serverUrl")
        Log.d("ClientExample", "Código de sala: $roomCode")
        Log.d("ClientExample", "Nombre del jugador: $playerName")
        
        client.connectToServer(
            serverUrl = serverUrl,
            roomCode = roomCode,
            playerName = playerName,
            onMessageReceived = { message ->
                handleServerMessage(message)
            },
            onConnectionStatus = { status ->
                handleConnectionStatus(status)
            }
        )
    }
    
    /**
     * Maneja los mensajes recibidos del servidor
     */
    private fun handleServerMessage(message: String) {
        try {
            // Intentar parsear como diferentes tipos de mensajes
            when {
                message.contains("\"Success\"") -> {
                    val successMessage = gson.fromJson(message, WebSocketMessage.Success::class.java)
                    Log.d("ClientExample", "✅ Éxito: ${successMessage.message}")
                    // Aquí actualizarías la UI para mostrar el éxito
                }
                
                message.contains("\"Error\"") -> {
                    val errorMessage = gson.fromJson(message, WebSocketMessage.Error::class.java)
                    Log.e("ClientExample", "❌ Error: ${errorMessage.message}")
                    // Aquí mostrarías el error en la UI
                }
                
                message.contains("\"RoomInfo\"") -> {
                    val roomInfo = gson.fromJson(message, WebSocketMessage.RoomInfo::class.java)
                    Log.d("ClientExample", "📋 Información de sala:")
                    Log.d("ClientExample", "   Código: ${roomInfo.roomCode}")
                    Log.d("ClientExample", "   Jugadores: ${roomInfo.players.size}")
                    roomInfo.players.forEach { player ->
                        Log.d("ClientExample", "   - ${player.name} (${player.id})")
                    }
                    // Aquí actualizarías la lista de jugadores en la UI
                }
                
                message.contains("\"PlayerJoined\"") -> {
                    val playerJoined = gson.fromJson(message, WebSocketMessage.PlayerJoined::class.java)
                    Log.d("ClientExample", "👋 Nuevo jugador: ${playerJoined.player.name}")
                    // Aquí actualizarías la UI para mostrar el nuevo jugador
                }
                
                message.contains("\"PlayerLeft\"") -> {
                    val playerLeft = gson.fromJson(message, WebSocketMessage.PlayerLeft::class.java)
                    Log.d("ClientExample", "👋 Jugador desconectado: ${playerLeft.playerId}")
                    // Aquí actualizarías la UI para remover el jugador
                }
                
                else -> {
                    Log.d("ClientExample", "📨 Mensaje no reconocido: $message")
                }
            }
        } catch (e: Exception) {
            Log.e("ClientExample", "Error procesando mensaje: ${e.message}")
        }
    }
    
    /**
     * Maneja los cambios de estado de conexión
     */
    private fun handleConnectionStatus(status: String) {
        when {
            status.startsWith("Conectado") -> {
                Log.d("ClientExample", "🟢 $status")
                // Aquí actualizarías la UI para mostrar que estás conectado
            }
            
            status.startsWith("Desconectado") -> {
                Log.d("ClientExample", "🔴 $status")
                // Aquí actualizarías la UI para mostrar que estás desconectado
            }
            
            status.startsWith("Error") -> {
                Log.e("ClientExample", "❌ $status")
                // Aquí mostrarías el error en la UI
            }
        }
    }
    
    /**
     * Desconecta del servidor
     */
    fun disconnect() {
        Log.d("ClientExample", "Desconectando del servidor...")
        client.disconnect()
    }
    
    /**
     * Ejemplo de uso completo
     */
    companion object {
        fun runExample() {
            val example = ClientUsageExample()
            
            // IMPORTANTE: Usar la IP real mostrada en la Android TV
            // La IP se muestra en la pantalla principal de la aplicación
            example.connectToGameServer(
                tvIpAddress = "192.168.1.100", // ⚠️ CAMBIAR por la IP real mostrada en la TV
                roomCode = "1234", // ⚠️ CAMBIAR por el código mostrado en la TV
                playerName = "Jugador1"
            )
            
            // Desconectar después de un tiempo (ejemplo)
            // Thread.sleep(30000) // Esperar 30 segundos
            // angelyjesus.disconnect()
        }
        
        /**
         * Ejemplo de cómo obtener la IP de la Android TV
         * NOTA: La IP ya se muestra en la pantalla de la aplicación
         */
        fun getTvIpAddress(): String {
            // La IP se muestra automáticamente en la pantalla de la Android TV
            // No necesitas obtenerla manualmente
            return "192.168.1.100" // Placeholder - usar la IP real mostrada
        }
        
        /**
         * Ejemplo de validación de código de sala
         */
        fun isValidRoomCode(code: String): Boolean {
            return code.length == 4 && code.all { it.isDigit() }
        }
        
        /**
         * Ejemplo de validación de nombre de jugador
         */
        fun isValidPlayerName(name: String): Boolean {
            return name.isNotBlank() && name.length <= 20
        }
        
        /**
         * Ejemplo de validación de IP
         */
        fun isValidIpAddress(ip: String): Boolean {
            val parts = ip.split(".")
            if (parts.size != 4) return false
            
            return parts.all { part ->
                try {
                    val num = part.toInt()
                    num in 0..255
                } catch (e: NumberFormatException) {
                    false
                }
            }
        }
    }
}
