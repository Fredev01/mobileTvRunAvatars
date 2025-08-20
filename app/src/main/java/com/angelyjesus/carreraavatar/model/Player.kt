package com.angelyjesus.carreraavatar.model

data class Player(
    val id: String,
    val name: String,
    val isConnected: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis()
)
