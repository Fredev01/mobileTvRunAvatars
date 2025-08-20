package com.angelyjesus.carreraavatar.model

sealed class WebSocketMessage {
    data class JoinRoom(
        val roomCode: String,
        val playerName: String
    ) : WebSocketMessage()
    
    data class PlayerJoined(
        val player: Player
    ) : WebSocketMessage()
    
    data class PlayerLeft(
        val playerId: String
    ) : WebSocketMessage()
    
    data class RoomInfo(
        val roomCode: String,
        val players: List<Player>
    ) : WebSocketMessage()
    
    data class Error(
        val message: String
    ) : WebSocketMessage()
    
    data class Success(
        val message: String
    ) : WebSocketMessage()
}
