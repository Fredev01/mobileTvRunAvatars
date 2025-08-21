package com.angelyjesus.carreraavatar.firebase

import android.util.Log
import com.angelyjesus.carreraavatar.model.GameRoom
import com.angelyjesus.carreraavatar.model.Player
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseGameClient {
    
    private val database: FirebaseDatabase = Firebase.database
    private val roomsRef = database.getReference("rooms")
    
    /**
     * Unirse a una sala existente
     */
    fun joinRoom(roomCode: String, playerName: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val roomRef = roomsRef.child(roomCode)
        
        roomRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val room = snapshot.getValue<GameRoom>()
                if (room != null) {
                    // Verificar si la sala no está llena
                    if (room.players.size < 8) {
                        // Crear nuevo jugador
                        val newPlayer = Player(
                            id = "player_${System.currentTimeMillis()}",
                            name = playerName,
                            avatarId = null,
                            isConnected = true,
                            joinedAt = System.currentTimeMillis()
                        )
                        
                        // Agregar jugador a la sala
                        addPlayerToRoom(roomCode, newPlayer, onSuccess, onError)
                    } else {
                        onError("La sala está llena")
                    }
                } else {
                    onError("Error al leer la sala")
                }
            } else {
                onError("Sala no encontrada")
            }
        }.addOnFailureListener { exception ->
            onError("Error al conectar: ${exception.message}")
        }
    }
    
    /**
     * Agregar jugador a una sala
     */
    private fun addPlayerToRoom(roomCode: String, player: Player, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val playerRef = roomsRef.child(roomCode).child("players").child(player.id)
        
        playerRef.setValue(player)
            .addOnSuccessListener {
                Log.d("FirebaseGameClient", "Jugador ${player.name} se unió a sala $roomCode")
                onSuccess(player.id)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameClient", "Error uniéndose a sala: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Escuchar cambios en una sala específica
     */
    fun listenToRoom(roomCode: String): Flow<GameRoom?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue<GameRoom>()
                Log.d("FirebaseGameClient", "Cambio en sala $roomCode: ${room?.players?.size} jugadores")
                trySend(room)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseGameClient", "Error escuchando sala $roomCode: ${error.message}")
                trySend(null)
            }
        }
        
        roomsRef.child(roomCode).addValueEventListener(listener)
        
        awaitClose {
            roomsRef.child(roomCode).removeEventListener(listener)
        }
    }
    
    /**
     * Seleccionar avatar para un jugador
     */
    fun selectAvatar(roomCode: String, playerId: String, avatarId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val playerRef = roomsRef.child(roomCode).child("players").child(playerId).child("avatarId")
        
        playerRef.setValue(avatarId)
            .addOnSuccessListener {
                Log.d("FirebaseGameClient", "Avatar seleccionado: $avatarId para jugador $playerId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameClient", "Error seleccionando avatar: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Enviar tap input
     */
    fun sendTapInput(roomCode: String, playerId: String, timestamp: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val tapRef = roomsRef.child(roomCode).child("gameState").child("taps").child(playerId)
        
        val tapData = mapOf(
            "timestamp" to timestamp,
            "playerId" to playerId
        )
        
        tapRef.push().setValue(tapData)
            .addOnSuccessListener {
                Log.d("FirebaseGameClient", "Tap enviado para jugador $playerId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameClient", "Error enviando tap: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Salir de la sala
     */
    fun leaveRoom(roomCode: String, playerId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val playerRef = roomsRef.child(roomCode).child("players").child(playerId)
        
        playerRef.removeValue()
            .addOnSuccessListener {
                Log.d("FirebaseGameClient", "Jugador $playerId salió de sala $roomCode")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameClient", "Error saliendo de sala: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
}
