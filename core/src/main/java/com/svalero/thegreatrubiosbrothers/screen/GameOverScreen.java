package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;
import com.svalero.thegreatrubiosbrothers.manager.DatabaseManager;
import com.svalero.thegreatrubiosbrothers.model.Score;

import java.util.List;

public class GameOverScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private final int finalScore;

    private Stage stage;
    private BitmapFont font;
    private Table mainTable;

    public GameOverScreen(final thegreatrubiosbrothers game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager.playMusic("sounds/06_High Scores.mp3");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
        font.getData().setScale(1.5f);

        // Se Construye la interfaz por primera vez
        buildUI(false);
    }

    private void buildUI(boolean scoreSaved) {
        stage.clear();

        mainTable = new Table();
        mainTable.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.YELLOW);
        //He hecho una copia directa de la version que salia en el C64
        Label title = new Label("ALL TIME GREATEST", labelStyle);
        title.setFontScale(2.0f);
        mainTable.add(title).padBottom(30).row();

        //tabla de puntuaciones
        List<Score> topScores = DatabaseManager.getTopScores();
        Table scoresTable = new Table();

        int rank = 1;
        for (Score s : topScores) {
            String rankStr = String.format("%02d.", rank);
            String scoreStr = String.format("%06d", s.score);
            String nameStr = s.name;

            scoresTable.add(new Label(rankStr, labelStyle)).padRight(20).align(Align.right);
            scoresTable.add(new Label(scoreStr, labelStyle)).padRight(20).align(Align.left);
            scoresTable.add(new Label(nameStr, labelStyle)).align(Align.left).row();
            rank++;
        }

        // Si la base de datos está vacía, se muestra un mensaje
        if (topScores.isEmpty()) {
            scoresTable.add(new Label("NO SCORES YET!", labelStyle)).row();
        }

        mainTable.add(scoresTable).padBottom(40).row();

        // Zona de Introduccion de nombre
        if (!scoreSaved) {
            Label prompt = new Label("YOUR SCORE: " + finalScore + " - ENTER YOUR NAME:", labelStyle);
            mainTable.add(prompt).padBottom(10).row();

            //Esto es para crear un cuadro de texto (TextField)
            Pixmap cursorColor = new Pixmap(2, (int) font.getLineHeight(), Pixmap.Format.RGBA8888);
            cursorColor.setColor(Color.YELLOW);
            cursorColor.fill();

            TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
            tfStyle.font = font;
            tfStyle.fontColor = Color.WHITE;
            tfStyle.cursor = new TextureRegionDrawable(new TextureRegion(new Texture(cursorColor)));

            final TextField nameInput = new TextField("", tfStyle);
            nameInput.setMaxLength(10); // Límite de letras tipo arcade
            nameInput.setAlignment(Align.center);
            mainTable.add(nameInput).width(250).padBottom(20).row();

            // Automáticamente se pone el foco en el cuadro de texto para escribir del tirón
            stage.setKeyboardFocus(nameInput);

            // Botón de Guardar
            TextButton saveBtn = createPrettyButton("SAVE SCORE");
            saveBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String playerName = nameInput.getText().trim();
                    if (playerName.isEmpty()) {
                        playerName = "AAA"; // El mítico "AAA" por defecto
                    }

                    //Se guarda en SQLLITE
                    DatabaseManager.saveScore(playerName.toUpperCase(), finalScore);

                    //Se recarga la interfaz ocultando el cajón de texto
                    buildUI(true);
                }
            });
            mainTable.add(saveBtn).row();

        } else {
            // Si ya se ha guardado, solo se muestra el botón de volver
            TextButton menuBtn = createPrettyButton("MAIN MENU");
            menuBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
            });
            mainTable.add(menuBtn).row();
        }

        stage.addActor(mainTable);
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
