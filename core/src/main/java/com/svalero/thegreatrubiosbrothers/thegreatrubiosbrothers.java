package com.svalero.thegreatrubiosbrothers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.thegreatrubiosbrothers.screen.LoadScreen;

public class thegreatrubiosbrothers extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Lanzamos directamente la pantalla de carga
        this.setScreen(new LoadScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
    }
}

