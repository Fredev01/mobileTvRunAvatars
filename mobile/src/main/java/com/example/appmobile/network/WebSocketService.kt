package com.example.appmobile.network

import android.util.Log
import com.example.appmobile.data.*
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

    private val json = Json { ignoreUnknownKeys = true }

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
            _connectionState.value = ConnectionState.CONNECTING
            
            val request = Request.Builder()
                .url(serverUrl)
                .build()

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
        val message = JoinRoomMessage(data = JoinRoomData(roomCode, playerName))
        sendMessage(message)
    }

    fun selectAvatar(avatarId: String) {
        val message = SelectAvatarMessage(data = SelectAvatarData(avatarId))
        sendMessage(message)
    }

    fun sendTap() {
        val message = TapMessage(data = TapData(System.currentTimeMillis()))
        sendMessage(message)
    }

    private fun sendMessage(message: WebSocketMessage) {
        try {
            val jsonMessage = json.encodeToString(WebSocketMessage.serializer(), message)
            webSocket?.send(jsonMessage)
            Log.d("WebSocket", "Mensaje enviado: $jsonMessage")
        } catch (e: Exception) {
            Log.e("WebSocket", "Error al enviar mensaje", e)
        }
    }

    private fun handleMessage(message: String) {
        try {
            val baseMessage = json.decodeFromString<WebSocketMessage>(message)
            
            when (baseMessage.type) {
                "ROOM_JOINED" -> {
                    val roomJoined = json.decodeFromString<RoomJoinedMessage>(message)
                    _playerData.value = PlayerData(
                        playerId = roomJoined.data.playerId,
                        roomCode = roomJoined.data.roomCode
                    )
                }
                "AVATAR_SELECTED" -> {
                    val avatarSelected = json.decodeFromString<AvatarSelectedMessage>(message)
                    _playerData.value = _playerData.value?.copy(
                        selectedAvatar = avatarSelected.data.avatarId
                    )
                }
                "GAME_START" -> {
                    val gameStart = json.decodeFromString<GameStartMessage>(message)
                    _gameState.value = GameState(
                        players = gameStart.data.players,
                        isGameActive = true
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error al procesar mensaje", e)
        }
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
    val players: List<PlayerInfo>,
    val isGameActive: Boolean
) 