package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;
import com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager;

public class ConfigurationScreen implements Screen {

    private final thegreatrubiosbrothers game; // <-- ¡Aquí está la clave!
    private Stage stage;
    private BitmapFont font;

    // EL CONSTRUCTOR QUE ARREGLA EL ERROR:
    public ConfigurationScreen(final thegreatrubiosbrothers game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));

        // Estilos para los botones y textos
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        // 1. TÍTULO
        Label titleLabel = new Label("OPTIONS", labelStyle);

        // 2. BOTÓN DE MÚSICA (Toggle On/Off)
        // Leemos de nuestro ConfigurationManager cómo está la música guardada
        String musicStatus = ConfigurationManager.isMusicOn() ? "MUSIC: ON" : "MUSIC: OFF";
        final TextButton musicButton = new TextButton(musicStatus, buttonStyle);

        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Invertimos el valor guardado
                boolean currentlyOn = ConfigurationManager.isMusicOn();
                ConfigurationManager.setMusicOn(!currentlyOn);
                // Actualizamos el texto del botón
                musicButton.setText(ConfigurationManager.isMusicOn() ? "MUSIC: ON" : "MUSIC: OFF");
            }
        });

        // 3. BOTÓN DE NIVEL (Alterna entre Nivel 1 y Nivel 2)
        String levelStatus = "START LEVEL: " + ConfigurationManager.getStartLevel();
        final TextButton levelButton = new TextButton(levelStatus, buttonStyle);

        levelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentLevel = ConfigurationManager.getStartLevel();
                // Si es el 1 pasamos al 2, si es el 2 volvemos al 1
                int nextLevel = (currentLevel == 1) ? 2 : 1;
                ConfigurationManager.setStartLevel(nextLevel);
                levelButton.setText("START LEVEL: " + nextLevel);
            }
        });

        // 4. BOTÓN DE VOLVER AL MENÚ
        TextButton backButton = new TextButton("BACK", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // 5. MAQUETACIÓN EN LA PANTALLA
        Table table = new Table();
        table.setFillParent(true);

        table.add(titleLabel).padBottom(50).row();
        table.add(musicButton).pad(15).row();
        table.add(levelButton).pad(15).row();
        table.add(backButton).padTop(50).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
