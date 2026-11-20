# DOPOSoccer — Versión uno (Presentación + Dominio)

Head Soccer para computador, dos jugadores en el mismo teclado. Proyecto final de DOPO 2026-2.

## Estructura

| Carpeta | Contenido |
|---|---|
| `src/dominio` | Reglas del juego (fachada `DOPOSoccer`, partido, jugadores, poderes, efectos, cuentas) |
| `src/presentacion` | Interfaz en Swing (`DOPOSoccerGUI` es la clase principal) |
| `test/dominio` | Pruebas de unidad con JUnit 5 |
| `lib` | Librerías de JUnit 5 (copiadas de BlueJ) |
| `recursos` | Imagen de fondo del menú (`fondo.jpg`); si no existe, el menú usa un degradado |

## Cómo abrirlo en VS Code

1. Instala la extensión **Extension Pack for Java** de Microsoft.
2. Abre la carpeta `DOPOSoccer` (Archivo → Abrir carpeta).
3. Para jugar: abre `src/presentacion/DOPOSoccerGUI.java` y presiona **Run**, o usa la configuración "Jugar DOPOSoccer" (F5).
4. Para las pruebas: pestaña **Testing** (ícono del matraz) → ejecutar todas.

`.vscode/settings.json` usa el JDK 21 que trae BlueJ (`C:\Program Files\BlueJ\jdk`). Si tienes otro JDK, cambia esa ruta.

## Sin VS Code

- `ejecutar.bat`: compila y abre el juego.
- `probar.bat`: compila y corre todas las pruebas de unidad.

## Controles

| Acción | Jugador 1 | Jugador 2 |
|---|---|---|
| Moverse | A / D | ← / → |
| Saltar | W | ↑ |
| Patear | S | ↓ |
| Activar poder | Q | Enter |

P pausa o reanuda el partido; Esc lo termina (pide confirmación).

## Alcance de esta versión

- Flujo: inicio con 2 ranuras → nombres (Enter para continuar) → pantalla con la imagen de fondo y cuatro casillas:
  **Tienda** (mejoras de estadísticas), **Modo de juego** (jugador vs jugador o máquina con su perfil, tipo de partido
  y resolución), **Personaje** (país de cada jugador) y **Jugar**.
- Partido por tiempo (con muerte súbita) o por goles, en 3 resoluciones.
- Vida, daño de patada, aturdimiento, barra de poder, aura y los 4 tiros de poder con sus efectos.
- Jugador vs máquina con 4 perfiles (adelanto de la versión dos).
- Guardar y abrir partidas, y guardar el progreso en disco, quedan para la versión dos: por ahora las ranuras
  viven mientras el juego esté abierto.
- Los errores inesperados se registran en `errores.log`.
