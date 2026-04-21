package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.svalero.thegreatrubiosbrothers.characters.Player;
import com.svalero.thegreatrubiosbrothers.util.Constans;
import com.svalero.thegreatrubiosbrothers.characters.enemies.Enemy;

import com.svalero.thegreatrubiosbrothers.items.PowerUp;
import com.svalero.thegreatrubiosbrothers.items.Fireball;

import java.util.List;
import java.util.ArrayList;

public class LogicManager {

    //Mini-clase para gestionar la cuenta atrás de los puentes
    private class CrumblingBlock {
        int cellX;
        int cellY;
        float timer;

        CrumblingBlock(int x, int y, float time) {
            this.cellX = x;
            this.cellY = y;
            this.timer = time;
        }
    }

    public Player player;
    private LevelManager levelManager;
    public List<Enemy> enemies;
    public List<PowerUp> powerUps;
    public List<Fireball> fireballs;
    private List<String> emptyBlocks;

    private List<CrumblingBlock> crumblingBlocks;

    private Rectangle viewPort;

    private float timeLeft;
    private float deathTimer = 0;
    private boolean gameOver = false;
    private boolean levelCompleted = false;

    public LogicManager() {
        player = new Player(R.getRegion("Player1-right0"), new Vector2(50, 150));
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        fireballs = new ArrayList<>();
        emptyBlocks = new ArrayList<>();
        crumblingBlocks = new ArrayList<>(); // Inicializamos la lista de puentes
        viewPort = new Rectangle();
        this.timeLeft = 100f;
    }

    public void setViewPort(float x, float y, float width, float height) {
        this.viewPort.set(x, y, width, height);
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        this.enemies = levelManager.loadEnemies();
    }

    public float getTimeLeft() { return timeLeft; }
    public boolean isGameOver() { return gameOver; }
    public boolean isLevelCompleted() { return levelCompleted; }

    public void update(float dt) {
        if (player.getCurrentState() == Player.State.DEAD) {
            deathTimer += dt;
            if (deathTimer > 3.0f) {
                if (player.getLives() > 0) {
                    respawn();
                } else {
                    gameOver = true;
                }
            }
        } else {
            handleInput();

            timeLeft -= dt;
            if (timeLeft < 0) {
                timeLeft = 0;
                player.die();
            }
            if (player.getY() < -10) {
                player.die();
            }
        }

        applyPhysics(dt);

        if (player.getCurrentState() != Player.State.DEAD) {
            player.updateAnimation(dt);
            checkPlayerEnemyCollisions();
            checkCollectibles();
            checkPlayerPowerUpCollisions();
            checkFireballEnemyCollisions();
        }

        for (Enemy enemy : enemies) {
            enemy.update(dt);
            applyEnemyPhysics(enemy, dt);
        }

        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.update(dt);
            if (p.isSpawned()) applyPowerUpCollisions(p);
            if (p.isToDestroy()) powerUps.remove(i);
        }

        for (int i = fireballs.size() - 1; i >= 0; i--) {
            Fireball f = fireballs.get(i);
            f.update(dt);
            applyFireballCollisions(f);
            if (f.isToDestroy()) fireballs.remove(i);
        }

        //GESTIÓN DEL TEMPORIZADOR DE LOS PUENTES
        for (int i = crumblingBlocks.size() - 1; i >= 0; i--) {
            CrumblingBlock cb = crumblingBlocks.get(i);
            cb.timer -= dt;
            //Se resta el tiempo que ha pasado en este frame
            if (cb.timer <= 0) {
                if (levelManager != null) {
                    levelManager.getCollisionLayer().setCell(cb.cellX, cb.cellY, null);
                }
                crumblingBlocks.remove(i);
            }
        }
        // ------------------------------------------------------
    }

    private void handleInput() {
        player.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.velocity.x = Constans.PLAYER_SPEED;
            player.setRunningRight(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.velocity.x = -Constans.PLAYER_SPEED;
            player.setRunningRight(false);
        }

        if (player.isOnGround() && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.velocity.y = Constans.JUMPING_SPEED;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.isHasFire()) {
            com.badlogic.gdx.graphics.Texture shotTex = R.assets.get(Constans.CHARACTERS_DIR + "shot.png", com.badlogic.gdx.graphics.Texture.class);
            com.badlogic.gdx.graphics.g2d.TextureRegion shotRegion = new com.badlogic.gdx.graphics.g2d.TextureRegion(shotTex);

            float spawnX = player.isRunningRight() ? player.getX() + player.getWidth() : player.getX() - shotRegion.getRegionWidth();
            Vector2 spawnPos = new Vector2(spawnX, player.getY() + player.getHeight() / 2f);

            fireballs.add(new Fireball(shotRegion, spawnPos, player.isRunningRight()));

            com.badlogic.gdx.audio.Sound shootSound = R.assets.get(Constans.SOUND_DIR + "shooting.wav", com.badlogic.gdx.audio.Sound.class);
            if (shootSound != null) shootSound.play();
        }
    }

    private void applyPhysics(float dt) {
        player.velocity.y -= Constans.GRAVITY * dt;
        player.move(0, player.velocity.y);
        player.setOnGround(false);

        if (player.getCurrentState() != Player.State.DEAD) {
            if (levelManager != null) {
                checkVerticalCollisions();
            }
        }

        player.move(player.velocity.x, 0);
        if (player.getCurrentState() != Player.State.DEAD) {
            if (levelManager != null) {
                checkHorizontalCollisions();

                if (player.getX() < 0) {
                    player.getPosition().x = 0;
                    player.getRect().setX(0);
                    player.velocity.x = 0;
                }
            }
        }
    }

    private void checkVerticalCollisions() {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int startX = (int) (player.getX() / Constans.TILE_WIDTH);
        int endX = (int) ((player.getX() + player.getWidth() - 1) / Constans.TILE_WIDTH);

        if (player.velocity.y < 0) {
            int bottomY = (int) (player.getY() / Constans.TILE_HEIGHT);
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, bottomY);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    player.getPosition().y = (bottomY + 1) * Constans.TILE_HEIGHT;
                    player.getRect().setY(player.getPosition().y);
                    player.velocity.y = 0;
                    player.setOnGround(true);

                    // --- NUEVO: AÑADIR PUENTE AL TEMPORIZADOR ---
                    if (cell.getTile().getProperties().containsKey("bridge")) {
                        // Verificamos si ya estaba en la lista para no añadirlo 60 veces por segundo
                        boolean alreadyCrumbling = false;
                        for (CrumblingBlock cb : crumblingBlocks) {
                            if (cb.cellX == x && cb.cellY == bottomY) {
                                alreadyCrumbling = true;
                                break;
                            }
                        }
                        if (!alreadyCrumbling) {
                            crumblingBlocks.add(new CrumblingBlock(x, bottomY, 0.4f));
                        }
                    }
                    break;
                }
            }
        } else if (player.velocity.y > 0) {
            int topY = (int) ((player.getY() + player.getHeight() - 1) / Constans.TILE_HEIGHT);
            boolean hitCeiling = false;

            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, topY);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    hitCeiling = true;

                    if (cell.getTile().getProperties().containsKey("power")) {
                        String blockId = x + "-" + topY;
                        if (!emptyBlocks.contains(blockId)) {
                            emptyBlocks.add(blockId);

                            com.badlogic.gdx.audio.Sound coinSound = R.assets.get(Constans.SOUND_DIR + "coins_appear.wav", com.badlogic.gdx.audio.Sound.class);
                            if (coinSound != null) coinSound.play();

                            Vector2 spawnPos = new Vector2(x * Constans.TILE_WIDTH, topY * Constans.TILE_HEIGHT);

                            if (!player.isHasFire()) {
                                com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> frames = new com.badlogic.gdx.utils.Array<>();
                                for (int i = 0; i <= 3; i++) frames.add(R.getRegion("Powerup-" + i));
                                com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> fireAnim = new com.badlogic.gdx.graphics.g2d.Animation<>(0.1f, frames);
                                powerUps.add(new PowerUp(fireAnim, spawnPos, PowerUp.Type.FIRE));

                            } else if (!player.isHasBomb()) {
                                com.badlogic.gdx.graphics.Texture bombTex = R.assets.get(Constans.CHARACTERS_DIR + "bomb.png", com.badlogic.gdx.graphics.Texture.class);
                                powerUps.add(new PowerUp(new com.badlogic.gdx.graphics.g2d.TextureRegion(bombTex), spawnPos, PowerUp.Type.BOMB));

                            } else if (!player.isHasLife()) {
                                com.badlogic.gdx.graphics.Texture lollipopTex = R.assets.get(Constans.CHARACTERS_DIR + "lollipop.png", com.badlogic.gdx.graphics.Texture.class);
                                powerUps.add(new PowerUp(new com.badlogic.gdx.graphics.g2d.TextureRegion(lollipopTex), spawnPos, PowerUp.Type.LIFE));

                            } else {
                                com.badlogic.gdx.graphics.Texture bombTex = R.assets.get(Constans.CHARACTERS_DIR + "bomb.png", com.badlogic.gdx.graphics.Texture.class);
                                powerUps.add(new PowerUp(new com.badlogic.gdx.graphics.g2d.TextureRegion(bombTex), spawnPos, PowerUp.Type.BOMB));
                            }
                        }
                    }
                }
            }

            if (hitCeiling) {
                player.getPosition().y = topY * Constans.TILE_HEIGHT - player.getHeight();
                player.getRect().setY(player.getPosition().y);
                player.velocity.y = 0;
            }
        }
    }

    private void checkHorizontalCollisions() {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int startY = (int) (player.getY() / Constans.TILE_HEIGHT);
        int endY = (int) ((player.getY() + player.getHeight() - 1) / Constans.TILE_HEIGHT);

        if (player.velocity.x > 0) {
            int rightX = (int) ((player.getX() + player.getWidth() - 1) / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(rightX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    player.getPosition().x = rightX * Constans.TILE_WIDTH - player.getWidth();
                    player.getRect().setX(player.getPosition().x);
                    player.velocity.x = 0;
                    break;
                }
            }
        } else if (player.velocity.x < 0) {
            int leftX = (int) (player.getX() / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(leftX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    player.getPosition().x = (leftX + 1) * Constans.TILE_WIDTH;
                    player.getRect().setX(player.getPosition().x);
                    player.velocity.x = 0;
                    break;
                }
            }
        }
    }

    private void applyPowerUpCollisions(PowerUp p) {
        if (levelManager == null) return;
        TiledMapTileLayer layer = levelManager.getCollisionLayer();

        int startX = (int) (p.getPosition().x / Constans.TILE_WIDTH);
        int endX = (int) ((p.getPosition().x + p.getBounds().width - 1) / Constans.TILE_WIDTH);

        if (p.velocity.y < 0) {
            int bottomY = (int) (p.getPosition().y / Constans.TILE_HEIGHT);
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, bottomY);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    p.landOnGround((bottomY + 1) * Constans.TILE_HEIGHT);
                    break;
                }
            }
        }

        int startY = (int) (p.getPosition().y / Constans.TILE_HEIGHT);
        int endY = (int) ((p.getPosition().y + p.getBounds().height - 1) / Constans.TILE_HEIGHT);

        if (p.velocity.x > 0) {
            int rightX = (int) ((p.getPosition().x + p.getBounds().width - 1) / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(rightX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    p.getPosition().x = rightX * Constans.TILE_WIDTH - p.getBounds().width;
                    p.reverseVelocity();
                    break;
                }
            }
        } else if (p.velocity.x < 0) {
            int leftX = (int) (p.getPosition().x / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(leftX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    p.getPosition().x = (leftX + 1) * Constans.TILE_WIDTH;
                    p.reverseVelocity();
                    break;
                }
            }
        }
    }

    private void applyFireballCollisions(Fireball f) {
        if (levelManager == null) return;
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int mapX = (int) ((f.getPosition().x + f.getBounds().width / 2) / Constans.TILE_WIDTH);
        int mapY = (int) ((f.getPosition().y + f.getBounds().height / 2) / Constans.TILE_HEIGHT);

        TiledMapTileLayer.Cell cell = layer.getCell(mapX, mapY);
        if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
            f.setToDestroy(true);
        }
    }

    private void applyEnemyPhysics(Enemy enemy, float dt) {
        if (!enemy.isFlying()) enemy.velocity.y -= Constans.GRAVITY * dt;
        enemy.move(0, enemy.velocity.y);
        if (levelManager != null) checkEnemyVerticalCollisions(enemy);
        enemy.move(enemy.velocity.x, 0);
        if (levelManager != null && !enemy.isFlying()) checkEnemyHorizontalCollisions(enemy);
    }

    private void checkEnemyVerticalCollisions(Enemy enemy) {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int startX = (int) (enemy.getX() / Constans.TILE_WIDTH);
        int endX = (int) ((enemy.getX() + enemy.getWidth() - 1) / Constans.TILE_WIDTH);

        if (enemy.velocity.y < 0) {
            int bottomY = (int) (enemy.getY() / Constans.TILE_HEIGHT);
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, bottomY);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    enemy.getPosition().y = (bottomY + 1) * Constans.TILE_HEIGHT;
                    enemy.getRect().setY(enemy.getPosition().y);
                    enemy.velocity.y = 0;
                    break;
                }
            }
        }
    }

    private void checkEnemyHorizontalCollisions(Enemy enemy) {
        TiledMapTileLayer layer = levelManager.getCollisionLayer();
        int startY = (int) (enemy.getY() / Constans.TILE_HEIGHT);
        int endY = (int) ((enemy.getY() + enemy.getHeight() - 1) / Constans.TILE_HEIGHT);

        if (enemy.velocity.x > 0) {
            int rightX = (int) ((enemy.getX() + enemy.getWidth() - 1) / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(rightX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    enemy.getPosition().x = rightX * Constans.TILE_WIDTH - enemy.getWidth();
                    enemy.getRect().setX(enemy.getPosition().x);
                    enemy.velocity.x = -enemy.velocity.x;
                    break;
                }
            }
        } else if (enemy.velocity.x < 0) {
            int leftX = (int) (enemy.getX() / Constans.TILE_WIDTH);
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(leftX, y);
                if (cell != null && cell.getTile().getProperties().containsKey("ground")) {
                    enemy.getPosition().x = (leftX + 1) * Constans.TILE_WIDTH;
                    enemy.getRect().setX(enemy.getPosition().x);
                    enemy.velocity.x = -enemy.velocity.x;
                    break;
                }
            }
        }
    }

    private void checkPlayerEnemyCollisions() {
        if (player.getCurrentState() == Player.State.DEAD) return;

        for (Enemy enemy : enemies) {
            if (enemy.isSquashed()) continue;

            if (player.getRect().overlaps(enemy.getRect())) {
                if (player.velocity.y < 0 && player.getY() > enemy.getY() + enemy.getHeight() / 2f) {
                    enemy.squash();
                    player.setScore(player.getScore() + 1);

                    com.badlogic.gdx.audio.Sound hitSound = R.assets.get("sounds/hit.wav", com.badlogic.gdx.audio.Sound.class);
                    if (hitSound != null) hitSound.play();
                    com.badlogic.gdx.audio.Sound bonusSound = R.assets.get("sounds/bonus_score.wav", com.badlogic.gdx.audio.Sound.class);
                    if (bonusSound != null) bonusSound.play();

                    player.velocity.y = Constans.JUMPING_SPEED * 0.8f;
                } else {
                    player.die();
                }
            }
        }
    }

    private void checkFireballEnemyCollisions() {
        for (Fireball f : fireballs) {
            if (f.isToDestroy()) continue;
            for (Enemy enemy : enemies) {
                if (enemy.isSquashed()) continue;
                if (f.getBounds().overlaps(enemy.getRect())) {
                    enemy.squash();
                    player.setScore(player.getScore() + 1);
                    f.setToDestroy(true);

                    com.badlogic.gdx.audio.Sound hitSound = R.assets.get("sounds/hit.wav", com.badlogic.gdx.audio.Sound.class);
                    if (hitSound != null) hitSound.play();
                    break;
                }
            }
        }
    }

    private void checkCollectibles() {
        if (player.getCurrentState() == Player.State.DEAD) return;

        TiledMapTileLayer layer = levelManager.getCollisionLayer();

        int startX = (int) (player.getX() / Constans.TILE_WIDTH);
        int endX = (int) ((player.getX() + player.getWidth() - 1) / Constans.TILE_WIDTH);
        int startY = (int) (player.getY() / Constans.TILE_HEIGHT);
        int endY = (int) ((player.getY() + player.getHeight() - 1) / Constans.TILE_HEIGHT);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                if (cell != null && cell.getTile() != null) {
                    if (cell.getTile().getProperties().containsKey("diamond")) {
                        player.setScore(player.getScore() + 1);
                        com.badlogic.gdx.audio.Sound diamondSound = R.assets.get("sounds/score_simple.wav", com.badlogic.gdx.audio.Sound.class);
                        if (diamondSound != null) diamondSound.play();
                        layer.setCell(x, y, null);
                    }

                    if (cell.getTile().getProperties().containsKey("exit")) {
                        levelCompleted = true;
                    }
                }
            }
        }
    }

    private void checkPlayerPowerUpCollisions() {
        if (player.getCurrentState() == Player.State.DEAD) return;

        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);

            if (p.isSpawned() && player.getRect().overlaps(p.getBounds())) {
                if (p.getType() == PowerUp.Type.FIRE) {
                    player.setHasFire(true);
                } else if (p.getType() == PowerUp.Type.BOMB) {
                    player.setHasBomb(true);
                    for (Enemy enemy : enemies) {
                        if (!enemy.isSquashed() && viewPort.contains(enemy.getX(), enemy.getY())) {
                            enemy.squash();
                            player.setScore(player.getScore() + 1);
                        }
                    }
                } else if (p.getType() == PowerUp.Type.LIFE) {
                    player.setHasLife(true);
                    player.setLives(player.getLives() + 1);
                }

                player.setScore(player.getScore() + 5);
                com.badlogic.gdx.audio.Sound bonusSound = R.assets.get("sounds/bonus_score.wav", com.badlogic.gdx.audio.Sound.class);
                if (bonusSound != null) bonusSound.play();

                p.setToDestroy(true);
            }
        }
    }

    private void respawn() {
        player.setCurrentState(Player.State.IDLE);
        player.velocity.set(0, 0);
        player.getPosition().set(50, 150);
        player.getRect().setPosition(50, 150);
        timeLeft = 100f;
        deathTimer = 0;
        if (levelManager != null) this.enemies = levelManager.loadEnemies();
        emptyBlocks.clear();
        fireballs.clear();
        crumblingBlocks.clear(); // --- Limpiamos los puentes a punto de caer
    }

    public void loadState(com.svalero.thegreatrubiosbrothers.model.SaveState state) {
        this.timeLeft = state.timeLeft;
        this.player.setScore(state.score);
        this.player.setLives(state.lives);

        this.player.setHasFire(state.hasFire);
        this.player.setHasBomb(state.hasBomb);
        this.player.setHasLife(state.hasLife);

        this.player.getPosition().set(state.playerX, state.playerY);
        this.player.getRect().setPosition(state.playerX, state.playerY);
    }
}
