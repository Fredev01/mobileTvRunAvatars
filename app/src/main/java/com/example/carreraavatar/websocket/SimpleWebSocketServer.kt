package com.example.carreraavatar.websocket

import android.content.Context
import android.util.Log
import com.example.carreraavatar.model.GameRoom
import com.example.carreraavatar.model.Player
import com.example.carreraavatar.model.WebSocketMessage
import com.example.carreraavatar.utils.NetworkUtils
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class SimpleWebSocketServer(
    private val context: Context,
    private val onPlayerJoined: (Player) -> Unit,
    private val onPlayerLeft: (String) -> Unit,
    private val onRoomInfoRequest: () -> GameRoom
) {
    private val gson = Gson()
    private val connections = ConcurrentHashMap<String, Socket>()
    private val playerCounter = AtomicInteger(1)
    
    private var serverSocket: ServerSocket? = null
    private val port = 8080
    private var isRunning = false
    private var localIpAddress: String = ""
    
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Obtener la IP local real
                localIpAddress = NetworkUtils.getLocalIpAddressFromWifi(context)
                Log.d("SimpleWebSocketServer", "IP local obtenida: $localIpAddress")
                
                serverSocket = ServerSocket(port)
                isRunning = true
                Log.d("SimpleWebSocketServer", "Servidor iniciado en puerto $port")
                Log.d("SimpleWebSocketServer", "URL del servidor: ${getServerUrl()}")
                
                while (isRunning) {
                    try {
                        val clientSocket = serverSocket?.accept()
                        clientSocket?.let { socket ->
                            handleClient(socket)
                        }
                    } catch (e: IOException) {
                        if (isRunning) {
                            Log.e("SimpleWebSocketServer", "Error aceptando conexión: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SimpleWebSocketServer", "Error al iniciar servidor: ${e.message}")
            }
        }
    }
    
    private fun handleClient(socket: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            val connectionId = java.util.UUID.randomUUID().toString()
            connections[connectionId] = socket
            
            try {
                val input = socket.getInputStream()
                val output = socket.getOutputStream()
                val buffer = ByteArray(1024)
                
                Log.d("SimpleWebSocketServer", "Nueva conexión: $connectionId")
                
                while (isRunning && !socket.isClosed) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead > 0) {
                        val message = String(buffer, 0, bytesRead)
                        processMessage(message, socket)
                    }
                }
            } catch (e: Exception) {
                Log.e("SimpleWebSocketServer", "Error en conexión $connectionId: ${e.message}")
            } finally {
                connections.remove(connectionId)
                try {
                    socket.close()
                } catch (e: Exception) {
                    Log.e("SimpleWebSocketServer", "Error cerrando socket: ${e.message}")
                }
                Log.d("SimpleWebSocketServer", "Conexión cerrada: $connectionId")
            }
        }
    }
    
    private fun processMessage(message: String, socket: Socket) {
        try {
            val joinMessage = gson.fromJson(message, WebSocketMessage.JoinRoom::class.java)
            handleJoinRoom(joinMessage, socket)
        } catch (e: Exception) {
            Log.e("SimpleWebSocketServer", "Error procesando mensaje: ${e.message}")
            sendError(socket, "Error procesando mensaje")
        }
    }
    
    private fun handleJoinRoom(joinMessage: WebSocketMessage.JoinRoom, socket: Socket) {
        val room = onRoomInfoRequest()
        
        if (joinMessage.roomCode != room.code) {
            sendError(socket, "Código de sala incorrecto")
            return
        }
        
        val player = Player(
            id = "player_${playerCounter.getAndIncrement()}",
            name = joinMessage.playerName
        )
        
        room.players.add(player)
        onPlayerJoined(player)
        
        // Enviar confirmación al cliente
        val successMessage = WebSocketMessage.Success("Te has unido a la sala ${room.code}")
        sendMessage(socket, successMessage)
        
        // Enviar información actualizada de la sala
        val roomInfo = WebSocketMessage.RoomInfo(room.code, room.players)
        sendMessage(socket, roomInfo)
        
        Log.d("SimpleWebSocketServer", "Jugador ${player.name} se unió a la sala")
    }
    
    private fun sendMessage(socket: Socket, message: WebSocketMessage) {
        try {
            val json = gson.toJson(message)
            val output = socket.getOutputStream()
            output.write(json.toByteArray())
            output.flush()
        } catch (e: Exception) {
            Log.e("SimpleWebSocketServer", "Error enviando mensaje: ${e.message}")
        }
    }
    
    private fun sendError(socket: Socket, errorMessage: String) {
        val error = WebSocketMessage.Error(errorMessage)
        sendMessage(socket, error)
    }
    
    fun stop() {
        isRunning = false
        connections.values.forEach { socket ->
            try {
                socket.close()
            } catch (e: Exception) {
                Log.e("SimpleWebSocketServer", "Error cerrando conexión: ${e.message}")
            }
        }
        connections.clear()
        
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            Log.e("SimpleWebSocketServer", "Error cerrando servidor: ${e.message}")
        }
        
        Log.d("SimpleWebSocketServer", "Servidor detenido")
    }
    
    fun getServerUrl(): String {
        return NetworkUtils.getWebSocketUrl(localIpAddress, port)
    }
    
    fun getLocalIpAddress(): String {
        return localIpAddress
    }
}
