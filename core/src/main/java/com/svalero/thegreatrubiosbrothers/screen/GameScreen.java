package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;
import com.svalero.thegreatrubiosbrothers.manager.LevelManager;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class GameScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private OrthographicCamera camera;
    private LevelManager levelManager;

    public GameScreen(thegreatrubiosbrothers game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constans.SCREEN_WIDTH / 3f, Constans.SCREEN_HEIGHT / 3f);
        camera.update();

        levelManager = new LevelManager();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(92/255f, 148/255f, 252/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        levelManager.render(camera);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 3f, height / 3f);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        levelManager.dispose();
    }
}
