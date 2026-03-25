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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;
import com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager;

public class MainMenuScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private final GameScreen pausedGameScreen; // Guardará la partida si estamos en pausa

    private Stage stage;
    private BitmapFont font;
    private Table mainTable;
    private Table instructionsTable;

    //Constructor normal para el menu incial del juego
    public MainMenuScreen(final thegreatrubiosbrothers game) {
        this.game = game;
        this.pausedGameScreen = null; // No hay partida pausada
    }

    //Constructor en Pausa (pulsar ESC en la partida)
    public MainMenuScreen(final thegreatrubiosbrothers game, GameScreen pausedScreen) {
        this.game = game;
        this.pausedGameScreen = pausedScreen; // Se gurada la partida actual
    }

    @Override
    public void show() {
        // Solo se inicia la música del menú si NO estamos en pausa
        if (pausedGameScreen == null) {
            ConfigurationManager.init();
            ConfigurationManager.playMusic("sounds/02_Title Screen.mp3");
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        mainTable = new Table();
        mainTable.setFillParent(true);

        instructionsTable = new Table();
        instructionsTable.setFillParent(true);
        instructionsTable.setVisible(false);


        // Título dinámico
        String titleText;
        if (pausedGameScreen == null) {
            titleText = "MAIN MENU";
        } else {
            titleText = "PAUSED";
        }

        Label titleLabel = new Label(titleText, labelStyle);
        mainTable.add(titleLabel).padBottom(40).row();

        // Botón de Instrucciones (Común para ambos modos)
        TextButton instructionsButton = new TextButton("INSTRUCTIONS", buttonStyle);
        instructionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.setVisible(false);
                instructionsTable.setVisible(true);
            }
        });

        if (pausedGameScreen == null) {
            // --- BOTONES MODO MENÚ PRINCIPAL ---
            TextButton playButton = new TextButton("PLAY", buttonStyle);
            TextButton configButton = new TextButton("CONFIGURATION", buttonStyle);
            TextButton exitButton = new TextButton("EXIT", buttonStyle);

            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new GameScreen(game));
                    dispose();
                }
            });

            configButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new ConfigurationScreen(game));
                    dispose();
                }
            });

            exitButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.exit();
                }
            });

            mainTable.add(playButton).pad(15).row();
            mainTable.add(instructionsButton).pad(15).row();
            mainTable.add(configButton).pad(15).row();
            mainTable.add(exitButton).pad(15).row();

        } else {
            // --- BOTONES MODO PAUSA ---
            // Fíjate que aquí NO añadimos el botón 'configButton'
            TextButton resumeButton = new TextButton("RESUME", buttonStyle);
            TextButton quitButton = new TextButton("QUIT TO MENU", buttonStyle);

            resumeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(pausedGameScreen); // Volvemos a la partida
                    dispose();
                }
            });

            quitButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pausedGameScreen.dispose(); // Destruimos la partida abandonada
                    game.setScreen(new MainMenuScreen(game)); // Cargamos el menú limpio
                    dispose();
                }
            });

            mainTable.add(resumeButton).pad(15).row();
            mainTable.add(instructionsButton).pad(15).row();
            mainTable.add(quitButton).pad(15).row();
        }

        // =========================================
        // TABLA DE INSTRUCCIONES
        // =========================================
        String text = "CONTROLS\n\n" +
            "LEFT / RIGHT ARROWS : Move\n" +
            "SPACE : Jump\n\n" +
            "Jump on enemies to squash them.\n" +
            "Collect diamonds to get points!";

        Label instrLabel = new Label(text, labelStyle);
        instrLabel.setAlignment(Align.center);

        TextButton backButton = new TextButton("BACK", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                instructionsTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });

        instructionsTable.add(instrLabel).padBottom(40).row();
        instructionsTable.add(backButton).pad(15).row();

        stage.addActor(mainTable);
        stage.addActor(instructionsTable);
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
