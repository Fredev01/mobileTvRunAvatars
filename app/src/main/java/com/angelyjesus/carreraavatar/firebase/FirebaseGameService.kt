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

class FirebaseGameService {
    
    private val database: FirebaseDatabase = Firebase.database
    private val roomsRef = database.getReference("rooms")
    
    /**
     * Crear una nueva sala de juego
     */
    fun createGameRoom(roomCode: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val room = GameRoom(code = roomCode)
        
        roomsRef.child(roomCode).setValue(room)
            .addOnSuccessListener {
                Log.d("FirebaseGameService", "Sala creada exitosamente: $roomCode")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameService", "Error creando sala: ${exception.message}")
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
                Log.d("FirebaseGameService", "Cambio en sala $roomCode: $room")
                trySend(room)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseGameService", "Error escuchando sala $roomCode: ${error.message}")
                trySend(null)
            }
        }
        
        roomsRef.child(roomCode).addValueEventListener(listener)
        
        awaitClose {
            roomsRef.child(roomCode).removeEventListener(listener)
        }
    }
    
    /**
     * Agregar un jugador a una sala
     */
    fun addPlayerToRoom(roomCode: String, player: Player, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val playerRef = roomsRef.child(roomCode).child("players").child(player.id)
        
        playerRef.setValue(player)
            .addOnSuccessListener {
                Log.d("FirebaseGameService", "Jugador ${player.name} agregado a sala $roomCode")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameService", "Error agregando jugador: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Remover un jugador de una sala
     */
    fun removePlayerFromRoom(roomCode: String, playerId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val playerRef = roomsRef.child(roomCode).child("players").child(playerId)
        
        playerRef.removeValue()
            .addOnSuccessListener {
                Log.d("FirebaseGameService", "Jugador $playerId removido de sala $roomCode")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameService", "Error removiendo jugador: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Actualizar el estado del juego
     */
    fun updateGameState(roomCode: String, gameState: Map<String, Any>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val gameStateRef = roomsRef.child(roomCode).child("gameState")
        
        gameStateRef.setValue(gameState)
            .addOnSuccessListener {
                Log.d("FirebaseGameService", "Estado del juego actualizado en sala $roomCode")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameService", "Error actualizando estado: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Actualizar el progreso de un jugador específico
     */
    fun updatePlayerProgress(roomCode: String, playerId: String, progress: Float, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val playerRef = roomsRef.child(roomCode).child("players").child(playerId).child("progress")
        
        playerRef.setValue(progress)
            .addOnSuccessListener {
                Log.d("FirebaseGameService", "Progreso actualizado para jugador $playerId: ${(progress * 100).toInt()}%")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameService", "Error actualizando progreso: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
    
    /**
     * Eliminar una sala cuando el juego termine
     */
    fun deleteRoom(roomCode: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        roomsRef.child(roomCode).removeValue()
            .addOnSuccessListener {
                Log.d("FirebaseGameService", "Sala $roomCode eliminada")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseGameService", "Error eliminando sala: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
}
