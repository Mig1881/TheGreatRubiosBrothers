package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.characters.Player;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class LogicManager {

    public Player player;

    public LogicManager() {
        player = new Player(R.getRegion("david_idle_left"), new Vector2(50, 150));
    }

    public void update(float dt) {
        handleInput();
        applyPhysics(dt);
    }

    private void handleInput() {
        // Reseteo la velocidad horizontal en cada frame para que se pare si soltamos la tecla
        player.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.velocity.x = Constans.PLAYER_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.velocity.x = -Constans.PLAYER_SPEED;
            //se mueve a tres pixeles en el eje x en cada tick de la logica, a 60 frames por segundo son 180 pixeles por segundo
        }

        // Salto básico.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.velocity.y = Constans.JUMPING_SPEED;
            //inicialmete la velocidad es 12, pero la grabvedad le empezara a restar hasta que llege a 0
        }
    }

    private void applyPhysics(float dt) {
        // Aplico la gravedad a la velocidad vertical, aqui si que entra el dt
        //Resto 20 * una pequeña fraccion de segundohace que la caida se acelere frame aframe, simulando aceleracion
        player.velocity.y -= Constans.GRAVITY * dt;

        // Movemos al jugador usando el método move() que creamos en Character.java
        player.move(player.velocity.x, player.velocity.y);

        // --- TRUCO TEMPORAL ---
        // Como aún no hay colisiones de Tiled, le pongo un "suelo invisible" en el 32 para que David no caiga
        if (player.getY() < 32) {
            player.getPosition().y = 32;
            player.getRect().setY(32);
            player.velocity.y = 0; // Detenemos la caída
        }
    }
}
