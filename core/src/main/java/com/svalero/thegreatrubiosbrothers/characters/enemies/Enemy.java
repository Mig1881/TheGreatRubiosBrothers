package com.svalero.thegreatrubiosbrothers.characters.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.characters.Character;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Enemy extends Character {

    public Vector2 velocity;
    protected float stateTimer;
    protected boolean squashed;

    public Enemy(TextureRegion image, Vector2 position) {
        super(image, position);
        this.velocity = new Vector2(0, 0);
        this.stateTimer = 0;
    }

    // Método abstracto: Obligo a que cada hijo defina su propio comportamiento y animación
    public abstract void update(float dt);

    public boolean isSquashed() {
        return squashed;
    }

    public abstract void squash();

}
