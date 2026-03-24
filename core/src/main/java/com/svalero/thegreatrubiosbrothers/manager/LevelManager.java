package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.svalero.thegreatrubiosbrothers.util.Constans;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy1;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

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

    public List<Enemy> loadEnemies() {
        List<Enemy> enemyList = new ArrayList<>();
        MapLayer layer = map.getLayers().get("enemies");

        if (layer == null) return enemyList;

        for (MapObject object : layer.getObjects()) {
            if (object.getProperties().containsKey("type")) {

                String type = object.getProperties().get("type").toString();
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);

                // ¡LA MAGIA DEL POLIMORFISMO!
                if (type.equals("enemy1")) {
                    enemyList.add(new Enemy1(new Vector2(x, y)));
                }
                // En el futuro harás: else if (type.equals("enemy2")) { enemyList.add(new Enemy2(...)); }
            }
        }
        return enemyList;
    }
}
