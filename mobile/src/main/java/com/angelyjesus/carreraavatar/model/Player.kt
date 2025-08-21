package com.angelyjesus.carreraavatar.model

data class Player @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val avatarId: String? = null,
    val isConnected: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis()
)
