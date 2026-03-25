package com.svalero.thegreatrubiosbrothers.characters.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svalero.thegreatrubiosbrothers.manager.R;

public class Enemy2 extends Enemy {

    private Animation<TextureRegion> walkAnimation;

    public Enemy2(Vector2 position) {
        // Le paso la primera imagen (enemy2-1) al padre
        super(R.getRegion("enemy2-1"), position);

        // Empieza patrullando hacia la izquierda (igual que el 1)
        this.velocity = new Vector2(-1f, 0);

        // Animación de caminar con 3 frames
        Array<TextureRegion> frames = new Array<>();
        frames.add(R.getRegion("enemy2-1"));
        frames.add(R.getRegion("enemy2-2"));
        frames.add(R.getRegion("enemy2-3"));
        walkAnimation = new Animation<>(0.15f, frames);
    }

    @Override
    public void update(float dt) {
        if (squashed) return; // Si está chafado, deja de animarse

        stateTimer += dt;
        this.currentFrame = walkAnimation.getKeyFrame(stateTimer, true);
    }

    @Override
    public void squash() {
        squashed = true;
        velocity.x = 0; // Se para en seco
        // frame de la muerte (boca abajo)
        this.currentFrame = R.getRegion("enemy2-4");
    }
}
