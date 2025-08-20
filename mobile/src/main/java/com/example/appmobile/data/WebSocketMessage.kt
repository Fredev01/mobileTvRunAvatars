package com.example.appmobile.data

import kotlinx.serialization.Serializable

@Serializable
sealed class WebSocketMessage {
    abstract val type: String
}

// Cliente → Servidor
@Serializable
data class JoinRoomMessage(
    override val type: String = "JOIN_ROOM",
    val data: JoinRoomData
) : WebSocketMessage()

@Serializable
data class JoinRoomData(
    val code: String,
    val playerName: String
)

@Serializable
data class SelectAvatarMessage(
    override val type: String = "SELECT_AVATAR",
    val data: SelectAvatarData
) : WebSocketMessage()

@Serializable
data class SelectAvatarData(
    val avatarId: String
)

@Serializable
data class TapMessage(
    override val type: String = "TAP",
    val data: TapData
) : WebSocketMessage()

@Serializable
data class TapData(
    val timestamp: Long
)

// Servidor → Cliente
@Serializable
data class RoomJoinedMessage(
    override val type: String = "ROOM_JOINED",
    val data: RoomJoinedData
) : WebSocketMessage()

@Serializable
data class RoomJoinedData(
    val playerId: String,
    val roomCode: String
)

@Serializable
data class AvatarSelectedMessage(
    override val type: String = "AVATAR_SELECTED",
    val data: AvatarSelectedData
) : WebSocketMessage()

@Serializable
data class AvatarSelectedData(
    val playerId: String,
    val avatarId: String
)

@Serializable
data class GameStartMessage(
    override val type: String = "GAME_START",
    val data: GameStartData
) : WebSocketMessage()

@Serializable
data class GameStartData(
    val players: List<PlayerInfo>
)

@Serializable
data class PlayerInfo(
    val playerId: String,
    val playerName: String,
    val avatarId: String
) 