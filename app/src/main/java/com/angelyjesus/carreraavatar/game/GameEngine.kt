package com.angelyjesus.carreraavatar.game

import android.util.Log
import com.angelyjesus.carreraavatar.model.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

class GameEngine {
    
    private val gameScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Estados del juego
    private val _gameState = MutableStateFlow("WAITING")
    val gameState: StateFlow<String> = _gameState.asStateFlow()
    
    private val _winner = MutableStateFlow<Player?>(null)
    val winner: StateFlow<Player?> = _winner.asStateFlow()
    
    private val _countdown = MutableStateFlow(3)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()
    
    // Configuración del juego
    private val raceDuration = 90000L // 60 segundos (1 minuto)
    private val tapMultiplier = 0.02f // Cada tap avanza 2% de la pista (50 taps para ganar)
    private val maxProgress = 1.0f // 100% de la pista
    
    private var gameJob: Job? = null
    private var tapCounter = AtomicInteger(0)
    
    // Callbacks
    var onGameStateChanged: ((String) -> Unit)? = null
    var onPlayerProgressUpdated: ((String, Float) -> Unit)? = null
    var onGameFinished: ((Player) -> Unit)? = null
    var onCountdownChanged: ((Int) -> Unit)? = null
    var onGameTimeout: (() -> Unit)? = null
    
    /**
     * Inicia el juego con la cuenta regresiva
     */
    fun startGame(players: List<Player>) {
        if (players.isEmpty()) {
            Log.w("GameEngine", "No hay jugadores para iniciar el juego")
            return
        }
        
        Log.d("GameEngine", "Iniciando juego con ${players.size} jugadores")
        
        // Reiniciar estado del juego
        _winner.value = null
        tapCounter.set(0)
        
        // Iniciar cuenta regresiva
        startCountdown(players)
    }
    
    /**
     * Inicia la cuenta regresiva antes de la carrera
     */
    private fun startCountdown(players: List<Player>) {
        _gameState.value = "COUNTDOWN"
        onGameStateChanged?.invoke("COUNTDOWN")
        
        gameJob = gameScope.launch {
            // Cuenta regresiva: 3, 2, 1, ¡GO!
            for (i in 3 downTo 1) {
                _countdown.value = i
                onCountdownChanged?.invoke(i)
                delay(1000)
            }
            
            // ¡GO!
            _countdown.value = 0
            onCountdownChanged?.invoke(0)
            delay(500)
            
            // Iniciar carrera
            startRace(players)
        }
    }
    
    /**
     * Inicia la carrera principal
     */
    private suspend fun startRace(players: List<Player>) {
        _gameState.value = "RACING"
        onGameStateChanged?.invoke("RACING")
        
        Log.d("GameEngine", "¡Carrera iniciada!")
        
        // Bucle principal del juego - REMOVIDO para evitar race conditions
        // La verificación de ganador ahora solo ocurre en processPlayerTap()
        val startTime = System.currentTimeMillis()
        
        while (System.currentTimeMillis() - startTime < raceDuration && _gameState.value == "RACING") {
            // Solo mantener el juego activo, sin verificar ganadores aquí
            // La verificación se hace en processPlayerTap() con datos actualizados
            delay(100) // Reducir frecuencia para evitar overhead
        }
        
        // Si llegamos aquí y el juego sigue activo, terminó por tiempo
        if (_gameState.value == "RACING") {
            Log.d("GameEngine", "Juego terminado por tiempo")
            onGameTimeout?.invoke()
        }
    }
    
    /**
     * Finaliza el juego y declara al ganador
     */
    fun finishGame(winner: Player) {
        _gameState.value = "FINISHED"
        _winner.value = winner
        
        onGameStateChanged?.invoke("FINISHED")
        onGameFinished?.invoke(winner)
        
        Log.d("GameEngine", "¡Juego terminado! Ganador: ${winner.name}")
        
        // Cancelar el trabajo del juego
        gameJob?.cancel()
        gameJob = null
    }
    
    /**
     * Procesa un tap de un jugador
     */
    fun processPlayerTap(playerId: String, players: List<Player>) {
        val player = players.find { it.id == playerId }
        if (player == null || _gameState.value != "RACING") {
            return
        }
        
        // Incrementar contador de taps
        val currentTaps = tapCounter.incrementAndGet()
        
        // Calcular progreso basado en taps
        val currentProgress = player.progress ?: 0f
        val newProgress = (currentProgress + tapMultiplier).coerceAtMost(maxProgress)
        
        // Notificar actualización del progreso (el ViewModel se encargará de actualizar la lista)
        onPlayerProgressUpdated?.invoke(playerId, newProgress)
        
        Log.d("GameEngine", "Tap de ${player.name}: ${(newProgress * 100).toInt()}%")
        
        // Verificar si este jugador ganó
        if (newProgress >= maxProgress) {
            finishGame(player)
        }
    }
    
    /**
     * Pausa el juego
     */
    fun pauseGame() {
        if (_gameState.value == "RACING") {
            gameJob?.cancel()
            _gameState.value = "PAUSED"
            onGameStateChanged?.invoke("PAUSED")
        }
    }
    
    /**
     * Reanuda el juego
     */
    fun resumeGame(players: List<Player>) {
        if (_gameState.value == "PAUSED") {
            // Lanzar una nueva coroutine para reanudar la carrera
            gameJob = gameScope.launch {
                startRace(players)
            }
        }
    }
    
    /**
     * Detiene el juego y vuelve al lobby
     */
    fun stopGame() {
        gameJob?.cancel()
        gameJob = null
        
        _gameState.value = "WAITING"
        _winner.value = null
        _countdown.value = 3
        
        onGameStateChanged?.invoke("WAITING")
        
        Log.d("GameEngine", "Juego detenido, volviendo al lobby")
    }
    
    /**
     * Obtiene estadísticas del juego
     */
    fun getGameStats(): GameStats {
        return GameStats(
            totalTaps = tapCounter.get(),
            gameState = _gameState.value,
            winner = _winner.value,
            countdown = _countdown.value
        )
    }
    
    /**
     * Limpia recursos del motor de juego
     */
    fun cleanup() {
        gameJob?.cancel()
        gameScope.cancel()
    }
}

/**
 * Estadísticas del juego
 */
data class GameStats(
    val totalTaps: Int,
    val gameState: String,
    val winner: Player?,
    val countdown: Int
)
