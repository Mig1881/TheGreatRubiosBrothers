package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.svalero.thegreatrubiosbrothers.model.SaveState;

public class SaveManager {

    // El nombre del archivo donde se guardará la partida
    private static final String SAVE_FILE = "savegame.sav";


    public static void saveGame(SaveState state) {
        Json json = new Json();
        String serializedData = json.toJson(state);
        // Obtengo la referencia (usando .local para poder escribir)
        FileHandle file = Gdx.files.local(SAVE_FILE);
        // Escribe la cadena de texto en el fichero (false para sobrescribir)
        file.writeString(serializedData, false);
        System.out.println("¡Partida guardada con éxito en: " + file.path() + "!");
    }

    public static SaveState loadGame() {
        FileHandle file = Gdx.files.local(SAVE_FILE);

        if (file.exists()) {
            // Lee el contenido del fichero como un String
            String serializedData = file.readString();

            // "Des-serializa" el String convirtiéndolo de vuelta en un objeto SaveState
            Json json = new Json();
            return json.fromJson(SaveState.class, serializedData);
        }
        // Si no hay archivo, devuelvo null
        return null;
    }

    public static boolean hasSave() {
        // Método para saber si pintamos el botón de "LOAD GAME" en el menú
        return Gdx.files.local(SAVE_FILE).exists();
    }
}
