package com.angelyjesus.carreraavatar.websocket

import android.content.Context
import android.util.Log
import com.angelyjesus.carreraavatar.model.GameRoom
import com.angelyjesus.carreraavatar.model.Player
import com.angelyjesus.carreraavatar.model.WebSocketMessage
import com.angelyjesus.carreraavatar.utils.NetworkUtils
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class SimpleWebSocketServer(
    private val context: Context,
    private val onPlayerJoined: (Player) -> Unit,
    private val onPlayerLeft: (String) -> Unit,
    private val onRoomInfoRequest: () -> GameRoom
) : WebSocketServer(InetSocketAddress("0.0.0.0", 8080)) {
    
    private val gson = Gson()
    private val connections = ConcurrentHashMap<String, WebSocket>()
    private val playerCounter = AtomicInteger(1)
    private var localIpAddress: String = ""
    
    override fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Obtener la IP local real
                localIpAddress = NetworkUtils.getLocalIpAddressFromWifi(context)
                Log.d("SimpleWebSocketServer", "IP local obtenida: $localIpAddress")
                
                Log.d("SimpleWebSocketServer", "Iniciando servidor WebSocket en puerto 8080")
                Log.d("SimpleWebSocketServer", "URL del servidor: ${getServerUrl()}")
                
                super.start()
                Log.d("SimpleWebSocketServer", "Servidor WebSocket iniciado exitosamente")
                
            } catch (e: Exception) {
                Log.e("SimpleWebSocketServer", "Error al iniciar servidor: ${e.message}")
            }
        }
    }
    
    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val connectionId = java.util.UUID.randomUUID().toString()
        connections[connectionId] = conn
        Log.d("SimpleWebSocketServer", "=== NUEVA CONEXIÓN ===")
        Log.d("SimpleWebSocketServer", "ID de conexión: $connectionId")
        Log.d("SimpleWebSocketServer", "Dirección remota: ${conn.remoteSocketAddress}")
        Log.d("SimpleWebSocketServer", "Dirección local: ${conn.localSocketAddress}")
        Log.d("SimpleWebSocketServer", "Headers del handshake: ${handshake.resourceDescriptor}")
        Log.d("SimpleWebSocketServer", "==========================")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        val connectionId = connections.entries.find { it.value == conn }?.key
        connections.remove(connectionId)
        Log.d("SimpleWebSocketServer", "Conexión cerrada: $connectionId, código: $code, razón: $reason")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        Log.d("SimpleWebSocketServer", "=== MENSAJE RECIBIDO ===")
        Log.d("SimpleWebSocketServer", "Tipo de mensaje: ${message::class.simpleName}")
        Log.d("SimpleWebSocketServer", "Longitud del mensaje: ${message.length}")
        Log.d("SimpleWebSocketServer", "Contenido del mensaje: '$message'")
        Log.d("SimpleWebSocketServer", "Bytes del mensaje: ${message.toByteArray().joinToString(", ") { "0x%02X".format(it) }}")
        Log.d("SimpleWebSocketServer", "==========================")
        
        try {
            val joinMessage = gson.fromJson(message, WebSocketMessage.JoinRoom::class.java)
            Log.d("SimpleWebSocketServer", "Mensaje parseado exitosamente: $joinMessage")
            handleJoinRoom(joinMessage, conn)
        } catch (e: Exception) {
            Log.e("SimpleWebSocketServer", "Error procesando mensaje: ${e.message}", e)
            Log.e("SimpleWebSocketServer", "Stack trace completo:", e)
            sendError(conn, "Error procesando mensaje: ${e.message}")
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        Log.e("SimpleWebSocketServer", "Error en WebSocket: ${ex.message}")
    }

    override fun onStart() {
        Log.d("SimpleWebSocketServer", "Servidor WebSocket iniciado en puerto 8080")
    }
    

    
    private fun handleJoinRoom(joinMessage: WebSocketMessage.JoinRoom, conn: WebSocket) {
        val room = onRoomInfoRequest()
        
        if (joinMessage.roomCode != room.code) {
            sendError(conn, "Código de sala incorrecto")
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
        sendMessage(conn, successMessage)
        
        // Enviar información actualizada de la sala
        val roomInfo = WebSocketMessage.RoomInfo(room.code, room.players)
        sendMessage(conn, roomInfo)
        
        Log.d("SimpleWebSocketServer", "Jugador ${player.name} se unió a la sala")
    }
    
    private fun sendMessage(conn: WebSocket, message: WebSocketMessage) {
        try {
            val json = gson.toJson(message)
            Log.d("SimpleWebSocketServer", "Enviando mensaje: $json")
            conn.send(json)
        } catch (e: Exception) {
            Log.e("SimpleWebSocketServer", "Error enviando mensaje: ${e.message}")
        }
    }
    
    private fun sendError(conn: WebSocket, errorMessage: String) {
        val error = WebSocketMessage.Error(errorMessage)
        sendMessage(conn, error)
    }
    

    
    override fun stop() {
        connections.values.forEach { conn ->
            try {
                conn.close()
            } catch (e: Exception) {
                Log.e("SimpleWebSocketServer", "Error cerrando conexión: ${e.message}")
            }
        }
        connections.clear()
        
        try {
            super.stop()
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
