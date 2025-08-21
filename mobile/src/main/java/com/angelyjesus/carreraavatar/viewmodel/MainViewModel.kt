package com.angelyjesus.carreraavatar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelyjesus.carreraavatar.firebase.FirebaseGameClient
import com.angelyjesus.carreraavatar.model.GameRoom
import com.angelyjesus.carreraavatar.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class ConnectionState {
    object DISCONNECTED : ConnectionState()
    object CONNECTING : ConnectionState()
    object CONNECTED : ConnectionState()
    data class ERROR(val message: String) : ConnectionState()
}

data class PlayerData(
    val playerId: String,
    val roomCode: String,
    val playerName: String,
    val selectedAvatar: String? = null
)

data class GameState(
    val players: List<Player>,
    val isGameActive: Boolean,
    val tapCount: Int = 0
)

class MainViewModel : ViewModel() {
    
    private val firebaseClient = FirebaseGameClient()
    
    // Estados de la aplicación
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _playerData = MutableStateFlow<PlayerData?>(null)
    val playerData: StateFlow<PlayerData?> = _playerData.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    private val _currentRoom = MutableStateFlow<GameRoom?>(null)
    val currentRoom: StateFlow<GameRoom?> = _currentRoom.asStateFlow()
    
    // Pantalla actual
    private val _currentScreen = MutableStateFlow<Screen>(Screen.CONNECTION)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()
    
    sealed class Screen {
        object CONNECTION : Screen()
        object AVATAR_SELECTION : Screen()
        object GAME : Screen()
    }
    
    /**
     * Conectar a una sala
     */
    fun connectToRoom(roomCode: String, playerName: String) {
        _connectionState.value = ConnectionState.CONNECTING
        
        firebaseClient.joinRoom(
            roomCode = roomCode,
            playerName = playerName,
            onSuccess = { playerId ->
                Log.d("MainViewModel", "Conectado exitosamente a sala $roomCode")
                _connectionState.value = ConnectionState.CONNECTED
                _playerData.value = PlayerData(
                    playerId = playerId,
                    roomCode = roomCode,
                    playerName = playerName
                )
                
                // Escuchar cambios en la sala
                listenToRoomChanges(roomCode)
                
                // Ir a selección de avatar
                _currentScreen.value = Screen.AVATAR_SELECTION
            },
            onError = { error ->
                Log.e("MainViewModel", "Error conectando: $error")
                _connectionState.value = ConnectionState.ERROR(error)
            }
        )
    }
    
    /**
     * Escuchar cambios en una sala específica
     */
    private fun listenToRoomChanges(roomCode: String) {
        viewModelScope.launch {
            firebaseClient.listenToRoom(roomCode).collect { room ->
                room?.let { updatedRoom ->
                    _currentRoom.value = updatedRoom
                    
                    // Actualizar estado del juego
                    _gameState.value = GameState(
                        players = updatedRoom.players.values.toList(),
                        isGameActive = updatedRoom.gameState.isActive,
                        tapCount = updatedRoom.gameState.taps[_playerData.value?.playerId] ?: 0
                    )
                    
                    Log.d("MainViewModel", "Sala actualizada: ${updatedRoom.players.size} jugadores")
                }
            }
        }
    }
    
    /**
     * Seleccionar avatar
     */
    fun selectAvatar(avatarId: String) {
        val playerId = _playerData.value?.playerId ?: return
        val roomCode = _playerData.value?.roomCode ?: return
        
        firebaseClient.selectAvatar(
            roomCode = roomCode,
            playerId = playerId,
            avatarId = avatarId,
            onSuccess = {
                Log.d("MainViewModel", "Avatar seleccionado: $avatarId")
                _playerData.value = _playerData.value?.copy(selectedAvatar = avatarId)
                _currentScreen.value = Screen.GAME
            },
            onError = { error ->
                Log.e("MainViewModel", "Error seleccionando avatar: $error")
                _connectionState.value = ConnectionState.ERROR(error)
            }
        )
    }
    
    /**
     * Enviar tap input
     */
    fun sendTap() {
        val playerId = _playerData.value?.playerId ?: return
        val roomCode = _playerData.value?.roomCode ?: return
        
        firebaseClient.sendTapInput(
            roomCode = roomCode,
            playerId = playerId,
            timestamp = System.currentTimeMillis(),
            onSuccess = {
                Log.d("MainViewModel", "Tap enviado exitosamente")
            },
            onError = { error ->
                Log.e("MainViewModel", "Error enviando tap: $error")
            }
        )
    }
    
    /**
     * Desconectar de la sala
     */
    fun disconnect() {
        val playerId = _playerData.value?.playerId
        val roomCode = _playerData.value?.roomCode
        
        if (playerId != null && roomCode != null) {
            firebaseClient.leaveRoom(
                roomCode = roomCode,
                playerId = playerId,
                onSuccess = {
                    Log.d("MainViewModel", "Desconectado exitosamente")
                },
                onError = { error ->
                    Log.e("MainViewModel", "Error desconectando: $error")
                }
            )
        }
        
        _connectionState.value = ConnectionState.DISCONNECTED
        _playerData.value = null
        _gameState.value = null
        _currentRoom.value = null
        _currentScreen.value = Screen.CONNECTION
    }
    
    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
