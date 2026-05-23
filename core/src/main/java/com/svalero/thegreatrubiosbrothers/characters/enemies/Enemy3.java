package com.svalero.thegreatrubiosbrothers.characters.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svalero.thegreatrubiosbrothers.manager.R;

public class Enemy3 extends Enemy {

    private Animation<TextureRegion> flyAnimation;

    public Enemy3(Vector2 position) {
        // Le pasamos la primera imagen de vuelo
        super(R.getRegion("enemy3-1"), position);

        // Las abejas pueden volar un pelín más rápido (-1.5f en vez de -1f)
        this.velocity = new Vector2(-com.svalero.thegreatrubiosbrothers.util.Constans.ENEMY_SPEED_FLY, 0);

        // Animación de aleteo (3 frames)
        Array<TextureRegion> frames = new Array<>();
        frames.add(R.getRegion("enemy3-1"));
        frames.add(R.getRegion("enemy3-2"));
        frames.add(R.getRegion("enemy3-3"));
        // El aleteo es un poco más rápido (0.1f)
        flyAnimation = new Animation<>(0.1f, frames);
    }

    @Override
    public void update(float dt) {
        if (squashed) return;

        stateTimer += dt;
        this.currentFrame = flyAnimation.getKeyFrame(stateTimer, true);
    }

    @Override
    public void squash() {
        squashed = true;
        velocity.x = 0;
        // Frame de la abeja muerta
        this.currentFrame = R.getRegion("enemy3-4");
    }

    @Override
    public boolean isFlying() {
        // Si no está chafada, vuela. Si está chafada, la gravedad tira de ella.
        return !squashed;
    }
}
