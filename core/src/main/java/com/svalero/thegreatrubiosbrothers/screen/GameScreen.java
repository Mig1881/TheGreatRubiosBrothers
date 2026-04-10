package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.svalero.thegreatrubiosbrothers.manager.*;
import com.svalero.thegreatrubiosbrothers.model.SaveState;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;

public class GameScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private LevelManager levelManager;
    private LogicManager logicManager;
    private RenderManager renderManager;
    private CameraManager cameraManager;

    private int currentLevel;

    public GameScreen(thegreatrubiosbrothers game) {
        this.game = game;
        this.currentLevel = ConfigurationManager.getStartLevel();
        initLevel(null);
    }

    public GameScreen(thegreatrubiosbrothers game, int levelToLoad, SaveState stateToInject) {
        this.game = game;
        this.currentLevel = levelToLoad;

        ConfigurationManager.setStartLevel(this.currentLevel);

        initLevel(stateToInject);
    }

    private void initLevel(SaveState state) {
        levelManager = new LevelManager(currentLevel);
        logicManager = new LogicManager();
        logicManager.setLevelManager(levelManager);

        if (state != null) {
            logicManager.loadState(state);
        }

        cameraManager = new CameraManager(logicManager);
        cameraManager.setMapWidth(levelManager.getMapPixelWidth());

        renderManager = new RenderManager(logicManager, game.batch, currentLevel);
    }

    @Override
    public void show() {
        ConfigurationManager.playMusic("sounds/04_In-Game 1.mp3");
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game, this));
            return;
        }

        // MUERTE (Game Over normal)
        if (logicManager.isGameOver()) {
            int finalScore = logicManager.player.getScore() * 10;
            game.setScreen(new GameOverScreen(game, finalScore));
            dispose();
            return;
        }

        // FIN DE NIVEL
        if (logicManager.isLevelCompleted()) {

            if (currentLevel >= 4) {
                ConfigurationManager.setStartLevel(1);
                int finalScore = logicManager.player.getScore() * 10;
                game.setScreen(new GameOverScreen(game, finalScore));
            } else {
                SaveState transferData = new SaveState(
                    currentLevel + 1,
                    logicManager.player.getScore(),
                    logicManager.player.getLives(),
                    100f,
                    50f, 150f,
                    logicManager.player.isHasFire(),
                    logicManager.player.isHasBomb(),
                    logicManager.player.isHasLife()
                );
                game.setScreen(new GameScreen(game, currentLevel + 1, transferData));
            }

            dispose();
            return;
        }

        logicManager.update(delta);
        cameraManager.handleCamera();

        OrthographicCamera cam = cameraManager.getCamera();
        logicManager.setViewPort(
            cam.position.x - cam.viewportWidth / 2,
            cam.position.y - cam.viewportHeight / 2,
            cam.viewportWidth,
            cam.viewportHeight
        );

        Gdx.gl.glClearColor(92/255f, 148/255f, 252/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        levelManager.render(cam);
        renderManager.draw(cam);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    public com.svalero.thegreatrubiosbrothers.manager.LogicManager getLogicManager() {
        return logicManager;
    }

    @Override
    public void dispose() {
        levelManager.dispose();
        renderManager.dispose();
    }
}
