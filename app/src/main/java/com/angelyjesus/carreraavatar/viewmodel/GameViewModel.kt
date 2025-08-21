package com.angelyjesus.carreraavatar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelyjesus.carreraavatar.firebase.FirebaseGameService
import com.angelyjesus.carreraavatar.game.GameEngine
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
    
    // Estados del juego
    private val _gameState = MutableStateFlow("WAITING")
    val gameState: StateFlow<String> = _gameState.asStateFlow()
    
    private val _winner = MutableStateFlow<Player?>(null)
    val winner: StateFlow<Player?> = _winner.asStateFlow()
    
    // Callback para cambios de estado del juego
    var onGameStateChanged: ((String) -> Unit)? = null
    
    private var gameRoom: GameRoom? = null
    private val firebaseService = FirebaseGameService()
    private val gameEngine = GameEngine()
    
    init {
        // Configurar callbacks del motor de juego
        gameEngine.onGameStateChanged = { state ->
            _gameState.value = state
            // Notificar también al callback de navegación
            onGameStateChanged?.invoke(state)
        }
        
        gameEngine.onGameFinished = { winnerPlayer ->
            _winner.value = winnerPlayer
        }
        
        gameEngine.onPlayerProgressUpdated = { playerId, newProgress ->
            // Actualizar el progreso del jugador en la lista local
            val currentPlayers = _players.value.toMutableList()
            val index = currentPlayers.indexOfFirst { it.id == playerId }
            if (index != -1) {
                val player = currentPlayers[index]
                val updatedPlayer = player.copy(progress = newProgress)
                currentPlayers[index] = updatedPlayer
                _players.value = currentPlayers
            }
        }
    }
    
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
    
    /**
     * Inicia el juego
     */
    fun startGame() {
        if (_players.value.isNotEmpty()) {
            Log.d("GameViewModel", "Iniciando juego...")
            gameEngine.startGame(_players.value)
        }
    }
    
    /**
     * Procesa un tap de un jugador
     */
    fun processPlayerTap(playerId: String) {
        gameEngine.processPlayerTap(playerId, _players.value)
    }
    
    /**
     * Vuelve al lobby después de terminar el juego
     */
    fun returnToLobby() {
        gameEngine.stopGame()
        _winner.value = null
        Log.d("GameViewModel", "Volviendo al lobby")
    }
    
    /**
     * Pausa el juego
     */
    fun pauseGame() {
        gameEngine.pauseGame()
    }
    
    /**
     * Reanuda el juego
     */
    fun resumeGame() {
        gameEngine.resumeGame(_players.value)
    }
    
    fun getLocalIpAddress(): String {
        return _serverIp.value
    }
    
    override fun onCleared() {
        super.onCleared()
        // Limpiar el motor de juego
        gameEngine.cleanup()
        
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
