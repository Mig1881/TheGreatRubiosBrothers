package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.svalero.thegreatrubiosbrothers.characters.enemies.*;
import com.svalero.thegreatrubiosbrothers.util.Constans;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    //Lo dejo preparado para que pueda cargar dinamicamente cualquier mapa que se eliga
    //de momento he hecho una copia del level1 y lo he renombrado a level2 para que no casque
    public LevelManager() {
        int selectedLevel = ConfigurationManager.getStartLevel();
        String mapPath = "levels/level" + selectedLevel + ".tmx";
        map = new TmxMapLoader().load(mapPath);
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

                if (type.equals("enemy1")) {
                    enemyList.add(new Enemy1(new Vector2(x, y)));
                } else if (type.equals("enemy2")) {
                    enemyList.add(new Enemy2(new Vector2(x, y)));
                } else if (type.equals("enemy3")) {
                    enemyList.add(new Enemy3(new Vector2(x, y)));
                } else if (type.equals("enemy4")) {
                    enemyList.add(new Enemy4(new Vector2(x, y)));
                }
            }
        }
        return enemyList;
    }
}
