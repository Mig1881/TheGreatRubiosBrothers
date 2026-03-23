package com.svalero.thegreatrubiosbrothers.characters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Character {

    private int lives;
    private int score;
    public Vector2 velocity;


    public Player(TextureRegion image, Vector2 position) {
        super(image, position);
        this.lives = 3;
        this.score = 0;
        this.velocity = new Vector2(0, 0); // Empieza quieto
    }

    public boolean isDead() {
        return lives <= 0;
    }
}
