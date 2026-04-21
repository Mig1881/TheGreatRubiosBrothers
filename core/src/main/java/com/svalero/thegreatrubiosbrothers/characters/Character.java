package com.svalero.thegreatrubiosbrothers.characters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Character {

    protected TextureRegion currentFrame;
    protected Vector2 position;
    protected Rectangle rect;

    public Character(TextureRegion image, Vector2 position) {
        this.currentFrame = image;
        this.position = position;
        // El rectángulo se crea automáticamente del mismo tamaño que la imagen
        this.rect = new Rectangle(position.x, position.y, image.getRegionWidth(), image.getRegionHeight());
    }

    public Character(TextureRegion image) {
        this.currentFrame = image;
        this.position = Vector2.Zero; // Si no le pasamos posición, nace en el 0,0
        this.rect = new Rectangle(position.x, position.y, image.getRegionWidth(), image.getRegionHeight());
    }

    //siempre que se mueva rl personaje, su Hitbox debe moverse con él.
    public void move(float x, float y) {
        position.x += x;
        position.y += y;
        rect.setPosition(position);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return currentFrame.getRegionWidth();
    }

    public float getHeight() {
        return currentFrame.getRegionHeight();
    }
}
