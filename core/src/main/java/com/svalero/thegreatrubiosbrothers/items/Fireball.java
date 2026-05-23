package com.svalero.thegreatrubiosbrothers.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class Fireball {
    private TextureRegion texture;
    private Vector2 position;
    public Vector2 velocity;
    private Rectangle bounds;
    private boolean toDestroy;

    public Fireball(TextureRegion texture, Vector2 startPos, boolean facingRight) {
        this.texture = texture;
        this.position = new Vector2(startPos.x, startPos.y);

        // Sale disparada a gran velocidad hacia donde mire el jugador
        this.velocity = new Vector2(facingRight ? 250f : -250f, 0);
        this.bounds = new Rectangle(position.x, position.y, texture.getRegionWidth(), texture.getRegionHeight());
        this.toDestroy = false;
    }

    public void update(float dt) {
        if (toDestroy) return;

        // Tiene gravedad, así que irá cayendo en picado poco a poco
        //velocity.y -= Constans.GRAVITY * dt;

        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        bounds.setPosition(position);

        // Si se sale del mapa por abajo, desaparece
        if (position.y < -20) {
            toDestroy = true;
        }
    }

    public TextureRegion getTexture() { return texture; }
    public Vector2 getPosition() { return position; }
    public Rectangle getBounds() { return bounds; }
    public boolean isToDestroy() { return toDestroy; }
    public void setToDestroy(boolean destroy) { this.toDestroy = destroy; }
}
