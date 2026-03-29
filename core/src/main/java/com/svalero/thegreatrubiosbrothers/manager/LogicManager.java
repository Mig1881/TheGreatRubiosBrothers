package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.characters.Player;
import com.svalero.thegreatrubiosbrothers.util.Constans;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy;
import java.util.List;
import java.util.ArrayList;

public class LogicManager {

    public Player player;
    private LevelManager levelManager;
    public List<Enemy> enemies;
    private float timeLeft;
    private float deathTimer = 0;
    private boolean gameOver = false;
    private boolean levelCompleted = false;

    public LogicManager() {
        player = new Player(R.getRegion("Player1-right0"), new Vector2(50, 150));
        enemies = new ArrayList<>();
        this.timeLeft = 100f;
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        this.enemies = levelManager.loadEnemies();
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public void update(float dt) {
        // Logica de muerte y tiempo
        if (player.getCurrentState() == Player.State.DEAD) {
            deathTimer += dt;
            // Esperamos 3 segundos para que caiga por el barranco y suene el "uuh"
            if (deathTimer > 3.0f) {
                if (player.getLives() > 0) {
                    respawn(); // Quedan vidas -> Volvemos a empezar
                } else {
                    gameOver = true; // 0 vidas -> Se acabó el juego
                }
            }
        } else {
            handleInput(); // Si está vivo, leemos el teclado

            // Y restamos el tiempo
            timeLeft -= dt;
            if (timeLeft < 0) {
                timeLeft = 0;
                player.die(); // ¡muere si se acaba el tiempo!
            }
            // Si la coordenada Y del jugador baja de 0 (es decir, se sale por debajo de la pantalla)
            if (player.getY() < -10) {
                player.die();
            }
        }

        applyPhysics(dt); // La gravedad sigue aplicando para que el cadáver caiga

        if (player.getCurrentState() != Player.State.DEAD) {
            player.updateAnimation(dt);
            checkPlayerEnemyCollisions(); // Compruebo los choques
            checkCollectibles();//Compruebo si nos estamos comiendo un diammante
        }

        for (Enemy enemy : enemies) {
            enemy.update(dt);
            applyEnemyPhysics(enemy, dt);
        }
    }

    private void handleInput() {
        // Reseteo la velocidad horizontal en cada frame para que se pare si soltamos la tecla
        player.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.velocity.x = Constans.PLAYER_SPEED;
            player.setRunningRight(true); // Gira a la derecha
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.velocity.x = -Constans.PLAYER_SPEED;
            //se mueve a tres pixeles en el eje x en cada tick de la logica, a 60 frames por segundo son 180 pixeles por segundo
            player.setRunningRight(false); // Gira a la izquierda
        }

        // Salto básico, solo se salta si esta en el suelo.
        //inicialmete la velocidad es 12, pero la grabvedad le empezara a restar hasta que llege a 0
        if (player.isOnGround() && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP))) {
            player.velocity.y = Constans.JUMPING_SPEED;
        }
    }

    private void applyPhysics(float dt) {
        // Aplico la gravedad a la velocidad vertical, aqui si que entra el dt
        //Resto 20 * una pequeña fraccion de segundohace que la caida se acelere frame aframe, simulando aceleracion
        player.velocity.y -= Constans.GRAVITY * dt;

        //Movimiento jugador SOLO en el eje Y (Arriba/Abajo)
        player.move(0, player.velocity.y);

        //Asumo que está en el aire (así si te caes por un barranco caminando, no puedes saltar en el aire)
        player.setOnGround(false);

        //Si no esta muerto ¿Colision con el suelo?
        if (player.getCurrentState() != Player.State.DEAD) {
            if (levelManager != null) {
                checkVerticalCollisions();
            }
        }

        //Movimiento del jugador en el eje X (Izquierda/Derecha)
        player.move(player.velocity.x, 0);
        //Si no esta muerto, compruebo si choca contra los muros
        if (player.getCurrentState() != Player.State.DEAD) {
            if (levelManager != null) {
                checkHorizontalCollisions();

                // Un pequeño extra de seguridad: no dejar que se salga por la izquierda del mapa
                if (player.getX() < 0) {
                    player.getPosition().x = 0;
                    player.getRect().setX(0);
                    player.velocity.x = 0;
                }
            }
        }
    }

    private void checkVerticalCollisions() {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();

        // Columnas (X) que ocupa el jugador (desde el lado izquierdo hasta el derecho)
        int startX = (int) (player.getX() / Constans.TILE_WIDTH);
        int endX = (int) ((player.getX() + player.getWidth() - 1) / Constans.TILE_WIDTH);

        //  El jugador está CAYENDO
        if (player.velocity.y < 0) {
            // Miramos la fila (Y) que está tocando los PIES del jugador
            int bottomY = (int) (player.getY() / Constans.TILE_HEIGHT);

            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, bottomY);

                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    // Chocamos con el suelo
                    player.getPosition().y = (bottomY + 1) * Constans.TILE_HEIGHT;
                    player.getRect().setY(player.getPosition().y);
                    player.velocity.y = 0;
                    player.setOnGround(true); // Podemos volver a saltar
                    break;
                }
            }
        }
        // El jugador está SALTANDO (Hacia arriba)
        else if (player.velocity.y > 0) {
            // Miramos la fila (Y) que está tocando la CABEZA del jugador
            int topY = (int) ((player.getY() + player.getHeight() - 1) / Constans.TILE_HEIGHT);

            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, topY);
                //Compruebo si hay algun bloque..
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    player.getPosition().y = topY * Constans.TILE_HEIGHT - player.getHeight();
                    player.getRect().setY(player.getPosition().y);
                    // Al poner la velocidad a 0, la gravedad (que siempre resta) lo hará caer en el siguiente frame
                    player.velocity.y = 0;
                    break;
                }
            }
        }
    }

    private void checkHorizontalCollisions() {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();

        // Calculo desde dónde hasta dónde mide David en vertical para comprobar toda su altura
        int startY = (int) (player.getY() / Constans.TILE_HEIGHT);
        int endY = (int) ((player.getY() + player.getHeight() - 1) / Constans.TILE_HEIGHT);

        // hacia la derecha
        if (player.velocity.x > 0) {
            // Calculo en qué columna de Tiled está el borde DERECHO de nuestro jugador
            int rightX = (int) ((player.getX() + player.getWidth() - 1) / Constans.TILE_WIDTH);

            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(rightX, y);
                if (cell != null) {
                    if (cell.getTile().getProperties().containsKey("ground")) {
                        // ¡Muro detectado! Lo pego al lado izquierdo del bloque
                        player.getPosition().x = rightX * Constans.TILE_WIDTH - player.getWidth();
                        player.getRect().setX(player.getPosition().x);
                        player.velocity.x = 0; // Frenamos en seco
                        break;
                    } else if (cell.getTile().getProperties().containsKey("exit")) {
                        System.out.println("¡NIVEL COMPLETADO! Has tocado el exit.");
                        levelCompleted = true;

                    }
                }
            }
        }
        // hacia la izquierda
        else if (player.velocity.x < 0) {
            // Calculamos en qué columna de Tiled está el borde IZQUIERDO de nuestro jugador
            int leftX = (int) (player.getX() / Constans.TILE_WIDTH);

            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(leftX, y);
                if (cell != null) {
                    if (cell.getTile().getProperties().containsKey("ground")) {
                        // ¡Muro detectado! Lo pegamos al lado derecho del bloque
                        player.getPosition().x = (leftX + 1) * Constans.TILE_WIDTH;
                        player.getRect().setX(player.getPosition().x);
                        player.velocity.x = 0;
                        break;
                    } else if (cell.getTile().getProperties().containsKey("exit")) {
                        System.out.println("¡NIVEL COMPLETADO! Has tocado el exit.");
                        levelCompleted = true;
                    }
                }
            }
        }
    }

    // --- FÍSICAS DE LOS ENEMIGOS ---

    private void applyEnemyPhysics(Enemy enemy, float dt) {
        // Solo aplicamos la gravedad si el enemigo NO está volando (La abeja no se cae)
        if (!enemy.isFlying()) {
            enemy.velocity.y -= Constans.GRAVITY * dt;
        }

        enemy.move(0, enemy.velocity.y);

        if (levelManager != null) {
            checkEnemyVerticalCollisions(enemy);
        }

        //Movimiento lateral (patrulla)
        enemy.move(enemy.velocity.x, 0);

        if (levelManager != null) {
            if (!enemy.isFlying()) {
                checkEnemyHorizontalCollisions(enemy);
            }
        }
    }

    private void checkEnemyVerticalCollisions(Enemy enemy) {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int startX = (int) (enemy.getX() / Constans.TILE_WIDTH);
        int endX = (int) ((enemy.getX() + enemy.getWidth() - 1) / Constans.TILE_WIDTH);

        // Los enemigos solo caen, no saltan, solo comprruebo hacia abajo
        if (enemy.velocity.y < 0) {
            int bottomY = (int) (enemy.getY() / Constans.TILE_HEIGHT);

            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, bottomY);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    enemy.getPosition().y = (bottomY + 1) * Constans.TILE_HEIGHT;
                    enemy.getRect().setY(enemy.getPosition().y);
                    enemy.velocity.y = 0; // Toca el suelo y deja de caer
                    break;
                }
            }
        }
    }

    private void checkEnemyHorizontalCollisions(Enemy enemy) {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int startY = (int) (enemy.getY() / Constans.TILE_HEIGHT);
        int endY = (int) ((enemy.getY() + enemy.getHeight() - 1) / Constans.TILE_HEIGHT);

        if (enemy.velocity.x > 0) { // Caminando hacia la derecha
            int rightX = (int) ((enemy.getX() + enemy.getWidth() - 1) / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(rightX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    enemy.getPosition().x = rightX * Constans.TILE_WIDTH - enemy.getWidth();
                    enemy.getRect().setX(enemy.getPosition().x);

                    //Media vuelta, se invierte la velocidad para que camine a la izquierda
                    enemy.velocity.x = -enemy.velocity.x;
                    break;
                }
            }
        } else if (enemy.velocity.x < 0) { // Caminando hacia la izquierda
            int leftX = (int) (enemy.getX() / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(leftX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    enemy.getPosition().x = (leftX + 1) * Constans.TILE_WIDTH;
                    enemy.getRect().setX(enemy.getPosition().x);

                    // Se da la media vuelta, se invierte la velocidad para que camine a la derecha
                    enemy.velocity.x = -enemy.velocity.x;
                    break;
                }
            }
        }
    }

    private void checkPlayerEnemyCollisions() {
        if (player.getCurrentState() == Player.State.DEAD) return; // Los fantasmas no chocan

        for (Enemy enemy : enemies) {
            if (enemy.isSquashed()) continue; // No interactua con cadáveres

            // Si los dos rectángulos se cruzan... ¡Contacto!
            if (player.getRect().overlaps(enemy.getRect())) {

                // Si el jugador está cayendo Y su borde inferior está más alto que el centro del enemigo
                if (player.velocity.y < 0 && player.getY() > enemy.getY() + enemy.getHeight() / 2f) {
                    //Lo pisa
                    enemy.squash();

                    player.setScore(player.getScore() + 1); // Suma 1 punto (10 en el HUD)

                    com.badlogic.gdx.audio.Sound hitSound = R.assets.get("sounds/hit.wav", com.badlogic.gdx.audio.Sound.class);
                    if (hitSound != null) {
                        hitSound.play();
                    }

                    // Sonido original de la puntuación
                    com.badlogic.gdx.audio.Sound bonusSound = R.assets.get("sounds/bonus_score.wav", com.badlogic.gdx.audio.Sound.class);
                    if (bonusSound != null) {
                        bonusSound.play();
                    }

                    // Pequeño rebote para el jugador
                    player.velocity.y = Constans.JUMPING_SPEED * 0.8f;
                } else {
                    //Me pilla
                    player.die();
                }
            }
        }
    }

    private void checkCollectibles() {
        if (player.getCurrentState() == Player.State.DEAD) return;

        TiledMapTileLayer layer = levelManager.getCollisionLayer();

        // Calculo la caja que ocupa el jugador
        int startX = (int) (player.getX() / Constans.TILE_WIDTH);
        int endX = (int) ((player.getX() + player.getWidth() - 1) / Constans.TILE_WIDTH);
        int startY = (int) (player.getY() / Constans.TILE_HEIGHT);
        int endY = (int) ((player.getY() + player.getHeight() - 1) / Constans.TILE_HEIGHT);

        // Reviso todas las celdas que el jugador está tocando con su cuerpo
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                // Si la celda existe y tiene "diamond"...
                if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("diamond")) {

                    // Sumo el punto a Player
                    player.setScore(player.getScore() + 1);
                    System.out.println("¡Diamante recogido! Puntuación total: " + player.getScore());
                    com.badlogic.gdx.audio.Sound diamondSound = R.assets.get("sounds/score_simple.wav", com.badlogic.gdx.audio.Sound.class);
                    if (diamondSound != null) {
                        diamondSound.play();
                    }

                    //Borro el diamante del mapa dejándolo nulo
                    layer.setCell(x, y, null);
                }
            }
        }
    }

    private void respawn() {
        // Restauro al player
        player.setCurrentState(Player.State.IDLE);
        player.velocity.set(0, 0);
        player.getPosition().set(50, 150); // ⚠️ Pon aquí tus coordenadas de inicio
        player.getRect().setPosition(50, 150);

        // Restauro el tiempo y el contador de muerte
        timeLeft = 100f;
        deathTimer = 0;

        // Recargoa los enemigos para que vuelvan a sus posiciones originales
        if (levelManager != null) {
            this.enemies = levelManager.loadEnemies();
        }
    }

    //Inyecta los datos de la partida guardada
    public void loadState(com.svalero.thegreatrubiosbrothers.model.SaveState state) {
        this.timeLeft = state.timeLeft;
        this.player.setScore(state.score);
        this.player.setLives(state.lives);

        // Coloca al jugador exactamente donde se guardó
        this.player.getPosition().set(state.playerX, state.playerY);
        this.player.getRect().setPosition(state.playerX, state.playerY);

        System.out.println("¡Datos de partida inyectados en el LogicManager");
    }

}
