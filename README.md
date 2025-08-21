# Carrera de Avatares - Android TV Game Host

Una aplicación Android TV que funciona como host para un juego multijugador de carreras con avatares. Los jugadores pueden conectarse desde sus dispositivos móviles usando un código de sala único.

## Características

### ✅ **Fase 1 - Lobby del Juego Completada**
- **Código de sala único**: Genera automáticamente un código de 4 dígitos al iniciar
- **Servidor Firebase**: Usa Firebase Realtime Database para conexiones en tiempo real
- **Validación de código**: Los móviles se conectan usando el código mostrado en la TV
- **Lista de jugadores en tiempo real**: Muestra los jugadores conectados y sus progresos
- **Botón de iniciar juego**: Permite al host iniciar la carrera cuando esté listo
- **UI optimizada para Android TV**: Interfaz diseñada para control remoto

### ✅ **Fase 2 - Motor de Juego Implementado**
- **Canvas de pista de carreras**: Pista visual con líneas de salida y meta
- **Sistema de coordenadas**: Posicionamiento preciso de avatares en la pista
- **Motor de juego**: Procesa eventos "tap" de móviles en tiempo real
- **Actualización de posiciones**: Los avatares se mueven según los taps
- **Detección de ganador**: Sistema automático para detectar quién cruza la meta
- **Pantalla de resultados**: Muestra el ranking final de la carrera

## Estructura del Proyecto

```
app/src/main/java/com/angelyjesus/carreraavatar/
├── MainActivity.kt                    # Actividad principal
├── model/                            # Modelos de datos
│   ├── Player.kt                     # Modelo de jugador
│   ├── GameRoom.kt                   # Modelo de sala de juego
│   └── GameState.kt                  # Estado del juego
├── game/                             # Motor del juego
│   └── GameEngine.kt                 # Lógica principal del juego
├── viewmodel/                        # ViewModels
│   └── GameViewModel.kt              # Lógica de la aplicación
├── ui/                               # Componentes de UI
│   ├── components/                   # Componentes reutilizables
│   │   ├── RoomCodeDisplay.kt        # Display del código de sala
│   │   ├── PlayersList.kt            # Lista de jugadores
│   │   └── StartGameButton.kt        # Botón de iniciar juego
│   ├── screens/                      # Pantallas
│   │   ├── GameLobbyScreen.kt        # Pantalla principal del lobby
│   │   └── GameScreen.kt             # Pantalla de juego con pista
│   └── theme/                        # Tema y estilos
│       ├── Color.kt                  # Colores del tema
│       ├── Theme.kt                  # Configuración del tema
│       └── Type.kt                   # Tipografía
├── navigation/                       # Navegación entre pantallas
│   └── GameNavigation.kt             # Sistema de navegación
└── config/                           # Configuración del juego
    └── GameConfig.kt                 # Constantes y configuración
```

## Cómo Funciona

### 1. **Inicio de la Aplicación**
- La app genera automáticamente un código de sala único de 4 dígitos
- Crea una sala en Firebase Realtime Database
- Muestra el código en pantalla para que los jugadores lo vean

### 2. **Conexión de Jugadores**
- Los jugadores móviles ingresan el código desde sus dispositivos
- Se conectan a Firebase usando el código de sala
- La lista de jugadores se actualiza en tiempo real

### 3. **Inicio del Juego**
- El host (Android TV) ve cuántos jugadores están conectados
- Presiona el botón "🚀 INICIAR JUEGO" cuando esté listo
- El estado cambia a "JUEGO EN CURSO" y se inicia la carrera

### 4. **Durante la Carrera**
- Los jugadores móviles tocan sus pantallas para acelerar
- Cada tap avanza el avatar 1% en la pista
- La TV muestra en tiempo real el progreso de todos los jugadores
- El primer jugador en llegar al 100% gana la carrera

### 5. **Finalización**
- Se detecta automáticamente el ganador
- Se muestra la pantalla de resultados con el ranking
- Opción de volver al lobby para una nueva carrera

## Tecnologías Utilizadas

- **Firebase Realtime Database**: Para sincronización en tiempo real
- **Jetpack Compose**: UI moderna declarativa
- **Canvas API**: Para dibujar la pista de carreras
- **ViewModel**: Gestión de estado y lógica de negocio
- **StateFlow**: Flujos reactivos para datos
- **Coroutines**: Programación asíncrona
- **Android TV Compose**: Componentes específicos para TV

## Configuración

### Firebase Setup
1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Habilitar Realtime Database
3. Descargar `google-services.json` y colocarlo en la carpeta `app/`
4. Configurar las reglas de seguridad de la base de datos

### Reglas de Firebase Database
```json
{
  "rules": {
    "gameRooms": {
      "$roomId": {
        ".read": true,
        ".write": true
      }
    }
  }
}
```

## Instalación y Uso

### Requisitos
- Android TV o dispositivo Android con soporte para TV
- Android API 30+ (Android 11+)
- Proyecto Firebase configurado

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
4. Esperar a que los jugadores se conecten desde sus móviles
5. Presionar "🚀 INICIAR JUEGO" cuando esté listo
6. Ver la carrera en tiempo real en la pantalla de la TV

## Flujo de Juego

1. **Lobby**: Los jugadores se conectan usando el código de sala
2. **Preparación**: El host ve la lista de jugadores conectados
3. **Inicio**: El host presiona el botón de iniciar juego
4. **Cuenta Regresiva**: 3, 2, 1, ¡GO!
5. **Carrera**: Los jugadores compiten tocando sus pantallas
6. **Resultados**: Se muestran las posiciones finales
7. **Lobby**: Opción de volver para una nueva carrera

## Motor de Juego

### Características Técnicas
- **Canvas de 60 FPS**: Renderizado suave de la pista y avatares
- **Sistema de coordenadas**: Posicionamiento preciso basado en porcentajes
- **Procesamiento de taps**: Cada tap avanza el avatar 1% de la pista
- **Detección de colisiones**: Sistema para detectar cuando un avatar cruza la meta
- **Estados del juego**: WAITING → COUNTDOWN → RACING → FINISHED

### Configuración Ajustable
- Duración de la carrera: 30 segundos (configurable)
- Incremento por tap: 1% de la pista (configurable)
- Número de carriles: 4 carriles (configurable)
- Tamaño de avatares: 60% del alto del carril (configurable)

## Próximas Fases

- **Fase 3**: Avatares personalizables y skins
- **Fase 4**: Múltiples modos de juego y niveles
- **Fase 5**: Sistema de puntuaciones y estadísticas
- **Fase 6**: Efectos de sonido y música
- **Fase 7**: Modo torneo y ligas

## Desarrollo

### Agregar Nuevas Funcionalidades
La estructura modular permite fácil extensión:

1. **Nuevos tipos de mensajes**: Agregar en los modelos de datos
2. **Nuevas pantallas**: Crear en `ui/screens/`
3. **Lógica de juego**: Extender `GameEngine.kt`
4. **Componentes UI**: Agregar en `ui/components/`

### Estructura de Datos Firebase
```
gameRooms/
├── {roomId}/
│   ├── code: "1234"
│   ├── isActive: true
│   ├── isGameStarted: false
│   ├── gameStartTime: 0
│   └── players/
│       ├── {playerId1}/
│       │   ├── id: "player1"
│       │   ├── name: "Jugador1"
│       │   ├── avatarId: "car_red"
│       │   ├── tapCount: 0
│       │   ├── progress: 0.75
│       │   └── joinedAt: 1642012345678
│       └── {playerId2}/
│           └── ...
```

## Troubleshooting

### Problemas Comunes

1. **No se pueden conectar los móviles**
   - Verificar que Firebase esté configurado correctamente
   - Comprobar las reglas de seguridad de la base de datos
   - Verificar que el código de sala sea correcto

2. **La app no inicia**
   - Verificar que `google-services.json` esté en la carpeta correcta
   - Comprobar que el dispositivo soporte Android TV
   - Verificar permisos de internet

3. **Errores de Firebase**
   - Revisar logs en Logcat
   - Verificar configuración de Firebase
   - Comprobar reglas de seguridad

4. **El juego no inicia**
   - Verificar que haya al menos un jugador conectado
   - Comprobar que el ViewModel esté inicializado correctamente
   - Revisar logs del GameEngine

## Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo LICENSE para más detalles.

## Contribuir

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

---

¡Disfruta creando tu juego multijugador de carreras! 🏎️🎮
