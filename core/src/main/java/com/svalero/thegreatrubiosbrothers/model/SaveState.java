package com.svalero.thegreatrubiosbrothers.model;

public class SaveState {

    //lo que necesito saber para continuar la partida
    public int level;
    public int score;
    public int lives;
    public float timeLeft;
    public float playerX;
    public float playerY;

    //Las herramientas de serialización en Java
    // necesitan obligatoriamente un constructor vacío.
    public SaveState() {
    }

    // Constructor para rellenar los datos rápidamente al guardar
    public SaveState(int level, int score, int lives, float timeLeft, float playerX, float playerY) {
        this.level = level;
        this.score = score;
        this.lives = lives;
        this.timeLeft = timeLeft;
        this.playerX = playerX;
        this.playerY = playerY;
    }
}
