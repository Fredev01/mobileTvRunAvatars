package com.angelyjesus.carreraavatar.config

object GameConfig {
    
    // Configuración de la carrera
    const val RACE_DURATION_MS = 30000L // 30 segundos
    const val TAP_PROGRESS_INCREMENT = 0.01f // 1% por tap
    const val MAX_PROGRESS = 1.0f // 100% de la pista
    
    // Configuración de la cuenta regresiva
    const val COUNTDOWN_SECONDS = 3
    const val COUNTDOWN_DELAY_MS = 1000L
    const val GO_DELAY_MS = 500L
    
    // Configuración del canvas
    const val TRACK_START_OFFSET = 50f
    const val TRACK_FINISH_OFFSET = 100f
    const val LANE_DIVISIONS = 4
    const val PLAYER_SIZE_MULTIPLIER = 0.6f
    
    // Colores de la pista
    val TRACK_BACKGROUND_COLOR = androidx.compose.ui.graphics.Color(0xFF4CAF50)
    val TRACK_LINE_COLOR = androidx.compose.ui.graphics.Color.White
    val START_LINE_COLOR = androidx.compose.ui.graphics.Color.White
    val FINISH_LINE_COLOR = androidx.compose.ui.graphics.Color(0xFFF44336)
    
    // Colores de los jugadores
    val PLAYER_COLORS = mapOf(
        "red" to androidx.compose.ui.graphics.Color(0xFFF44336),
        "blue" to androidx.compose.ui.graphics.Color(0xFF2196F3),
        "green" to androidx.compose.ui.graphics.Color(0xFF4CAF50),
        "yellow" to androidx.compose.ui.graphics.Color(0xFFFFEB3B),
        "purple" to androidx.compose.ui.graphics.Color(0xFF9C27B0),
        "orange" to androidx.compose.ui.graphics.Color(0xFFFF9800),
        "pink" to androidx.compose.ui.graphics.Color(0xFFE91E63),
        "cyan" to androidx.compose.ui.graphics.Color(0xFF00BCD4),
        "default" to androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    )
    
    // Estados del juego
    object GameStates {
        const val WAITING = "WAITING"
        const val COUNTDOWN = "COUNTDOWN"
        const val RACING = "RACING"
        const val PAUSED = "PAUSED"
        const val FINISHED = "FINISHED"
    }
    
    // Configuración de la UI
    object UI {
        const val HEADER_FONT_SIZE = 36
        const val COUNTDOWN_FONT_SIZE = 48
        const val BUTTON_HEIGHT = 80
        const val PLAYER_NAME_FONT_SIZE = 16f
        const val TRACK_CORNER_RADIUS = 16
    }
    
    // Configuración de Firebase
    object Firebase {
        const val DATABASE_URL = "https://carrera-avatar-default-rtdb.firebaseio.com/"
        const val GAME_ROOMS_PATH = "gameRooms"
        const val PLAYERS_PATH = "players"
        const val GAME_STATE_PATH = "gameState"
    }
}
