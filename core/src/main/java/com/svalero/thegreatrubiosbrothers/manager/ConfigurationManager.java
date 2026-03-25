package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

public class ConfigurationManager {

    private static Preferences prefs;
    private static Music currentMusic;

    public static void init() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences("TheGreatRubiosBrothersPrefs");
            // Valores por defecto
            if (!prefs.contains("musicOn")) {
                prefs.putBoolean("musicOn", true);
                prefs.putInteger("startLevel", 1);
                prefs.flush(); // Guarda los cambios
            }
        }
    }

    // --- MÉTODOS PARA LEER Y GUARDAR OPCIONES ---
    public static boolean isMusicOn() { return prefs.getBoolean("musicOn", true); }

    public static void setMusicOn(boolean on) {
        prefs.putBoolean("musicOn", on);
        prefs.flush();
        updateMusicState();
    }

    public static int getStartLevel() { return prefs.getInteger("startLevel", 1); }

    public static void setStartLevel(int level) {
        prefs.putInteger("startLevel", level);
        prefs.flush();
    }

    // --- GESTOR DE MÚSICA DE FONDO ---
    public static void playMusic(String filePath) {
        init(); // Aseguro prefs

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        // Cargamos la nueva y la ponemos en bucle
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.5f); // Volumen al 50%

        if (isMusicOn()) {
            currentMusic.play();
        }
    }

    // Se llama cuando cambiamos el interruptor en opciones
    private static void updateMusicState() {
        if (currentMusic != null) {
            if (isMusicOn() && !currentMusic.isPlaying()) {
                currentMusic.play();
            } else if (!isMusicOn() && currentMusic.isPlaying()) {
                currentMusic.pause();
            }
        }
    }
}
