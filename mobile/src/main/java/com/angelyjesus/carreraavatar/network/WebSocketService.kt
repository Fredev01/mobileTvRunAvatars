package com.angelyjesus.carreraavatar.network

import android.util.Log
import com.angelyjesus.carreraavatar.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketService {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    // Estados de conexión
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Datos del jugador
    private val _playerData = MutableStateFlow<PlayerData?>(null)
    val playerData: StateFlow<PlayerData?> = _playerData.asStateFlow()

    // Estado del juego
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    fun connectToServer(serverUrl: String) {
        try {
            Log.d("WebSocket", "Intentando conectar a: $serverUrl")
            _connectionState.value = ConnectionState.CONNECTING
            
            val request = Request.Builder()
                .url(serverUrl)
                .build()

            Log.d("WebSocket", "Creando WebSocket...")
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocket", "Conexión establecida")
                    _connectionState.value = ConnectionState.CONNECTED
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WebSocket", "Mensaje recibido: $text")
                    handleMessage(text)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocket", "Conexión cerrada: $code - $reason")
                    _connectionState.value = ConnectionState.DISCONNECTED
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocket", "Error de conexión", t)
                    _connectionState.value = ConnectionState.ERROR(t.message ?: "Error desconocido")
                }
            })
        } catch (e: Exception) {
            Log.e("WebSocket", "Error al conectar", e)
            _connectionState.value = ConnectionState.ERROR(e.message ?: "Error al conectar")
        }
    }

    fun joinRoom(roomCode: String, playerName: String) {
        val message = JoinRoom(roomCode = roomCode, playerName = playerName)
        sendMessage(message)
    }

    fun selectAvatar(avatarId: String) {
        val playerId = _playerData.value?.playerId ?: return
        val message = SelectAvatar(playerId = playerId, avatarId = avatarId)
        sendMessage(message)
    }

    fun sendTap() {
        val playerId = _playerData.value?.playerId ?: return
        val message = TapInput(playerId = playerId, timestamp = System.currentTimeMillis())
        sendMessage(message)
    }

    private fun sendMessage(message: WebSocketMessage) {
        try {
            Log.d("WebSocket", "Intentando serializar mensaje: ${message::class.simpleName}")
            
            // Serializar directamente la clase específica en lugar de usar el serializer de la clase sellada
            val jsonMessage = when (message) {
                is JoinRoom -> json.encodeToString(JoinRoom.serializer(), message)
                is SelectAvatar -> json.encodeToString(SelectAvatar.serializer(), message)
                is TapInput -> json.encodeToString(TapInput.serializer(), message)
                else -> throw IllegalArgumentException("Tipo de mensaje no soportado: ${message::class.simpleName}")
            }
            
            Log.d("WebSocket", "Mensaje serializado: $jsonMessage")
            
            webSocket?.let { ws ->
                val sent = ws.send(jsonMessage)
                if (sent) {
                    Log.d("WebSocket", "Mensaje enviado exitosamente")
                } else {
                    Log.e("WebSocket", "Error: No se pudo enviar el mensaje")
                }
            } ?: run {
                Log.e("WebSocket", "Error: WebSocket no está conectado")
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error al enviar mensaje: ${e.message}", e)
        }
    }

    private fun handleMessage(message: String) {
        try {
            Log.d("WebSocket", "Procesando mensaje recibido: $message")
            
            // Intentar parsear diferentes tipos de mensajes del servidor
            when {
                message.contains("\"roomCode\"") -> {
                    val roomInfo = json.decodeFromString<RoomInfo>(message)
                    handleRoomInfo(roomInfo)
                }
                message.contains("\"player\"") -> {
                    val playerJoined = json.decodeFromString<PlayerJoined>(message)
                    handlePlayerJoined(playerJoined)
                }
                message.contains("\"message\"") -> {
                    if (message.contains("\"Success\"") || message.contains("unido")) {
                        val success = json.decodeFromString<Success>(message)
                        handleSuccess(success)
                    } else {
                        val error = json.decodeFromString<Error>(message)
                        handleError(error)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error al procesar mensaje: ${e.message}", e)
        }
    }
    
    private fun handleRoomInfo(roomInfo: RoomInfo) {
        Log.d("WebSocket", "Información de sala recibida: ${roomInfo.roomCode}")
        _playerData.value = PlayerData(
            playerId = "temp_id", // Se actualizará cuando el servidor envíe el ID real
            roomCode = roomInfo.roomCode
        )
    }
    
    private fun handlePlayerJoined(playerJoined: PlayerJoined) {
        Log.d("WebSocket", "Jugador unido: ${playerJoined.player.name}")
        // Actualizar con el ID real del jugador
        _playerData.value = _playerData.value?.copy(
            playerId = playerJoined.player.id
        )
    }
    
    private fun handleSuccess(success: Success) {
        Log.d("WebSocket", "Mensaje de éxito: ${success.message}")
    }
    
    private fun handleError(error: Error) {
        Log.e("WebSocket", "Error del servidor: ${error.message}")
        _connectionState.value = ConnectionState.ERROR(error.message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Desconexión normal")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
        _playerData.value = null
        _gameState.value = null
    }
}

sealed class ConnectionState {
    object DISCONNECTED : ConnectionState()
    object CONNECTING : ConnectionState()
    object CONNECTED : ConnectionState()
    data class ERROR(val message: String) : ConnectionState()
}

data class PlayerData(
    val playerId: String,
    val roomCode: String,
    val selectedAvatar: String? = null
)

data class GameState(
    val players: List<Player>,
    val isGameActive: Boolean
) 