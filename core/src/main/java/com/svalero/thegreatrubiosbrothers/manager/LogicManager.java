package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.characters.Player;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class LogicManager {

    public Player player;
    private LevelManager levelManager; // Referencia al mapa

    public LogicManager() {
        player = new Player(R.getRegion("Player1-right0"), new Vector2(50, 150));
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void update(float dt) {
        handleInput();
        applyPhysics(dt);
        player.updateAnimation(dt);
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

        //¿Colision con el suelo?
        if (levelManager != null) {
            checkVerticalCollisions();
        }

        //Movimiento del jugador en el eje X (Izquierda/Derecha)
        player.move(player.velocity.x, 0);
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
                        // Final¡¡
                        System.out.println("¡NIVEL COMPLETADO! Has tocado el exit.");
                        // TODO: Más adelante aquí cambiaremos a la pantalla de victoria o al nivel 2
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
                    }
                }
            }
        }
    }

}
