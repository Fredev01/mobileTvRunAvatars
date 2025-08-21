package com.angelyjesus.carreraavatar.model

data class Player @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val avatarId: String? = null,
    val isConnected: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis(),
    val progress: Float = 0f, // Progreso en la carrera (0.0f a 1.0f)
    val tapCount: Int = 0 // Contador de taps del jugador
)
