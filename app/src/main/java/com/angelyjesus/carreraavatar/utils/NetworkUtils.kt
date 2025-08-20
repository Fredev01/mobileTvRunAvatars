package com.angelyjesus.carreraavatar.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.net.NetworkInterface
import java.util.*

object NetworkUtils {
    
    /**
     * Obtiene la dirección IP local del dispositivo
     */
    fun getLocalIpAddress(): String {
        try {
            // Intentar obtener la IP de la interfaz WiFi
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    
                    // Filtrar solo direcciones IPv4 que no sean loopback
                    if (!inetAddress.isLoopbackAddress && 
                        inetAddress.hostAddress.indexOf(':') < 0) {
                        
                        val ip = inetAddress.hostAddress
                        Log.d("NetworkUtils", "IP encontrada: $ip")
                        return ip
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error obteniendo IP: ${e.message}")
        }
        
        // Fallback a IP por defecto
        return "192.168.1.100"
    }
    
    /**
     * Obtiene la dirección IP usando WifiManager (más confiable en Android)
     */
    fun getLocalIpAddressFromWifi(context: Context): String {
        try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            
            if (wifiInfo != null && wifiInfo.ipAddress != 0) {
                val ipAddress = wifiInfo.ipAddress
                val ip = String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    ipAddress and 0xff,
                    ipAddress shr 8 and 0xff,
                    ipAddress shr 16 and 0xff,
                    ipAddress shr 24 and 0xff
                )
                Log.d("NetworkUtils", "IP WiFi: $ip")
                return ip
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error obteniendo IP WiFi: ${e.message}")
        }
        
        // Si no se puede obtener la IP WiFi, usar el método genérico
        return getLocalIpAddress()
    }
    
    /**
     * Formatea la URL completa del servidor WebSocket
     */
    fun getWebSocketUrl(ipAddress: String, port: Int = 8080): String {
        return "ws://$ipAddress:$port"
    }
    
    /**
     * Verifica si una dirección IP es válida
     */
    fun isValidIpAddress(ip: String): Boolean {
        val parts = ip.split(".")
        if (parts.size != 4) return false
        
        return parts.all { part ->
            try {
                val num = part.toInt()
                num in 0..255
            } catch (e: NumberFormatException) {
                false
            }
        }
    }
}
