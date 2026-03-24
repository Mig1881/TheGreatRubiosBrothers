package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy;

public class RenderManager {

    private LogicManager logicManager;
    private SpriteBatch batch;

    public RenderManager(LogicManager logicManager, SpriteBatch batch) {
        this.logicManager = logicManager;
        this.batch = batch;
    }


    public void draw(OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //Pinto PRIMERO a los enemigos
        for (Enemy enemy : logicManager.enemies) {
            batch.draw(enemy.getCurrentFrame(), enemy.getX(), enemy.getY());
        }
        //Pinto ÚLTIMO al jugador
        batch.draw(logicManager.player.getCurrentFrame(),
            logicManager.player.getX(),
            logicManager.player.getY());

        batch.end();
    }
}
