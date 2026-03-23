package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.svalero.thegreatrubiosbrothers.manager.CameraManager;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;
import com.svalero.thegreatrubiosbrothers.manager.LevelManager;
import com.svalero.thegreatrubiosbrothers.manager.LogicManager;
import com.svalero.thegreatrubiosbrothers.manager.RenderManager;

public class GameScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private LevelManager levelManager;
    private LogicManager logicManager;
    private RenderManager renderManager;
    private CameraManager cameraManager;

    public GameScreen(thegreatrubiosbrothers game) {
        this.game = game;
    }

    @Override
    public void show() {
        levelManager = new LevelManager();
        logicManager = new LogicManager();
        logicManager.setLevelManager(levelManager);
        cameraManager = new CameraManager(logicManager);
        // Le pasamos la anchura del mapa a la cámara
        cameraManager.setMapWidth(levelManager.getMapPixelWidth());
        renderManager = new RenderManager(logicManager, game.batch);
    }

    @Override
    public void render(float delta) {
        logicManager.update(delta);

        cameraManager.handleCamera();

        Gdx.gl.glClearColor(92/255f, 148/255f, 252/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        OrthographicCamera cam = cameraManager.getCamera();

        levelManager.render(cam);
        renderManager.draw(cam);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        levelManager.dispose();
    }
}
