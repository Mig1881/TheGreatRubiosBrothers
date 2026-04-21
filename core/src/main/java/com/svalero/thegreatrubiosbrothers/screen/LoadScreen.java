package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.svalero.thegreatrubiosbrothers.manager.ConfigurationManager;
import com.svalero.thegreatrubiosbrothers.manager.R;
import com.svalero.thegreatrubiosbrothers.thegreatrubiosbrothers;

public class LoadScreen implements Screen {

    private final thegreatrubiosbrothers game;
    private Texture backgroundImage;
    private BitmapFont font;
    private GlyphLayout layout;
    private String message = "LOAD\n\nPRESS PLAY ON TAPE";
    private float minimumWaitTimer = 15.0f;

    public LoadScreen(thegreatrubiosbrothers game) {
        this.game = game;

        backgroundImage = new Texture(Gdx.files.internal("load_screen.png"));
        font = new BitmapFont();
        font.getData().setScale(3.0f);

        layout = new GlyphLayout();

        R.loadAllResources();
    }

    @Override
    public void show() {
        ConfigurationManager.playMusic("sounds/01_Intro.mp3");
    }

    @Override
    public void render(float delta) {
        boolean loaded = R.assets.update();
        minimumWaitTimer -= delta;

        if (loaded && minimumWaitTimer <= 0) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        game.batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        layout.setText(font, message);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = Gdx.graphics.getHeight() * 0.25f;

        font.draw(game.batch, layout, x, y);

        game.batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        backgroundImage.dispose();
        font.dispose();
    }
}
