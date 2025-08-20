package com.example.appmobile.config

object GameConfig {
    // URL por defecto del servidor WebSocket
    const val DEFAULT_SERVER_URL = "ws://192.168.1.100:8080"
    
    // Tiempo de timeout para conexiones
    const val CONNECTION_TIMEOUT_SECONDS = 10L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    
    // Configuración de la sala
    const val ROOM_CODE_LENGTH = 4
    const val MAX_PLAYERS_PER_ROOM = 8
    
    // Configuración del juego
    const val GAME_DURATION_SECONDS = 60L
    const val TAP_COOLDOWN_MS = 100L
} 