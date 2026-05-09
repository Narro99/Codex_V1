# SurfTracker (Android Studio)

Versión enfocada en **probar solo el algoritmo de seguimiento lateral** del surfista usando la cámara frontal.

> En esta fase se elimina la parte de motor + Bluetooth para validar primero el tracking.

## Qué hace ahora

1. Abre cámara frontal con CameraX.
2. Analiza cada frame en tiempo real.
3. Calcula una estimación lateral: `IZQUIERDA`, `CENTRO` o `DERECHA`.
4. Muestra el resultado en pantalla.

## Algoritmo actual (sin ML aún)

El `LateralTracker` actual:
- Usa el plano de luminancia (Y) del frame.
- Calcula un centroide horizontal ponderado (más peso a píxeles oscuros).
- Aplica suavizado exponencial (EMA) para evitar parpadeo.
- Define una zona muerta (`deadZone`) alrededor del centro para estabilizar `CENTRO`.

Esto te sirve para validar pipeline y comportamiento lateral antes de integrar MediaPipe/ML Kit.

## Cómo probarlo en tu móvil Android

1. Abre el proyecto en Android Studio.
2. Activa `Depuración USB` en el móvil.
3. Conecta por USB y ejecuta `Run` sobre el dispositivo físico.
4. Acepta permiso de cámara.
5. Observa el texto `Seguimiento lateral: ...` mientras te mueves a izquierda/derecha frente a la cámara.

## Ajustes rápidos para mejorar estabilidad

En `LateralTracker.kt` puedes ajustar:
- `alpha` (0.15–0.35): más alto = respuesta más rápida, menos suave.
- `deadZone` (0.06–0.12): más alto = más tiempo en `CENTRO`.

## Siguiente paso recomendado

Si esta fase va bien, el siguiente paso es reemplazar este tracker por detección real de persona/pose (MediaPipe Pose o ML Kit) y usar el centroide X del torso/cadera.
