package com.svalero.thegreatrubiosbrothers.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svalero.thegreatrubiosbrothers.manager.R;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Character {

    public enum State { IDLE, WALKING, JUMPING, DEAD }
    private State currentState;
    private State previousState;

    // Variables de control
    private float stateTimer;
    private boolean runningRight;
    private int lives;
    private int score;
    public Vector2 velocity;
    private boolean onGround;

    // --- ANIMACIONES ---
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkLeft;
    private TextureRegion idleRight;
    private TextureRegion idleLeft;
    private TextureRegion jumpRight;
    private TextureRegion jumpLeft;

    public Player(TextureRegion image, Vector2 position) {
        super(image, position);
        this.lives = 3;
        this.score = 0;
        this.velocity = new Vector2(0, 0);
        this.onGround = false;

        this.currentState = State.IDLE;
        this.previousState = State.IDLE;
        this.stateTimer = 0;
        this.runningRight = true;

        //ANIMACIÓN DE CAMINAR DERECHA (Frames 0 al 6)
        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i <= 6; i++) {
            frames.add(R.getRegion("Player1-right" + i));
        }
        // 0.1f es la velocidad de la animación (100 milisegundos por frame)
        walkRight = new Animation<>(0.1f, frames);
        frames.clear();

        //ANIMACIÓN DE CAMINAR IZQUIERDA (Frames 0 al 6)
        for(int i = 0; i <= 6; i++) {
            frames.add(R.getRegion("Player1-left" + i));
        }
        walkLeft = new Animation<>(0.1f, frames);

        //ESTADOS DE REPOSO
        idleRight = R.getRegion("Player1-right0");
        idleLeft = R.getRegion("Player1-left0");

        //FRAMES DE SALTO
        jumpRight = R.getRegion("Player1-right5");
        jumpLeft = R.getRegion("Player1-left5");


    }

    // Este método lo llamare desde el LogicManager en cada frame
    public void updateAnimation(float dt) {
        if (currentState == State.DEAD) return;
        //que está haciendo el jugador ahora mismo
        currentState = getState();

        //Controlamos el cronómetro de la animación
        if (currentState == previousState) {
            // Si seguimos haciendo lo mismo que en el frame anterior, el tiempo sigue corriendo
            stateTimer = stateTimer + dt;
        } else {
            // Si hemos cambiado de estado (ej. de estar quietos a empezar a correr), reseteamos a 0
            stateTimer = 0;
        }

        // Guardo el estado actual para compararlo en el siguiente frame
        previousState = currentState;

        //Asignamos la imagen correspondiente al frame actual
        TextureRegion region;
        switch(currentState) {
            case WALKING:
                // Si está caminando, comprobamos hacia dónde mira para elegir la animación
                if (runningRight) {
                    region = walkRight.getKeyFrame(stateTimer, true);
                } else {
                    region = walkLeft.getKeyFrame(stateTimer, true);
                }
                break;

            case JUMPING:
                if (runningRight) {
                    region = jumpRight;
                } else {
                    region = jumpLeft;
                }
                break;

            case IDLE:
            default:
                // Si está quieto, comprobamos hacia dónde fue su último movimiento
                if (runningRight) {
                    region = idleRight;
                } else {
                    region = idleLeft;
                }
                break;
        }

        // currentFrame es la variable de la clase Padre (Character) que el RenderManager pinta
        this.currentFrame = region;
    }

    private State getState() {
        if (!onGround) {
            return State.JUMPING; // Si no pisa el suelo, está saltando/cayendo
        } else if (velocity.x != 0) {
            return State.WALKING; // Si pisa el suelo y se mueve, camina
        } else {
            return State.IDLE;    // Si pisa el suelo y no se mueve, está quieto
        }
    }

    public boolean isDead() {
        return lives <= 0;
    }
    public void die() {
        if (currentState == State.DEAD) return; // Si ya está muerto, no lo vuelvo a matar

        currentState = State.DEAD;
        lives--;

        //El salto de la muerte, Anulo movimiento lateral y le damos un empujón hacia arriba
        velocity.x = 0;
        velocity.y = 12f; // Un salto alto para salir de la pantalla

        // Ponemos el frame de muerte
        this.currentFrame = R.getRegion("Player1-down");
        com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/uuh.wav")).play();
    }
}
