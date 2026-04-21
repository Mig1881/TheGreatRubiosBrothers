package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class CameraManager {

    private OrthographicCamera camera;
    private LogicManager logicManager;
    private float mapWidth;

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

    public void setMapWidth(float mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void handleCamera() {
        // Obtengo la posición actual del juagador
        float playerX = logicManager.player.getX();
        float halfCameraWidth = camera.viewportWidth / 2f;

        // Tope por la izquierda
        if (playerX < halfCameraWidth) {
            camera.position.x = halfCameraWidth;
        }
        // Tope por la derecha (si hemos configurado el mapWidth)
        else if (mapWidth > 0 && playerX > (mapWidth - halfCameraWidth)) {
            camera.position.x = mapWidth - halfCameraWidth;
        }
        // Movimiento libre
        else {
            camera.position.x = playerX;
        }

        camera.position.y = camera.viewportHeight / 2f;
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
