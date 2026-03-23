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
    protected Rectangle rect; // La "hitbox" para calcular colisiones físicas

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

    // Este método es crucial: siempre que movamos al personaje, su Hitbox debe moverse con él.
    public void move(float x, float y) {
        position.x += x;
        position.y += y;
        rect.setPosition(position);
    }
}
