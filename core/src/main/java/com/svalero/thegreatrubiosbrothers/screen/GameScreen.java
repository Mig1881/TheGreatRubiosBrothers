package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.svalero.thegreatrubiosbrothers.manager.*;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;

public class GameScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private LevelManager levelManager;
    private LogicManager logicManager;
    private RenderManager renderManager;
    private CameraManager cameraManager;

    public GameScreen(thegreatrubiosbrothers game) {
        this.game = game;
        levelManager = new LevelManager();
        logicManager = new LogicManager();
        logicManager.setLevelManager(levelManager);
        cameraManager = new CameraManager(logicManager);
        cameraManager.setMapWidth(levelManager.getMapPixelWidth());
        renderManager = new RenderManager(logicManager, game.batch);
    }

    @Override
    public void show() {
        ConfigurationManager.playMusic("sounds/04_In-Game 1.mp3");
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Le pasamos 'this' para que sepa que hay una partida activa
            game.setScreen(new MainMenuScreen(game, this));
            return;
        }
        if (logicManager.isGameOver()) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }
        if (logicManager.isLevelCompleted()) {
            // ¿en qué nivel estamos?
            int currentLevel = com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager.getStartLevel();

            if (currentLevel == 1) {
                // Si estamos en el 1, cambiamos la config al 2 y recargamos la pantalla de juego
                com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager.setStartLevel(2);
                game.setScreen(new GameScreen(game));
            } else {
                // Si nos pasamos el 2 (o el último que haya), reseteamos al nivel 1 para la próxima...
                com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager.setStartLevel(1);
                // ... y mandamos al jugador al menú principal como campeón.
                game.setScreen(new MainMenuScreen(game));
            }

            dispose();
            return;
        }
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
        renderManager.dispose();
    }
}
