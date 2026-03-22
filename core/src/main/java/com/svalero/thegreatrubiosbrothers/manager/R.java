package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class R {

    public static AssetManager assets = new AssetManager();


    public static void loadAllResources() {
        assets.load(Constans.TEXTURE_ATLAS, TextureAtlas.class);
        assets.load(Constans.TEXTURES_DIR + "libgdx.png", Texture.class);

        loadSounds();
        loadMusics();
    }

    private static void loadSounds() {
        assets.load(Constans.SOUND_DIR + "score_simple.wav", Sound.class);
        assets.load(Constans.SOUND_DIR + "hit.wav", Sound.class);
        assets.load(Constans.SOUND_DIR + "bonus_score.wav", Sound.class);
        assets.load(Constans.SOUND_DIR + "coins_appear.wav", Sound.class);
    }

    private static void loadMusics() {
        assets.load(Constans.MUSIC_DIR + "01_Intro.mp3", Music.class);
        assets.load(Constans.MUSIC_DIR + "02_Title Screen.mp3", Music.class);
        assets.load(Constans.MUSIC_DIR + "04_In-Game 1.mp3", Music.class);
    }


    public static boolean update() {
        return assets.update();
    }


    public static TextureRegion getRegion(String name) {
        return assets.get(Constans.TEXTURE_ATLAS, TextureAtlas.class).findRegion(name);
    }

    public static void dispose() {
        assets.dispose();
    }
}
