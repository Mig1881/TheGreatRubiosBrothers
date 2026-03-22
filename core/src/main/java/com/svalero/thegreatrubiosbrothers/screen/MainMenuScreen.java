package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;

public class MainMenuScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private Stage stage;
    private BitmapFont font;

    public MainMenuScreen(thegreatrubiosbrothers game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        font = new BitmapFont();

        Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);


        Table table = new Table();
        table.setFillParent(true);
        table.center();


        Label title = new Label("THE GREAT RUBIOS BROTHERS", textStyle);
        title.setFontScale(2f); // Hacemos el título más grande

        Label playInstruction = new Label("Pulsa ENTER para jugar", textStyle);
        Label exitInstruction = new Label("Pulsa ESC para salir", textStyle);

        table.add(title).padBottom(50).row();
        table.add(playInstruction).padBottom(20).row();
        table.add(exitInstruction).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            System.out.println("¡Ir a jugar!");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
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
