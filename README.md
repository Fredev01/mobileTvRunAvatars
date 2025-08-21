# Carrera Avatar - Android Mobile Client

Cliente Android Mobile para el juego multijugador de carreras con avatares.

## Descripción

Este es el cliente móvil Android que permite a los jugadores conectarse a una sala de juego, seleccionar su avatar y participar en carreras multijugador controladas por la TV Android (servidor).

## Características

### ✅ Configuración del Proyecto Android Mobile

- Proyecto configurado con Jetpack Compose
- Dependencias WebSocket y navegación agregadas
- Permisos de internet configurados

### ✅ Pantalla de Conexión

- Input para código de sala (4 caracteres)
- Campo para nombre del jugador
- Configuración de URL del servidor
- Manejo de estados de conexión (Desconectado, Conectando, Conectado, Error)
- Cliente WebSocket integrado

### ✅ Selección de Avatar

- Grid de avatares disponibles (16 opciones)
- Avatares con emojis de vehículos (🏎️, 🏍️, 🚲)
- Diferentes colores para cada tipo de vehículo
- Confirmación de selección
- Envío de selección al servidor TV

### ✅ Pantalla de Juego

- Botón de tap animado para controlar el avatar
- Contador de taps del jugador
- Información del estado del juego
- Lista de jugadores conectados
- Pantalla de espera cuando el juego no ha iniciado

## Protocolo de Comunicación WebSocket

### Cliente → Servidor

```json
{
  "type": "JOIN_ROOM",
  "data": {"code": "1234", "playerName": "Player1"}
}

{
  "type": "SELECT_AVATAR",
  "data": {"avatarId": "car_red"}
}

{
  "type": "TAP",
  "data": {"timestamp": 1642012345678}
}
```

### Servidor → Cliente

```json
{
  "type": "ROOM_JOINED",
  "data": {"playerId": "abc123", "roomCode": "1234"}
}

{
  "type": "AVATAR_SELECTED",
  "data": {"playerId": "abc123", "avatarId": "car_red"}
}

{
  "type": "GAME_START",
  "data": {
    "players": [
      {"playerId": "abc123", "playerName": "Player1", "avatarId": "car_red"}
    ]
  }
}
```

**Nota**: Internamente, el cliente móvil usa `messageType` en lugar de `type` para evitar conflictos con Kotlinx Serialization.

## Avatares Disponibles

### Coches (🏎️)

- Coche Rojo (`car_red`)
- Coche Azul (`car_blue`)
- Coche Verde (`car_green`)
- Coche Amarillo (`car_yellow`)
- Coche Púrpura (`car_purple`)
- Coche Naranja (`car_orange`)
- Coche Rosa (`car_pink`)
- Coche Cian (`car_cyan`)

### Motos (🏍️)

- Moto Roja (`motorcycle_red`)
- Moto Azul (`motorcycle_blue`)
- Moto Verde (`motorcycle_green`)
- Moto Amarilla (`motorcycle_yellow`)

### Bicicletas (🚲)

- Bici Roja (`bike_red`)
- Bici Azul (`bike_blue`)
- Bici Verde (`bike_green`)
- Bici Amarilla (`bike_yellow`)

## Configuración

### URL del Servidor

Por defecto: `ws://192.168.1.100:8080`

Puedes cambiar esta URL en la pantalla de conexión o modificar `GameConfig.DEFAULT_SERVER_URL`.

### Permisos Requeridos

- `INTERNET`: Para conexión WebSocket
- `ACCESS_NETWORK_STATE`: Para verificar estado de red

## Arquitectura

### Estructura de Paquetes

```
appmobile/
├── data/
│   ├── WebSocketMessage.kt      # Modelos de mensajes WebSocket
│   └── Avatar.kt                # Modelo de avatar y repositorio
├── network/
│   └── WebSocketService.kt      # Servicio WebSocket
├── ui/
│   ├── screens/
│   │   ├── ConnectionScreen.kt  # Pantalla de conexión
│   │   ├── AvatarSelectionScreen.kt # Selección de avatar
│   │   └── GameScreen.kt        # Pantalla de juego
│   └── viewmodel/
│       └── MainViewModel.kt     # ViewModel principal
├── config/
│   └── GameConfig.kt            # Configuración del juego
└── MainActivity.kt              # Actividad principal
```

### Tecnologías Utilizadas

- **Jetpack Compose**: UI moderna declarativa
- **ViewModel**: Gestión de estado y lógica de negocio
- **StateFlow**: Flujos reactivos para datos
- **OkHttp WebSocket**: Cliente WebSocket
- **Kotlinx Serialization**: Serialización JSON
- **Coroutines**: Programación asíncrona

## Flujo de Uso

1. **Conexión**: El usuario ingresa el código de sala y su nombre
2. **Selección de Avatar**: Elige un vehículo de la grilla disponible
3. **Juego**: Toca el botón para acelerar su vehículo en la carrera
4. **Resultados**: Ve el progreso en tiempo real

## Desarrollo

### Requisitos

- Android Studio Hedgehog o superior
- Android SDK 31+ (API 31)
- Kotlin 2.0.21+

### Compilación

```bash
./gradlew assembleDebug
```

### Instalación

```bash
./gradlew installDebug
```

## Próximos Pasos

- [ ] Implementar persistencia local de configuración
- [ ] Agregar efectos de sonido
- [ ] Implementar reconexión automática
- [ ] Agregar animaciones de transición entre pantallas
- [ ] Implementar modo offline para pruebas
- [ ] Agregar configuración de calidad gráfica
