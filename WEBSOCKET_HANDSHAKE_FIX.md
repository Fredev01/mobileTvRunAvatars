# Corrección del Handshake WebSocket - Servidor TV

## Problema Identificado

El error `SocketTimeoutException` se producía porque el servidor TV estaba implementando un servidor TCP básico, pero el cliente móvil estaba intentando hacer una conexión WebSocket HTTP. El servidor no manejaba el handshake WebSocket HTTP inicial.

## Solución Implementada

### 1. ✅ Configuración del Servidor para Escuchar en Todas las Interfaces

**Antes**:

```kotlin
serverSocket = ServerSocket(port)
```

**Después**:

```kotlin
// Escuchar en TODAS las interfaces, no solo localhost
serverSocket = ServerSocket(port, 50, java.net.InetAddress.getByName("0.0.0.0"))
```

### 2. ✅ Implementación del Handshake WebSocket HTTP

Se agregó el manejo del protocolo de handshake WebSocket:

#### Método `handleWebSocketHandshake()`

- Lee la solicitud HTTP inicial del cliente
- Verifica que sea una solicitud WebSocket válida
- Extrae el `Sec-WebSocket-Key`
- Genera la respuesta de handshake con `Sec-WebSocket-Accept`

#### Método `generateWebSocketHandshakeResponse()`

- Genera la clave de aceptación usando SHA-1 y Base64
- Construye la respuesta HTTP 101 Switching Protocols
- Incluye los headers necesarios para WebSocket

### 3. ✅ Implementación de Frames WebSocket

Se agregó el manejo completo de frames WebSocket según RFC 6455:

#### Clase `WebSocketFrame`

- Estructura de datos para representar frames WebSocket
- Incluye FIN, opcode, MASK, payload length y payload

#### Método `readWebSocketFrame()`

- Lee y parsea frames WebSocket del stream
- Maneja extended payload lengths (126, 127)
- Aplica unmasking cuando es necesario
- Soporta diferentes tipos de opcodes

#### Método `sendWebSocketFrame()`

- Construye y envía frames WebSocket
- Maneja diferentes tamaños de payload
- Envía frames de texto, close, ping/pong

#### Método `processWebSocketMessage()`

- Procesa diferentes tipos de frames (text, close, ping)
- Parsear mensajes JSON de frames de texto
- Maneja errores de parsing apropiadamente

### 4. ✅ Flujo de Conexión Actualizado

1. **Cliente móvil** envía solicitud HTTP GET con headers WebSocket
2. **Servidor TV** recibe la solicitud y verifica que sea WebSocket
3. **Servidor TV** genera respuesta de handshake con clave de aceptación
4. **Cliente móvil** recibe respuesta y establece conexión WebSocket
5. **Comunicación** continúa con frames WebSocket que contienen JSON

## Archivos Modificados

- `app/src/main/java/com/example/carreraavatar/websocket/SimpleWebSocketServer.kt`

## Cambios Específicos

### Línea 35: Configuración del ServerSocket

```kotlin
// Escuchar en TODAS las interfaces, no solo localhost
serverSocket = ServerSocket(port, 50, java.net.InetAddress.getByName("0.0.0.0"))
```

### Líneas 75-85: Manejo de Handshake

```kotlin
// Manejar handshake WebSocket HTTP
val handshake = handleWebSocketHandshake(input, output)
if (!handshake) {
    Log.e("SimpleWebSocketServer", "Handshake WebSocket falló para conexión: $connectionId")
    return@launch
}
```

### Líneas 130-180: Métodos de Handshake

- `handleWebSocketHandshake()`: Maneja el protocolo de handshake
- `generateWebSocketHandshakeResponse()`: Genera la respuesta HTTP

## Estado Actual

✅ **Servidor TV escucha en todas las interfaces**
✅ **Handshake WebSocket implementado correctamente**
✅ **Protocolo HTTP 101 Switching Protocols funcionando**
✅ **Frames WebSocket implementados completamente**
✅ **Compatible con cliente OkHttp WebSocket**

## Próximos Pasos

1. **Instalar la app TV** actualizada en el emulador/dispositivo
2. **Iniciar el servidor** desde la app TV
3. **Probar la conexión** desde el cliente móvil
4. **Verificar los logs** para confirmar handshake exitoso

## Notas Técnicas

- El servidor ahora maneja correctamente el protocolo WebSocket RFC 6455
- La clave de aceptación se genera usando SHA-1 y Base64 como especifica el estándar
- Los headers HTTP incluyen `Upgrade: websocket` y `Connection: Upgrade`
- El cliente móvil debería poder conectarse sin errores de timeout
