package com.svalero.thegreatrubiosbrothers.characters.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svalero.thegreatrubiosbrothers.manager.R;

public class Enemy4 extends Enemy {

    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;

    public Enemy4(Vector2 position) {
        // La imagen por defecto al nacer
        super(R.getRegion("enemy4-0"), position);

        // Empieza patrullando hacia la izquierda a velocidad normal
        this.velocity = new Vector2(-1f, 0);

        // --- ANIMACIÓN ANDAR IZQUIERDA ---
        Array<TextureRegion> framesLeft = new Array<>();
        framesLeft.add(R.getRegion("enemy4-0"));
        framesLeft.add(R.getRegion("enemy4-1"));
        framesLeft.add(R.getRegion("enemy4-2"));
        walkLeftAnimation = new Animation<>(0.15f, framesLeft);

        // --- ANIMACIÓN ANDAR DERECHA ---
        Array<TextureRegion> framesRight = new Array<>();
        framesRight.add(R.getRegion("enemy4-5"));
        framesRight.add(R.getRegion("enemy4-6"));
        framesRight.add(R.getRegion("enemy4-7"));
        walkRightAnimation = new Animation<>(0.15f, framesRight);
    }

    @Override
    public void update(float dt) {
        if (squashed) return; // Si está muerto, ni se mueve ni se anima

        stateTimer += dt;

        // Selecionamosqué animación reproducir según hacia dónde camine
        if (velocity.x < 0) {
            this.currentFrame = walkLeftAnimation.getKeyFrame(stateTimer, true);
        } else {
            this.currentFrame = walkRightAnimation.getKeyFrame(stateTimer, true);
        }
    }

    @Override
    public void squash() {
        squashed = true;
        velocity.x = 0; // Se para en seco

        // Selecciono el fotograma de "chafado" dependiendo de hacia dónde miraba
        if (this.currentFrame == walkLeftAnimation.getKeyFrame(stateTimer, true)) {
            this.currentFrame = R.getRegion("enemy4-3");
        } else {
            this.currentFrame = R.getRegion("enemy4-8");
        }
    }
}
