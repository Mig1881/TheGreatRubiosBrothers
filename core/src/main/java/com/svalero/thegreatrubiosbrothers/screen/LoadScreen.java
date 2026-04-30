package com.svalero.thegreatrubiosbrothers.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    private ShapeRenderer shapeRenderer;
    private float timeElapsed = 0f;

    //Paleta de colores del Commodore 64 ---
    private Color[] c64Colors = {
        new Color(0.53f, 0.0f, 0.0f, 1f),   // Rojo C64
        new Color(1.0f, 0.46f, 0.46f, 1f),  // Rojo Claro C64
        new Color(0.86f, 0.53f, 0.33f, 1f), // Naranja C64
        new Color(0.93f, 0.93f, 0.46f, 1f), // Amarillo C64
        new Color(0.0f, 0.8f, 0.33f, 1f),   // Verde C64
        new Color(0.0f, 0.0f, 0.66f, 1f),   // Azul Oscuro C64
        new Color(0.0f, 0.53f, 1.0f, 1f),   // Azul Claro C64
        new Color(0.8f, 0.26f, 0.8f, 1f)    // Morado C64
    };

    public LoadScreen(thegreatrubiosbrothers game) {
        this.game = game;

        backgroundImage = new Texture(Gdx.files.internal("load_screen.png"));
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
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
        timeElapsed += delta;

        if (loaded && minimumWaitTimer <= 0) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        //BARRAS DE COLORES (FONDO)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int barHeight = 24;
        float speed = 200f;
        for (int y = -barHeight; y < screenHeight; y += barHeight) {
            int colorIndex = Math.abs(((y - (int)(timeElapsed * speed)) / barHeight) % c64Colors.length);
            shapeRenderer.setColor(c64Colors[colorIndex]);
            shapeRenderer.rect(0, y, screenWidth, barHeight);
        }
        shapeRenderer.end();
        game.batch.begin();

        //IMAGEN AL 80%
        float imgWidth = screenWidth * 0.8f;
        float imgRatio = (float) backgroundImage.getHeight() / backgroundImage.getWidth();
        float imgHeight = imgWidth * imgRatio;

        float imgX = (screenWidth - imgWidth) / 2f;
        float imgY = (screenHeight - imgHeight) / 2f;

        game.batch.draw(backgroundImage, imgX, imgY, imgWidth, imgHeight);

        layout.setText(font, message);
        float textX = (screenWidth - layout.width) / 2f;
        float textY = screenHeight * 0.25f;


        font.draw(game.batch, layout, textX, textY);
        font.setColor(Color.WHITE);

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
        shapeRenderer.dispose();
    }
}
