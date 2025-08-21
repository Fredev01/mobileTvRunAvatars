# Configuración de Red - Carrera Avatar Mobile

## Problema Resuelto

El error `CLEARTEXT communication to 10.0.2.15 not permitted by network security policy` se produce porque Android, por defecto, no permite conexiones HTTP/WebSocket sin cifrar (cleartext) por razones de seguridad.

## Solución Implementada

### 1. Archivo de Configuración de Seguridad de Red

Se creó `mobile/src/main/res/xml/network_security_config.xml` que permite conexiones cleartext a:

- **10.0.2.15**: IP del emulador Android (host)
- **10.0.2.2**: IP del host desde el emulador
- **192.168.1.100**: IP de red local (para dispositivos físicos)
- **localhost** y **127.0.0.1**: Para desarrollo local

### 2. Configuración en AndroidManifest.xml

Se agregaron las siguientes propiedades a la aplicación:

```xml
android:networkSecurityConfig="@xml/network_security_config"
android:usesCleartextTraffic="true"
```

### 3. URL por Defecto Actualizada

Se cambió la URL por defecto de `ws://192.168.1.100:8080` a `ws://10.0.2.15:8080` para el emulador.

## Configuraciones por Entorno

### Para Emulador Android

```
URL del servidor: ws://10.0.2.15:8080
```

**Nota**: El servidor debe estar corriendo en la aplicación TV Android

### Para Dispositivo Físico en la Misma Red

```
URL del servidor: ws://192.168.1.100:8080
```

**Nota**: Reemplaza con la IP real de tu TV Android donde corre el servidor

### Para Desarrollo Local

```
URL del servidor: ws://localhost:8080
```

**Nota**: Requiere que la app TV Android esté corriendo en el mismo dispositivo

## Consideraciones de Seguridad

⚠️ **Importante**: Esta configuración permite tráfico cleartext solo para desarrollo. Para producción, deberías:

1. Usar WebSocket Secure (WSS) en lugar de WS
2. Configurar certificados SSL/TLS apropiados
3. Restringir las conexiones a dominios específicos
4. Remover `android:usesCleartextTraffic="true"`

## Verificación

Para verificar que la configuración funciona:

1. Compila e instala la aplicación
2. En la pantalla de conexión, verifica que la URL sea `ws://10.0.2.15:8080`
3. Intenta conectarte al servidor
4. No deberías ver el error de cleartext

## Troubleshooting

Si sigues teniendo problemas:

1. **Verifica la IP del servidor**: Asegúrate de que el servidor esté corriendo en la IP correcta
2. **Reinicia la aplicación**: A veces es necesario reiniciar después de cambios de configuración
3. **Verifica el puerto**: Asegúrate de que el puerto 8080 esté abierto y accesible
4. **Logs de red**: Revisa los logs de Android Studio para más detalles del error
