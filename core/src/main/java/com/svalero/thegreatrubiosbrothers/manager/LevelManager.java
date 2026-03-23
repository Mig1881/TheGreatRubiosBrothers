package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.svalero.thegreatrubiosbrothers.util.Constans;

public class LevelManager {

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public LevelManager() {
        map = new TmxMapLoader().load("levels/level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
    }

    // Este método lo llamaremos 60 veces por segundo desde la pantalla principal
    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

    public com.badlogic.gdx.maps.tiled.TiledMapTileLayer getCollisionLayer() {
        return (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get("terrain");
    }

    public float getMapPixelWidth() {
        return getCollisionLayer().getWidth() * Constans.TILE_WIDTH;
    }

    public float getMapPixelHeight() {
        return getCollisionLayer().getHeight() * Constans.TILE_HEIGHT;
    }
}
