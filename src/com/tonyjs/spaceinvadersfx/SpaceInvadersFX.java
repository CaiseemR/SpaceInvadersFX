package com.tonyjs.spaceinvadersfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by tonysaavedra on 6/17/16.
 */
public class SpaceInvadersFX extends Application {
    private Sprite[][] enemies = new Sprite[5][13];
    private Sprite[][] enemiesMoved = new Sprite[5][13];
    private Sprite[][] currentEnemies;
    private Sprite[] barriers = new Sprite[4];
    private int[] coordinates = new int[]{80, 95, 120, 140, 200, 260, 300, 320, 340};
    private int SCENE_WIDTH = 600;
    private int APP_HEIGHT = 600;
    private int APP_WIDTH = 800;
    private int SPACE = 40;
    private int coordinateY = 80;
    private int coordinateX = APP_WIDTH/3 - (SPACE*3);
    private int playerLives = 3;
    private int score = 0;
    private int totalEnemies;
    private int currentMoveSound = 0;
    private Text playerLivesLabel, scoreLabel, pointsLabel;
    private GraphicsContext gc;
    private Sprite tank, secondTank, thirdTank, lastAlien, UFO;
    private ArrayList<Sprite> missiles = new ArrayList<>();
    private ArrayList<Sprite> alienBombs = new ArrayList<>();
    private ArrayList<SoundEffect> moveEffects;
    private Canvas gameCanvas;
    private double time = 0.40;
    private double ufoTime = 0.40;
    private boolean GAME_IS_PAUSED = false;
    private boolean SHIFTING_RIGHT, SHIFTING_LEFT, SHIFTING_DOWN, PLAYER_SHOT,
            GAME_OVER, GAME_IS_WON, LIFE_END, UFO_SPAWNED, MISSILE_LAUNCHED;
    private LongValue startNanoTime;
    private double elapsedTime, pos;
    private AnimationTimer timer;
    private SoundEffect ufoEffect, alienMoveEffect,
            shootEffect, killEffect, explosionEffect;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Space Invaders FX");
        primaryStage.setResizable(false);

        Group root = new Group();

        gameCanvas = new Canvas(APP_WIDTH, APP_HEIGHT);

        gc = gameCanvas.getGraphicsContext2D();

        Scene mainScene = new Scene(root);
        mainScene.setFill(Color.BLACK);

        setGUI();
        spawnEnemies();
        setMovedEnemies();
        updateCurrentEnemies();
        setBarriers();
        setPlayer();
        setSoundEffects();
        startGame();

        root.getChildren().addAll(gameCanvas, scoreLabel, pointsLabel, playerLivesLabel);

        primaryStage.setScene(mainScene);
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
            } else if (e.getCode() == KeyCode.LEFT) {
                if (tank.getPositionX() > 50 && !GAME_IS_PAUSED) {
                    moveTankLeft();
                }
            } else if (e.getCode() == KeyCode.RIGHT) {
                if (tank.getPositionX() < APP_WIDTH - 100 && !GAME_IS_PAUSED) {
                    moveTankRight();
                }
            } else if (e.getCode() == KeyCode.SPACE) {
                GAME_IS_PAUSED = !GAME_IS_PAUSED;
                if (GAME_IS_PAUSED) {
                    timer.stop();
                } else {
                    timer.start();
                }
            } else if (LIFE_END && e.getCode() == KeyCode.ENTER) {

                updatePlayerLives();
                resetGameVariables();
                startGame();
            }
        });

        primaryStage.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP && !MISSILE_LAUNCHED) {
                shootEffect.playClip();
                shootMissile();
                MISSILE_LAUNCHED = true;
            } else if (e.getCode() == KeyCode.LEFT) {
                tank.setVelocity(0, 0);
            } else if (e.getCode() == KeyCode.RIGHT) {
                tank.setVelocity(0, 0);
            }
        });
        primaryStage.show();
    }

    private void setGUI() {
        playerLivesLabel = new Text("LIVES:");
        playerLivesLabel.setFill(Color.WHITE);
        playerLivesLabel.setFont(Font.font("Monaco", FontWeight.EXTRA_BOLD, 20));
        playerLivesLabel.setX(SCENE_WIDTH + SPACE);
        playerLivesLabel.setY(30);

        scoreLabel = new Text("SCORE:");
        scoreLabel.setFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Monaco", FontWeight.EXTRA_BOLD, 20));
        scoreLabel.setX(10);
        scoreLabel.setY(30);

        pointsLabel = new Text(Integer.toString(score));
        pointsLabel.setFill(Color.LIMEGREEN);
        pointsLabel.setFont(Font.font("Monaco", FontWeight.EXTRA_BOLD, 20));
        pointsLabel.setX(95);
        pointsLabel.setY(30);
    }

    private void spawnEnemies() {
        for (int y = 80, i = 0; y < APP_HEIGHT / 2 + SPACE && i < 5; y += SPACE, i++) {
            for (int x = APP_WIDTH/3 - (SPACE*3), j = 0; x < 660 && j < 13; x += SPACE, j++) {
                if (y < 90) {
                    enemies[i][j] = spawnSmallAlien(x, y, "/images/small_invader_a.png");
                    gc.drawImage(enemies[i][j].getImage(), x, y);
                } else if (y < 200) {
                    enemies[i][j] = spawnMediumAlien(x, y, "/images/medium_invader_a.png");
                    gc.drawImage(enemies[i][j].getImage(), x, y);
                } else {
                    enemies[i][j] = spawnLargeAlien(x, y, "/images/large_invader_a.png");
                    gc.drawImage(enemies[i][j].getImage(), x, y);
                }
                totalEnemies++;
            }
        }
    }

    private void setMovedEnemies() {
        for (int y = 80, i = 0; y < APP_HEIGHT / 2 + SPACE && i < 5; y += SPACE, i++) {
            for (int x = APP_WIDTH/3 - (SPACE*3), j = 0; x < 660 && j < 13; x += SPACE, j++) {
                if (y < 90) {
                    enemiesMoved[i][j] = spawnSmallAlien(x, y, "/images/small_invader_b.png");
                } else if (y < 200) {
                    enemiesMoved[i][j] = spawnMediumAlien(x, y, "/images/medium_invader_b.png");
                } else {
                    enemiesMoved[i][j] = spawnLargeAlien(x, y, "/images/large_invader_b.png");
                }
            }
        }
    }

    private Sprite spawnSmallAlien(int x, int y, String imagePath) {
        Sprite smallAlien = new Sprite();
        smallAlien.setImage(imagePath);
        smallAlien.setPosition(x, y);
        return smallAlien;
    }

    private Sprite spawnMediumAlien(int x, int y, String imagePath) {
        Sprite mediumAlien = new Sprite();
        mediumAlien.setImage(imagePath);
        mediumAlien.setPosition(x, y);
        return mediumAlien;
    }

    private Sprite spawnLargeAlien(int x, int y, String imagePath) {
        Sprite largeAlien = new Sprite();
        largeAlien.setImage(imagePath);
        largeAlien.setPosition(x, y);
        return largeAlien;
    }

    private void setBarriers() {
        for (int x = 180, i = 0; x < SCENE_WIDTH; x += 3*SPACE, i++) {
            Sprite barrier = new Sprite();
            barrier.setImage("/images/barrier.png");
            barrier.setPosition(x, APP_HEIGHT - 100);
            barriers[i] = barrier;
        }
    }

    private void renderBarriers() {
        for(Sprite barrier : barriers) {
            barrier.render(gc);
        }
    }

    private void setPlayer() {
        tank = new Sprite();
        tank.setImage("/images/tank.png");
        tank.setPosition(APP_WIDTH/2 - SPACE/2, APP_HEIGHT - SPACE);
        tank.render(gc);

        secondTank = new Sprite();
        secondTank.setImage("/images/tank.png");
        secondTank.setPosition(SCENE_WIDTH + 3 * SPACE, 10);
        secondTank.render(gc);

        thirdTank = new Sprite();
        thirdTank.setImage("/images/tank.png");
        thirdTank.setPosition(SCENE_WIDTH + 4 * SPACE, 10);
        thirdTank.render(gc);
    }

    private void startGame() {
        SHIFTING_RIGHT = true;
        startNanoTime = new LongValue(System.nanoTime());

        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0;
                startNanoTime.value = now;

                gc.clearRect(0, 30, APP_WIDTH, APP_HEIGHT);

                renderBarriers();

                lastAlien = getLastAlien();

                checkLastAlienStatus();
                animateEnemies();
                tryToSpawnUFO();
                checkUFOStatus();
                checkMissileStatus();
                checkBombStatus();
                checkTankStatus();

                int timeDiff = updateTime();
                time += timeDiff >= 0 ? 0.010 + (.005 * timeDiff) : 0.12;

                if (time >= 0.5) {
                    playMoveEffect();
                    if (SHIFTING_RIGHT) {
                        if (coordinateX < 210) {
                            if (coordinateY <= 320) {
                                coordinateX += 10;
                            } else {
                                coordinateX += 20;
                            }
                        } else {
                            if (!SHIFTING_LEFT) {
                                coordinateY += 15;
                                SHIFTING_LEFT = true;
                                SHIFTING_RIGHT = false;
                            }
                        }
                    } else if (SHIFTING_LEFT ) {
                        if (coordinateX > 80) {
                            if (coordinateY <= 320) {
                                coordinateX -= 10;
                            } else {
                                coordinateX -= 20;
                            }
                        } else {
                            if (!SHIFTING_RIGHT) {
                                coordinateY += 15;
                                SHIFTING_RIGHT = true;
                                SHIFTING_LEFT = false;
                            }

                        }
                    }
                    updateCurrentEnemies();
                    shootPlayer();
                    time = 0;
                }

                if (UFO_SPAWNED) {
                    ufoTime += 0.008;
                    if (ufoTime >= 0.084) {
                        ufoEffect.playClip();
                        ufoTime = 0;
                    }
                }
            }
        };
        timer.start();
    }

    private int updateTime() {
        for (int i = 0; i < coordinates.length; i++) {
            if (coordinateY <= coordinates[i]) {
                return i;
            }
        }
        return -1;
    }

    private void checkLastAlienStatus() {
        if (lastAlien != null) {
            pos = lastAlien.getPositionY();
            if (pos >= APP_HEIGHT - 100 - lastAlien.getHeight()) {
                LIFE_END = true;
                MISSILE_LAUNCHED = false;
                missiles.clear();
                UFO = null;
                timer.stop();
            }
        }
    }

    private void checkTankStatus() {
        if (tank.getPositionX() < 50) {
            tank.setPosition(tank.getPositionX() + 1, tank.getPositionY());
            tank.setVelocity(0, 0);
        } else if (tank.getPositionX() > APP_WIDTH - 100) {
            tank.setPosition(tank.getPositionX() - 1, tank.getPositionY());
            tank.setVelocity(0, 0);
        }

        tank.render(gc);
        tank.update(elapsedTime);
    }

    private void tryToSpawnUFO() {
        if (!UFO_SPAWNED && spawnRandomUFO()) {
            UFO_SPAWNED = true;
            UFO.setVelocity(170, 0);
        }
    }

    private void checkUFOStatus() {
        if (UFO_SPAWNED && UFO.getPositionX() < APP_WIDTH) {
            UFO.render(gc);
            UFO.update(elapsedTime);
            if (MISSILE_LAUNCHED) {
                if (UFO.intersects(missiles.get(0))) {
                    explosionEffect.playClip();
                    score += 100;
                    updateTotalScore();
                    UFO = null;
                    UFO_SPAWNED = false;
                }
            }
        } else {
            UFO = null;
            UFO_SPAWNED = false;
        }
    }

    private void checkMissileStatus() {
        if (MISSILE_LAUNCHED) {
            Sprite missile = missiles.get(0);
            missile.render(gc);
            missile.update(elapsedTime);
            if (missileHit() || missile.getPositionY() <= 30 || barrierHit(missile)) {
                missiles.clear();
                MISSILE_LAUNCHED = false;
                if (totalEnemies == 0) {
                    GAME_IS_WON = true;
                    System.exit(0);
                }
            }
        }
    }

    private void checkBombStatus() {
        if (PLAYER_SHOT) {
            Sprite bomb = alienBombs.get(0);
            bomb.render(gc);
            bomb.update(elapsedTime);
            if (bomb.getPositionY() >= APP_HEIGHT - SPACE || barrierHit(bomb) || playerHit(bomb)) {
                alienBombs.clear();
                PLAYER_SHOT = false;
            }
        }
    }

    private void updateCurrentEnemies() {
        currentEnemies = currentEnemies == enemies ? enemiesMoved : enemies;
    }

    private void animateEnemies() {
        for (int y = coordinateY, i = 0; y < APP_HEIGHT - 100  && i < 5; y += SPACE, i++) {
            for (int x = coordinateX, j = 0; x < 700 && j < 13; x += SPACE, j++) {
                if (currentEnemies[i][j] != null) {
                    currentEnemies[i][j].setPosition(x, y);
                    if (y < 90) {
                        gc.drawImage(currentEnemies[i][j].getImage(), x, y);
                    } else if (y < 200) {
                        gc.drawImage(currentEnemies[i][j].getImage(), x, y);
                    } else {
                        gc.drawImage(currentEnemies[i][j].getImage(), x, y);
                    }
                }
            }
        }
    }

    private Sprite getLastAlien() {
        for (int i = currentEnemies.length - 1; i >= 0; i --) {
            for (int j = 0; j < currentEnemies[0].length; j++) {
                if (currentEnemies[i][j] != null) {
                    return currentEnemies[i][j];
                }
            }
        }
        return null;
    }

    private boolean spawnRandomUFO() {
        double random = Math.random();
        if (random < 0.0003) {
            spawnUFO();
            return true;
        }
        return false;
    }

    private void spawnUFO() {
        UFO = new Sprite();
        UFO.setImage("/images/ufo.png");
        UFO.setPosition(0, 40);
        UFO.render(gc);
    }

    private void moveTankLeft() {
        tank.setVelocity(-250, 0);
    }

    private void moveTankRight() {
        tank.setVelocity(250, 0);
    }

    private void shootMissile() {
        Sprite missile = new Sprite();
        missile.setImage("/images/rocket.png");
        missile.setPosition(tank.getPositionX() + 10, tank.getPositionY() - 20);
        missile.setVelocity(0, -350);
        missile.render(gc);
        missiles.add(missile);
    }

    private boolean missileHit() {
        for (int i = 0; i < currentEnemies.length; i++) {
            for (int j = 0; j < currentEnemies[0].length; j++) {
                if (currentEnemies[i][j] != null) {
                    if (currentEnemies[i][j].intersects(missiles.get(0))) {
                        switch ((int)currentEnemies[i][j].getWidth()) {
                            case 31:
                                score += 10;
                                break;
                            case 28:
                                score += 20;
                                break;
                            case 21:
                                score += 30;
                                break;
                        }
                        killEffect.playClip();
                        updateTotalScore();
                        totalEnemies--;
                        currentEnemies[i][j] = null;
                        enemies[i][j] = null;
                        enemiesMoved[i][j] = null;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean barrierHit(Sprite ammo) {
        for (Sprite barrier : barriers) {
            if (barrier.intersects(ammo)) {
                return true;
            }
        }
        return false;
    }

    private void shootPlayer() {
        Sprite alienBomb = new Sprite();
        alienBomb.setImage("/images/missile.png");
        for (int i = (int)(Math.random() * currentEnemies.length - 1); i >= 0; i--) {
            if (PLAYER_SHOT) {
                break;
            }
            for (int j = (int)(Math.random() * currentEnemies[i].length - 1); j >= 0; j--) {
                if (currentEnemies[i][j] != null) {
                    alienBomb.setPosition(currentEnemies[i][j].getPositionX(),
                            currentEnemies[i][j].getPositionY());
                    PLAYER_SHOT = true;
                    break;
                }
            }
        }
        alienBomb.setVelocity(0, 350);
        alienBombs.add(alienBomb);
    }

    private boolean playerHit(Sprite bomb) {
        if (tank.intersects(bomb)) {
            explosionEffect.playClip();
            LIFE_END = true;
            return true;
        }
        return false;
    }

    private void updateTotalScore() {
        pointsLabel.setText(Integer.toString(score));
    }

    private void updatePlayerLives() {
        playerLives--;
        if (playerLives == 2 && LIFE_END) {
            gc.clearRect(SCENE_WIDTH + 4 * SPACE, 10, APP_WIDTH, 20);
        } else if (playerLives == 1 && LIFE_END) {
            gc.clearRect(SCENE_WIDTH + 3 * SPACE, 10, APP_WIDTH, 20);
        }
    }

    private void resetGameVariables() {
        time = 0;
        coordinateY = 80;
        coordinateX = APP_WIDTH/3 - (SPACE*3);
        LIFE_END = false;
        SHIFTING_DOWN = false;
        SHIFTING_LEFT = false;
    }

    private void setSoundEffects() {
        shootEffect = new SoundEffect("/sounds/shoot.wav");
        killEffect = new SoundEffect("/sounds/alienKilled.wav");
        ufoEffect = new SoundEffect("/sounds/ufo.wav");
        explosionEffect = new SoundEffect("/sounds/explosion.wav");
        setAlienMoveSounds();
    }

    private void setAlienMoveSounds() {
        moveEffects = new ArrayList<>();
        moveEffects.add(new SoundEffect("/sounds/alienMove.wav"));
        moveEffects.add(new SoundEffect("/sounds/alienMove2.wav"));
        moveEffects.add(new SoundEffect("/sounds/alienMove3.wav"));
        moveEffects.add(new SoundEffect("/sounds/alienMove4.wav"));
    }

    private void playMoveEffect() {
        if (currentMoveSound == moveEffects.size()) {
            currentMoveSound = 0;
        }

        moveEffects.get(currentMoveSound).playClip();
        currentMoveSound++;
    }

    public class LongValue {
        public long value;

        public LongValue(long i) {
            this.value = i;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
