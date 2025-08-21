package com.angelyjesus.carreraavatar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelyjesus.carreraavatar.firebase.FirebaseGameService
import com.angelyjesus.carreraavatar.model.GameRoom
import com.angelyjesus.carreraavatar.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import android.util.Log

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
    private val firebaseService = FirebaseGameService()
    
    fun initialize(context: Context) {
        generateRoomCode()
        startFirebaseServer(context)
    }
    
    private fun generateRoomCode() {
        val random = Random()
        val code = String.format("%04d", random.nextInt(10000))
        _roomCode.value = code
        
        gameRoom = GameRoom(code = code)
    }
    
    private fun startFirebaseServer(context: Context) {
        _isServerRunning.value = true
        _serverUrl.value = "Firebase Realtime Database"
        _serverIp.value = "Firebase Cloud"
        
        // Crear la sala en Firebase
        firebaseService.createGameRoom(
            roomCode = _roomCode.value,
            onSuccess = {
                Log.d("GameViewModel", "Sala creada en Firebase: ${_roomCode.value}")
                // Crear jugador host
                val hostPlayer = Player(
                    id = "host_player",
                    name = "Host",
                    avatarId = "car_blue"
                )
                addHostPlayer(hostPlayer)
                
                // Escuchar cambios en la sala
                listenToRoomChanges()
            },
            onError = { error ->
                Log.e("GameViewModel", "Error creando sala: $error")
                _isServerRunning.value = false
            }
        )
    }
    
    private fun addHostPlayer(hostPlayer: Player) {
        firebaseService.addPlayerToRoom(
            roomCode = _roomCode.value,
            player = hostPlayer,
            onSuccess = {
                _players.value = listOf(hostPlayer)
                // No necesitamos modificar gameRoom.players ya que Firebase lo maneja
            },
            onError = { error ->
                Log.e("GameViewModel", "Error agregando jugador host: $error")
            }
        )
    }
    
    private fun listenToRoomChanges() {
        viewModelScope.launch {
            firebaseService.listenToRoom(_roomCode.value).collect { room ->
                room?.let { updatedRoom ->
                    // Convertir Map a List para la UI
                    _players.value = updatedRoom.players.values.toList()
                    gameRoom = updatedRoom
                    Log.d("GameViewModel", "Sala actualizada: ${updatedRoom.players.size} jugadores")
                }
            }
        }
    }
    
    fun getLocalIpAddress(): String {
        return _serverIp.value
    }
    
    override fun onCleared() {
        super.onCleared()
        // Eliminar la sala cuando se cierre el ViewModel
        firebaseService.deleteRoom(
            roomCode = _roomCode.value,
            onSuccess = {
                Log.d("GameViewModel", "Sala eliminada al cerrar ViewModel")
            },
            onError = { error ->
                Log.e("GameViewModel", "Error eliminando sala: $error")
            }
        )
        _isServerRunning.value = false
    }
}
