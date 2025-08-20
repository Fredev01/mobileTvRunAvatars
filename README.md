# Carrera de Avatares - Android TV Multiplayer Game

Una aplicación Android TV que funciona como host para un juego multijugador local. Los jugadores pueden conectarse desde sus dispositivos móviles usando un código de sala único.

## Características

### Fase 1 - Lobby del Juego
- ✅ **Código de sala único**: Genera automáticamente un código de 4 dígitos al iniciar
- ✅ **Servidor WebSocket local**: Permite conexiones desde dispositivos móviles
- ✅ **Validación de código**: Verifica que el código ingresado coincida con el de la TV
- ✅ **Lista de jugadores en tiempo real**: Muestra los jugadores conectados
- ✅ **UI optimizada para Android TV**: Interfaz diseñada para control remoto

## Estructura del Proyecto

```
app/src/main/java/com/example/carreraavatar/
├── MainActivity.kt                    # Actividad principal
├── model/                            # Modelos de datos
│   ├── Player.kt                     # Modelo de jugador
│   ├── GameRoom.kt                   # Modelo de sala de juego
│   └── WebSocketMessage.kt           # Mensajes WebSocket
├── websocket/                        # Servidor WebSocket
│   └── SimpleWebSocketServer.kt      # Implementación del servidor
├── viewmodel/                        # ViewModels
│   └── GameViewModel.kt              # Lógica de la aplicación
├── ui/                               # Componentes de UI
│   ├── components/                   # Componentes reutilizables
│   │   ├── RoomCodeDisplay.kt        # Display del código de sala
│   │   └── PlayersList.kt            # Lista de jugadores
│   └── screens/                      # Pantallas
│       └── GameLobbyScreen.kt        # Pantalla principal del lobby
└── client/                           # Ejemplo de cliente móvil
    └── MobileClientExample.kt        # Código de ejemplo para móviles
```

## Cómo Funciona

### 1. Inicio de la Aplicación
- La app genera automáticamente un código de sala único de 4 dígitos
- Inicia un servidor WebSocket local en el puerto 8080
- Muestra el código en pantalla para que los jugadores lo vean

### 2. Conexión de Jugadores
- Los jugadores móviles ingresan el código de sala
- El servidor valida el código
- Si es correcto, el jugador se une a la sala
- La lista de jugadores se actualiza en tiempo real

### 3. Comunicación WebSocket
- Los mensajes se envían en formato JSON
- Tipos de mensajes soportados:
  - `JoinRoom`: Solicitud de unión a sala
  - `PlayerJoined`: Confirmación de jugador unido
  - `PlayerLeft`: Notificación de jugador desconectado
  - `RoomInfo`: Información actualizada de la sala
  - `Error`: Mensajes de error
  - `Success`: Confirmaciones exitosas

## Instalación y Uso

### Requisitos
- Android TV o dispositivo Android con soporte para TV
- Android API 30+ (Android 11+)
- Dispositivos móviles en la misma red WiFi

### Compilación
```bash
# Clonar el repositorio
git clone <repository-url>
cd mobileTvRunAvatars

# Compilar y instalar
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Uso en Android TV
1. Instalar la aplicación en el dispositivo Android TV
2. Abrir la aplicación
3. Anotar el código de sala que aparece en pantalla
4. Esperar a que los jugadores se conecten

### Conexión desde Dispositivos Móviles
Los jugadores pueden usar el código de ejemplo en `MobileClientExample.kt`:

```kotlin
val client = MobileClientExample()
client.connectToServer(
    serverUrl = "ws://IP_DE_LA_TV:8080",
    roomCode = "1234", // Código mostrado en la TV
    playerName = "MiNombre",
    onMessageReceived = { message ->
        // Procesar mensajes recibidos
    },
    onConnectionStatus = { status ->
        // Actualizar UI con estado de conexión
    }
)
```

## Configuración de Red

### Obtener IP de la Android TV
```bash
# En la Android TV, ejecutar:
adb shell ip addr show wlan0
```

### Configurar Firewall
Asegúrate de que el puerto 8080 esté abierto en la red local.

## Desarrollo

### Agregar Nuevas Funcionalidades
La estructura modular permite fácil extensión:

1. **Nuevos tipos de mensajes**: Agregar en `WebSocketMessage.kt`
2. **Nuevas pantallas**: Crear en `ui/screens/`
3. **Lógica de juego**: Extender `GameViewModel.kt`
4. **Componentes UI**: Agregar en `ui/components/`

### Próximas Fases
- Fase 2: Implementación del juego de carreras
- Fase 3: Avatares personalizables
- Fase 4: Múltiples modos de juego

## Dependencias Principales

- **OkHttp**: Cliente HTTP y WebSocket
- **Gson**: Serialización JSON
- **Coroutines**: Programación asíncrona
- **Jetpack Compose**: UI declarativa
- **Android TV Compose**: Componentes específicos para TV

## Troubleshooting

### Problemas Comunes

1. **No se pueden conectar los móviles**
   - Verificar que estén en la misma red WiFi
   - Comprobar que el puerto 8080 esté abierto
   - Usar la IP correcta de la Android TV

2. **La app no inicia**
   - Verificar permisos de red en AndroidManifest.xml
   - Comprobar que el dispositivo soporte Android TV

3. **Errores de WebSocket**
   - Revisar logs en Logcat
   - Verificar que el servidor esté iniciado correctamente

## Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo LICENSE para más detalles.
