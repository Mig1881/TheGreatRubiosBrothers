package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;
import com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager;

public class ConfigurationScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private Stage stage;
    private BitmapFont font;
    private Texture backgroundTexture; // 🎨 Fondo de pantalla

    public ConfigurationScreen(final thegreatrubiosbrothers game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargo la misma imagen de fondo que en el Main Menu
        backgroundTexture = new Texture(Gdx.files.internal("ui/menu_background.png"));

        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));

        font.getData().setScale(2.0f);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        Label titleLabel = new Label("OPTIONS", labelStyle);
        titleLabel.setColor(new Color(1f, 0.4f, 0f, 1f)); // Color Naranja retro
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(3.0f); // Título aún más gigante

        String musicStatus;
        if (ConfigurationManager.isMusicOn()) {
            musicStatus = "MUSIC: ON";
        } else {
            musicStatus = "MUSIC: OFF";
        }

        final TextButton musicButton = createPrettyButton(musicStatus);

        musicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean currentlyOn = ConfigurationManager.isMusicOn();
                ConfigurationManager.setMusicOn(!currentlyOn);

                if (ConfigurationManager.isMusicOn()) {
                    musicButton.setText("MUSIC: ON");
                } else {
                    musicButton.setText("MUSIC: OFF");
                }
            }
        });

        String levelStatus = "START LEVEL: " + ConfigurationManager.getStartLevel();
        final TextButton levelButton = createPrettyButton(levelStatus);

        levelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int currentLevel = ConfigurationManager.getStartLevel();
                int nextLevel;

                if (currentLevel == 1) {
                    nextLevel = 2;
                } else {
                    nextLevel = 1;
                }

                ConfigurationManager.setStartLevel(nextLevel);
                levelButton.setText("START LEVEL: " + nextLevel);
            }
        });

        //BOTÓN DE VOLVER AL MENÚ
        TextButton backButton = createPrettyButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        //MAQUETACIÓN EN LA PANTALLA
        Table table = new Table();
        table.setFillParent(true);

        table.add(titleLabel).padBottom(80).row();
        table.add(musicButton).pad(20).row();
        table.add(levelButton).pad(20).row();
        table.add(backButton).padTop(60).padBottom(20).row();

        stage.addActor(table);
    }

    private TextButton createPrettyButton(String text) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        final TextButton button = new TextButton(text, style);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.getLabel().setColor(Color.YELLOW);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.getLabel().setColor(Color.WHITE);
            }
        });

        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
