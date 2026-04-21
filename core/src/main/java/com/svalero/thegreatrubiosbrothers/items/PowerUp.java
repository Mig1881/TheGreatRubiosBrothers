package com.svalero.thegreatrubiosbrothers.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class PowerUp {

    public enum Type { FIRE, BOMB, LIFE }

    private TextureRegion texture; // Para los estáticos (bomba, piruleta)
    private Animation<TextureRegion> animation; // Para la rueda de fuego
    private float stateTimer;

    private Vector2 position;
    public Vector2 velocity;
    private Rectangle bounds;
    private Type type;

    // Para saber si ya ha terminado de salir
    private boolean spawned;
    // Hasta dónde tiene que subir
    private float spawnTargetY;
    // Si se cae por un barranco o lo recogemos
    private boolean toDestroy;
    private boolean onGround;
    private float moveSpeed = 80f;

    //Constructor para Power-Ups ESTÁTICOS (Bomba, Vida)
    public PowerUp(TextureRegion texture, Vector2 startPos, Type type) {
        this.texture = texture;
        this.animation = null;
        init(startPos, type, texture.getRegionWidth(), texture.getRegionHeight());
    }

    //Constructor para Power-Ups ANIMADOS (Rueda de Fuego)
    public PowerUp(Animation<TextureRegion> animation, Vector2 startPos, Type type) {
        this.texture = null;
        this.animation = animation;
        init(startPos, type, animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
    }

    // Configuración común para ambos
    private void init(Vector2 startPos, Type type, float width, float height) {
        this.position = new Vector2(startPos.x, startPos.y);
        this.velocity = new Vector2(0, 30f); // Sube despacio
        this.type = type;
        this.spawnTargetY = startPos.y + Constans.TILE_HEIGHT;
        this.spawned = false;
        this.toDestroy = false;
        this.onGround = false; // --- NUEVO
        this.stateTimer = 0;
        //La caja de colisiones se adapta asi a cualquier tamaño
        this.bounds = new Rectangle(position.x, position.y, width, height);
    }

    public void update(float dt) {
        if (toDestroy) return;
        stateTimer += dt;

        if (!spawned) {
            position.y += velocity.y * dt;
            if (position.y >= spawnTargetY) {
                position.y = spawnTargetY;
                spawned = true;
                velocity.set(moveSpeed, 0);
            }
        } else {
            if (!onGround) {
                // Cancelamos velocidad X para que caiga recto (a plomo)
                // Y aplicamos una gravedad MUCHO más fuerte (x6 o x8)
                velocity.y -= Constans.GRAVITY * 8 * dt;
                position.y += velocity.y * dt;
            } else {
                // En el suelo se mueve a la velocidad horizontal doblada
                position.x += velocity.x * dt;
                velocity.y -= Constans.GRAVITY * dt;
                position.y += velocity.y * dt;
            }

            onGround = false;
            if (position.y < -20) toDestroy = true;
        }
        bounds.setPosition(position);
    }

    // Devuelve el frame de la animación o la foto estática según corresponda
    public TextureRegion getFrame() {
        if (animation != null) {
            return animation.getKeyFrame(stateTimer, true);
        }
        return texture;
    }

    public Vector2 getPosition() { return position; }
    public Rectangle getBounds() { return bounds; }
    public Type getType() { return type; }
    public boolean isToDestroy() { return toDestroy; }
    public void setToDestroy(boolean destroy) { this.toDestroy = destroy; }
    public boolean isSpawned() { return spawned; }

    public void landOnGround(float groundY) {
        position.y = groundY;
        velocity.y = 0;
        onGround = true;
    }

    public void reverseVelocity() {
        velocity.x = -velocity.x; // Por si choca con una pared
        moveSpeed = -moveSpeed;
    }
}
