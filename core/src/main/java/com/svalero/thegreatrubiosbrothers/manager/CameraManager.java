package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class CameraManager {

    private OrthographicCamera camera;
    private LogicManager logicManager; // Necesitamos saber dónde está el jugador

    public CameraManager(LogicManager logicManager) {
        this.logicManager = logicManager;
        init();
    }

    private void init() {
        camera = new OrthographicCamera();
        // Configuramos el zoom inicial (dividiendo entre 3, como hicimos antes en GameScreen)
        camera.setToOrtho(false, Constans.SCREEN_WIDTH / 3f, Constans.SCREEN_HEIGHT / 3f);
        camera.update();
    }

    public void handleCamera() {
        // Obtengo la posición actual de David
        float playerX = logicManager.player.getX();
        float playerY = logicManager.player.getY();

        // Seguimiento en el eje X:
        // Si el jugador está muy a la izquierda, no dejamos que la cámara muestre el vacío (fuera del mapa).
        // Calculamos la mitad del ancho de visión de la cámara para saber dónde poner el tope izquierdo.
        float halfCameraWidth = camera.viewportWidth / 2f;

        if (playerX < halfCameraWidth) {
            camera.position.x = halfCameraWidth; // Tope a la izquierda
        } else {
            camera.position.x = playerX; // Sigue a David libremente
        }

        // Seguimiento en el eje Y:
        // Mantendremos la cámara a una altura fija por ahora para que no maree saltando
        camera.position.y = camera.viewportHeight / 2f;

        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
