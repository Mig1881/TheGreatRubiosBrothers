package com.svalero.thegreatrubiosbrothers.characters.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svalero.thegreatrubiosbrothers.manager.R;

public class Enemy1 extends Enemy {

    private Animation<TextureRegion> walkAnimation;

    public Enemy1(Vector2 position) {
        // Le pasamos la imagen inicial al padre
        super(R.getRegion("enemy1-0"), position);

        // Empieza patrullando hacia la izquierda
        this.velocity = new Vector2(-1f, 0);

        Array<TextureRegion> frames = new Array<>();
        frames.add(R.getRegion("enemy1-0"));
        frames.add(R.getRegion("enemy1-1"));
        walkAnimation = new Animation<>(0.2f, frames);
    }

    @Override
    public void update(float dt) {
        if (squashed) return; // Si está chafado, no hace nada más

        stateTimer += dt;
        this.currentFrame = walkAnimation.getKeyFrame(stateTimer, true);
    }
    @Override
    public void squash() {
        squashed = true;
        velocity.x = 0; // Se para en seco
        this.currentFrame = com.svalero.thegreatrubiosbrothers.manager.R.getRegion("enemy1-2");
    }
}
