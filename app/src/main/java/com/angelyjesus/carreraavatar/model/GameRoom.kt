package com.angelyjesus.carreraavatar.model

data class GameRoom(
    val code: String,
    val players: MutableList<Player> = mutableListOf(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
