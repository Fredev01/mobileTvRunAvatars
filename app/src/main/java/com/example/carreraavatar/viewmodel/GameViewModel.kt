package com.example.carreraavatar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carreraavatar.model.GameRoom
import com.example.carreraavatar.model.Player
import com.example.carreraavatar.websocket.SimpleWebSocketServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class GameViewModel : ViewModel() {
    
    private val _roomCode = MutableStateFlow("")
    val roomCode: StateFlow<String> = _roomCode.asStateFlow()
    
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()
    
    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning: StateFlow<Boolean> = _isServerRunning.asStateFlow()
    
    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()
    
    private val _serverIp = MutableStateFlow("")
    val serverIp: StateFlow<String> = _serverIp.asStateFlow()
    
    private var gameRoom: GameRoom? = null
    private var webSocketServer: SimpleWebSocketServer? = null
    
    fun initialize(context: Context) {
        generateRoomCode()
        startServer(context)
    }
    
    private fun generateRoomCode() {
        val random = Random()
        val code = String.format("%04d", random.nextInt(10000))
        _roomCode.value = code
        
        gameRoom = GameRoom(code = code)
    }
    
    private fun startServer(context: Context) {
        webSocketServer = SimpleWebSocketServer(
            context = context,
            onPlayerJoined = { player ->
                viewModelScope.launch {
                    val currentPlayers = _players.value.toMutableList()
                    currentPlayers.add(player)
                    _players.value = currentPlayers
                    
                    // Actualizar la sala
                    gameRoom?.players?.add(player)
                }
            },
            onPlayerLeft = { playerId ->
                viewModelScope.launch {
                    val currentPlayers = _players.value.toMutableList()
                    currentPlayers.removeAll { it.id == playerId }
                    _players.value = currentPlayers
                    
                    // Actualizar la sala
                    gameRoom?.players?.removeAll { it.id == playerId }
                }
            },
            onRoomInfoRequest = {
                gameRoom ?: GameRoom(code = _roomCode.value)
            }
        )
        
        webSocketServer?.start()
        _isServerRunning.value = true
        
        // Actualizar la URL y IP del servidor
        viewModelScope.launch {
            // Esperar un poco para que el servidor obtenga la IP
            kotlinx.coroutines.delay(1000)
            _serverUrl.value = webSocketServer?.getServerUrl() ?: ""
            _serverIp.value = webSocketServer?.getLocalIpAddress() ?: ""
        }
    }
    
    fun getLocalIpAddress(): String {
        return _serverIp.value
    }
    
    override fun onCleared() {
        super.onCleared()
        webSocketServer?.stop()
        _isServerRunning.value = false
    }
}
