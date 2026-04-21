package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy;
import com.svalero.thegreatrubiosbrothers.util.Constans;
import com.svalero.thegreatrubiosbrothers.items.PowerUp;
import com.svalero.thegreatrubiosbrothers.items.Fireball;

public class RenderManager {

    private LogicManager logicManager;
    private SpriteBatch batch;

    private OrthographicCamera hudCamera;
    private BitmapFont font;
    private int currentLevel;

    public RenderManager(LogicManager logicManager, SpriteBatch batch, int currentLevel) {
        this.logicManager = logicManager;
        this.batch = batch;
        this.currentLevel = currentLevel;

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constans.SCREEN_WIDTH, Constans.SCREEN_HEIGHT);
        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
    }

    public void draw(OrthographicCamera camera) {

        //EL JUEGO
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Enemy enemy : logicManager.enemies) {
            batch.draw(enemy.getCurrentFrame(), enemy.getX(), enemy.getY());
        }

        for (PowerUp p : logicManager.powerUps) {
            batch.draw(p.getFrame(), p.getPosition().x, p.getPosition().y);
        }

        for (Fireball f : logicManager.fireballs) {
            batch.draw(f.getTexture(), f.getPosition().x, f.getPosition().y);
        }

        batch.draw(logicManager.player.getCurrentFrame(),
            logicManager.player.getX(),
            logicManager.player.getY());

        batch.end();

        //EL HUD / MARCADORES
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String scoreText = "RUBIOS\n" + String.format("%06d", logicManager.player.getScore() * 10);
        String livesText = "LIVES\n" + logicManager.player.getLives();

        String stageText = "STAGE\n1-" + currentLevel;

        String timeText = "TIME\n" + String.format("%03d", (int) Math.max(0, logicManager.getTimeLeft()));

        float y = Constans.SCREEN_HEIGHT - 20;

        font.draw(batch, scoreText, 50, y);
        font.draw(batch, livesText, Constans.SCREEN_WIDTH * 0.40f, y);
        font.draw(batch, stageText, Constans.SCREEN_WIDTH * 0.65f, y);
        font.draw(batch, timeText, Constans.SCREEN_WIDTH * 0.85f, y);

        batch.end();
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}
