package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svalero.thegreatrubiosbrothers.characters.enemies.*;
import com.svalero.thegreatrubiosbrothers.util.Constans;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<Texture> waterTextures;

    public LevelManager(int currentLevel) {
        String mapPath = "levels/level" + currentLevel + ".tmx";
        map = new TmxMapLoader().load(mapPath);
        animateWater();
        mapRenderer = new OrthogonalTiledMapRenderer(map);
    }

    private void animateWater() {
        waterTextures = new Array<>();
        Array<StaticTiledMapTile> waterFrames = new Array<>();

        for (int i = 0; i <= 3; i++) {
            Texture tex = new Texture(Gdx.files.internal("characters/water" + i + ".png"));
            waterTextures.add(tex);
            waterFrames.add(new StaticTiledMapTile(new TextureRegion(tex)));
        }

        AnimatedTiledMapTile animatedWater = new AnimatedTiledMapTile(0.2f, waterFrames);
        animatedWater.getProperties().put("water", true);
        animatedWater.setId(9999);


        for (MapLayer mapLayer : map.getLayers()) {
            // Solo interesan las capas de baldosas
            if (mapLayer instanceof TiledMapTileLayer) {
                TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;

                for (int x = 0; x < layer.getWidth(); x++) {
                    for (int y = 0; y < layer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                        if (cell != null && cell.getTile() != null) {
                            // Si tiene la propiedad "water", la cambio para animar
                            if (cell.getTile().getProperties().containsKey("water")) {
                                cell.setTile(animatedWater);
                            }
                        }
                    }
                }
            }
        }
    }

    public void render(OrthographicCamera camera) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        if (waterTextures != null) {
            for (Texture tex : waterTextures) {
                tex.dispose();
            }
        }
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
