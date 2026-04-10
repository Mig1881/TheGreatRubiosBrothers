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

// --- NUEVO --- Imports para el guardado/carga de partidas
import com.svalero.thegreatrubiosbrothers.manager.SaveManager;
import com.svalero.thegreatrubiosbrothers.model.SaveState;

public class MainMenuScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private final GameScreen pausedGameScreen;

    private Stage stage;
    private BitmapFont font;
    private Table mainTable;
    private Table instructionsTable;

    private Texture backgroundTexture;

    public MainMenuScreen(final thegreatrubiosbrothers game) {
        this.game = game;
        this.pausedGameScreen = null;
    }

    public MainMenuScreen(final thegreatrubiosbrothers game, GameScreen pausedScreen) {
        this.game = game;
        this.pausedGameScreen = pausedScreen;
    }

    @Override
    public void show() {
        if (pausedGameScreen == null) {
            ConfigurationManager.init();
            ConfigurationManager.playMusic("sounds/02_Title Screen.mp3");
        }

        //Cargo la imagen de fondo
        backgroundTexture = new Texture(Gdx.files.internal("ui/menu_background.png"));

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
        font.getData().setScale(2.0f);

        // Estilos para los textos
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        mainTable = new Table();
        mainTable.setFillParent(true);

        instructionsTable = new Table();
        instructionsTable.setFillParent(true);
        instructionsTable.setVisible(false);

        // TÍTULO DINÁMICO
        String titleText;
        if (pausedGameScreen == null) {
            titleText = "THE GREAT\nRUBIOS BROTHERS"; // 🎨 MEJORA VISUAL: Título más épico
        } else {
            titleText = "PAUSED";
        }

        Label titleLabel = new Label(titleText, labelStyle);
        titleLabel.setColor(new Color(1f, 0.4f, 0f, 1f));
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(3f);

        mainTable.add(titleLabel).padBottom(80).row();

        Table buttonTable = new Table();

        if (pausedGameScreen == null) {
            TextButton playButton = createPrettyButton("PLAY");
            //Botón de Cargar Partida (solo aparece si existe el archivo)
            if (SaveManager.hasSave()) {
                TextButton loadButton = createPrettyButton("LOAD GAME");
                loadButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        SaveState state = SaveManager.loadGame();
                        if (state != null) {
                            // Configuramos el nivel correcto
                            ConfigurationManager.setStartLevel(state.level);
                            // Creamos la pantalla
                            GameScreen loadedGame = new GameScreen(game);
                            // Le inyectamos los datos guardados
                            loadedGame.getLogicManager().loadState(state);
                            // Arrancamos el juego
                            game.setScreen(loadedGame);
                            dispose();
                        }
                    }
                });
                buttonTable.add(loadButton).pad(20).fillX().row();
            }
            TextButton instructionsButton = createPrettyButton("INSTRUCTIONS");
            TextButton configButton = createPrettyButton("CONFIGURATION");
            TextButton exitButton = createPrettyButton("EXIT");

            playButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new GameScreen(game));
                    dispose();
                }
            });
            instructionsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    mainTable.setVisible(false);
                    instructionsTable.setVisible(true);
                }
            });
            configButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new ConfigurationScreen(game));
                    dispose();
                }
            });
            exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) { Gdx.app.exit(); }
            });

            buttonTable.add(playButton).pad(20).fillX().row();
            buttonTable.add(instructionsButton).pad(20).fillX().row();
            buttonTable.add(configButton).pad(20).fillX().row();
            buttonTable.add(exitButton).pad(20).fillX().row();

        } else {
            //BOTONES MODO PAUSA
            TextButton resumeButton = createPrettyButton("RESUME GAME");
            //Botón de Guardar Partida en el menú de pausa
            final TextButton saveButton = createPrettyButton("SAVE GAME");
            saveButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Extraemos los datos actuales
                    com.svalero.thegreatrubiosbrothers.manager.LogicManager lm = pausedGameScreen.getLogicManager();
                    SaveState state = new SaveState(
                        ConfigurationManager.getStartLevel(),
                        lm.player.getScore(),
                        lm.player.getLives(),
                        lm.getTimeLeft(),
                        lm.player.getX(),
                        lm.player.getY(),
                        lm.player.isHasFire(),
                        lm.player.isHasBomb(),
                        lm.player.isHasLife()
                    );

                    // Guardamos en el archivo .sav
                    SaveManager.saveGame(state);

                    // Feedback visual para el jugador
                    saveButton.setText("SAVED!");
                }
            });

            TextButton instructionsButton = createPrettyButton("INSTRUCTIONS");
            TextButton quitButton = createPrettyButton("QUIT TO MENU");

            resumeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(pausedGameScreen);
                    dispose();
                }
            });
            instructionsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    mainTable.setVisible(false);
                    instructionsTable.setVisible(true);
                }
            });
            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pausedGameScreen.dispose();
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
            });

            buttonTable.add(resumeButton).pad(20).fillX().row();
            buttonTable.add(saveButton).pad(20).fillX().row();
            buttonTable.add(instructionsButton).pad(20).fillX().row();
            buttonTable.add(quitButton).pad(20).fillX().row();
        }

        // Añadimos la tabla de botones a la principal
        mainTable.add(buttonTable);


        // TABLA DE INSTRUCCIONES

        String text = "CONTROLS\n\n" +
            "LEFT / RIGHT ARROWS : Move\n" +
            "SPACE : Jump\n\n" +
            "Jump on enemies to squash them.\n" +
            "Collect diamonds to get points!";

        Label instrLabel = new Label(text, labelStyle);
        instrLabel.setAlignment(Align.center);
        // Texto de instrucciones de color amarillo claro
        instrLabel.setColor(Color.LIGHT_GRAY);

        TextButton backButton = createPrettyButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                instructionsTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });

        instructionsTable.add(instrLabel).padBottom(60).row();
        instructionsTable.add(backButton).pad(20).row();

        stage.addActor(mainTable);
        stage.addActor(instructionsTable);
    }

    private TextButton createPrettyButton(String text) {
        // Estilo base (letra blanca)
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        // style.overFontColor = Color.YELLOW;

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
        // 🎨 MEJORA VISUAL 4: Dibujamos la imagen de fondo ANTES que el escenario
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Dibujamos el fondo estirado a toda la pantalla
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
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
