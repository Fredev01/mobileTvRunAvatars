package com.angelyjesus.carreraavatar.model

data class GameRoom @JvmOverloads constructor(
    val code: String = "",
    val players: Map<String, Player> = emptyMap(),
    val gameState: GameState = GameState(),
    val createdAt: Long = System.currentTimeMillis()
)

data class GameState @JvmOverloads constructor(
    val isActive: Boolean = false,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val taps: Map<String, Any> = emptyMap(), // Cambiar a Any para manejar tanto Int como Map
    val currentState: String = "WAITING", // Estados: WAITING, COUNTDOWN, RACING, FINISHED
    val winner: Player? = null,
    val countdown: Int = 3
)
