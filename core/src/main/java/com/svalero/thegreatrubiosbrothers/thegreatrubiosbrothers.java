package com.svalero.thegreatrubiosbrothers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.thegreatrubiosbrothers.screen.MainMenuScreen;

public class thegreatrubiosbrothers extends Game {


    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Nota, ya cargare cuando haga la splash screeen los recursos
        // de momento los cargo aqui en memoria, TODO cambiar esto mas adelante
        com.svalero.thegreatrubiosbrothers.manager.R.loadAllResources();
        com.svalero.thegreatrubiosbrothers.manager.R.assets.finishLoading();

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}
