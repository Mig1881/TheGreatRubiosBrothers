## **The Great Rubios Brothers** 


 Es un videojuego de plataformas en 2D que rinde homenaje a la era dorada de los 8 bits (C64).
 Vive una aventura llena de peligros, enemigos mecánicos y puentes que se desmoronan, 
 todo bajo una atmósfera retro cuidadosamente recreada.

## 🚀 Tecnologías Utilizadas

* **Lenguaje:** Java 21.
* **Framework:** [LibGDX](https://libgdx.com/) (Gestión de gráficos, audio e inputs).
* **Editor de Mapas:** [Tiled](https://www.mapeditor.org/) (Formato TMX para niveles dinámicos).
* **Gestión de Dependencias:** Gradle.
* **Control de Versiones:** GitHub (Issues, Releases y Wiki).

## 🛠️ Instalación y Ejecución

Sigue estos pasos para ejecutar el juego en tu entorno local:

1. Requisitos Previos
* Tener instalado el **JDK 21** o superior.
* Un IDE recomendado (**IntelliJ IDEA** o VS Code).

2. Clonar el Repositorio
Abre tu terminal y ejecuta:
```bash
git clone https://github.com/Mig1881/thegreatrubiosbrothers.git
cd thegreatrubiosbrothers
```


3. Importar el Proyecto
Abre IntelliJ IDEA.

Selecciona File -> Open y elige la carpeta del proyecto.

Deja que Gradle descargue las dependencias necesarias.

4. Ejecución
Localiza la clase principal dentro del módulo desktop o utiliza la tarea de Gradle:

```bash
./gradlew desktop:run
```

## 🌟 Objetivos Logrados

El desarrollo ha cumplido estrictamente con estos 12 puntos de requerimientos técnicos exigidos:

🎮 Protagonista y Estructura: Personaje principal con control total, narrativa de inicio a fin y 4 niveles diferenciados.

📊 HUD Dinámico: Información en tiempo real de puntuación, vidas, nivel actual y temporizador.

🖥️ Menús Completos: Menú principal, instrucciones y configuración. Rejugabilidad total sin salir de la app.

🎵 Audio y Animación: Animaciones fluidas en todos los caracteres y banda sonora original integrada (Intro y Gameplay).

👾 IA de Enemigos: Sistema de 4 NPCs (enemigos) con comportamientos y patrones de movimiento.

🐙 Gestión en GitHub: Uso profesional de Issues, Releases para versiones estables.

🏆 Hall of Fame: Sistema de persistencia de puntuaciones (Top 10) con registro de nombres de jugadores.

⏸️ Menú In-Game: Menú de pausa funcional para activar/desactivar sonido, seleccion de nivel y volver al menú principal.

🗺️ Expansión de Niveles: Inclusión de niveles extra (Nivel 3 y 4) con mecánicas de dificultad progresiva.

🏗️ Generador de Niveles: Implementación de carga vía TiledMap, permitiendo crear nuevos mundos sin casi tocar el código Java.

💾 Save & Load: Sistema de guardado y carga para continuar la partida exactamente donde se dejó.

⚡ Sistema de Power-Ups: 3 tipos de mejoras (Fuego, Bomba de pantalla y Vidas extra) con persistencia entre niveles.


## 🎨 Detalles Especiales (Nostalgia y Pulido)

LoadScreen (C64): Pantalla de carga asíncrona que emula el ritual del Commodore 64 con el mensaje "PRESS PLAY ON TAPE" y música de introducción.

Agua Animada: Algoritmo dinámico que detecta baldosas de agua y las anima automáticamente mediante AnimatedTiledMapTile.

Donut Blocks (Puentes): Mecánica de puentes que se desmoronan tras 0.4s de contacto, con sistema de restauración automática tras la muerte del jugador para evitar bloqueos de nivel.

Optimización (Culling): Implementación de visión de cámara para la "Bomba Inteligente", afectando solo a los enemigos visibles en pantalla.
