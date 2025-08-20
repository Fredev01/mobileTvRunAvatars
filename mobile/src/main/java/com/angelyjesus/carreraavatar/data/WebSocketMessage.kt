package com.angelyjesus.carreraavatar.data

import kotlinx.serialization.Serializable

@Serializable
sealed class WebSocketMessage

// Cliente → Servidor - Compatible con servidor TV
@Serializable
data class JoinRoom(
    val roomCode: String,
    val playerName: String
) : WebSocketMessage()

@Serializable
data class SelectAvatar(
    val playerId: String,
    val avatarId: String
) : WebSocketMessage()

@Serializable
data class TapInput(
    val playerId: String,
    val timestamp: Long
) : WebSocketMessage()

// Servidor → Cliente - Compatible con servidor TV
@Serializable
data class PlayerJoined(
    val player: Player
) : WebSocketMessage()

@Serializable
data class PlayerLeft(
    val playerId: String
) : WebSocketMessage()

@Serializable
data class RoomInfo(
    val roomCode: String,
    val players: List<Player>
) : WebSocketMessage()

@Serializable
data class Error(
    val message: String
) : WebSocketMessage()

@Serializable
data class Success(
    val message: String
) : WebSocketMessage()

@Serializable
data class Player(
    val id: String,
    val name: String,
    val avatarId: String? = null,
    val isConnected: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis()
) 