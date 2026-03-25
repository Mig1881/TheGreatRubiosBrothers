package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class RenderManager {

    private LogicManager logicManager;
    private SpriteBatch batch;

    //HUD
    private OrthographicCamera hudCamera;
    private BitmapFont font;

    public RenderManager(LogicManager logicManager, SpriteBatch batch) {
        this.logicManager = logicManager;
        this.batch = batch;

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constans.SCREEN_WIDTH, Constans.SCREEN_HEIGHT);
        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
    }

    public void draw(OrthographicCamera camera) {

        //EL JUEGO
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Pinto PRIMERO a los enemigos
        for (Enemy enemy : logicManager.enemies) {
            batch.draw(enemy.getCurrentFrame(), enemy.getX(), enemy.getY());
        }

        // Pinto ÚLTIMO al jugador
        batch.draw(logicManager.player.getCurrentFrame(),
            logicManager.player.getX(),
            logicManager.player.getY());

        batch.end();
        //EL HUD / MARCADORES (Cámara estática)
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String scoreText = "RUBIOS\n" + String.format("%06d", logicManager.player.getScore() * 10);
        String livesText = "LIVES\n" + logicManager.player.getLives();
        int currentLevel = ConfigurationManager.getStartLevel();
        String stageText = "STAGE\n1-" + currentLevel;
        String timeText = "TIME\n" + String.format("%03d", (int) Math.max(0, logicManager.getTimeLeft()));

        // Altura a la que vamos a pintar los textos (casi arriba del todo)
        float y = Constans.SCREEN_HEIGHT - 20;

        // Dibujamos repartido por la pantalla (X, Y)
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
