package com.angelyjesus.carreraavatar.config

object GameConfig {
    // Configuración de Firebase
    const val FIREBASE_DATABASE_URL = "https://carrera-avatar-default-rtdb.firebaseio.com"
    
    // Configuración de la sala
    const val ROOM_CODE_LENGTH = 4
    const val MAX_PLAYERS_PER_ROOM = 8
    
    // Configuración del juego
    const val GAME_DURATION_SECONDS = 60L
    const val TAP_COOLDOWN_MS = 100L
    
    // Timeouts de Firebase
    const val FIREBASE_TIMEOUT_MS = 10000L
} 