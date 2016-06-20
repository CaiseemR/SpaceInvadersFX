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

/**
 * Created by tonysaavedra on 6/17/16.
 */
public class SpaceInvadersFX extends Application {
    private Sprite[][] enemies = new Sprite[5][13];
    private Sprite[][] enemiesMoved = new Sprite[5][13];
    private Sprite[][] currentEnemies;
    private int SCENE_WIDTH = 600;
    private int APP_HEIGHT = 600;
    private int APP_WIDTH = 800;
    private int SPACE = 40;
    private int playerLives = 3;
    private int score = 0;
    private Text playerLivesLabel, scoreLabel, pointsLabel;
    private GraphicsContext gc;
    private Sprite tank, secondTank, thirdTank;
    private Canvas gameCanvas;
    private double time = 0.40;
    private boolean GAME_IS_PAUSED = false;
    private LongValue startNanoTime;
    private double elapsedTime;

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
        startGame();

        root.getChildren().addAll(gameCanvas, scoreLabel, pointsLabel, playerLivesLabel);

        primaryStage.setScene(mainScene);
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
            } else if (e.getCode() == KeyCode.LEFT) {
                if (tank.getPositionX() > 50) {
                    moveTankLeft();
                }
            } else if (e.getCode() == KeyCode.RIGHT) {
                if (tank.getPositionX() < APP_WIDTH - 100) {
                    moveTankRight();
                }
            } else if (e.getCode() == KeyCode.SPACE) {
                GAME_IS_PAUSED = !GAME_IS_PAUSED;
            }
        });

        primaryStage.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP) {
            } else if (e.getCode() == KeyCode.LEFT) {
                tank.setVelocity(0, 0);
            } else if (e.getCode() == KeyCode.RIGHT) {
                tank.setVelocity(0, 0);
            } else if (e.getCode() == KeyCode.SPACE) {
                GAME_IS_PAUSED = !GAME_IS_PAUSED;
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
        for (int x = 180; x < SCENE_WIDTH; x += 3*SPACE) {
            Sprite barrier = new Sprite();
            barrier.setImage("/images/barrier.png");
            barrier.setPosition(x, APP_HEIGHT - 100);
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
        startNanoTime = new LongValue(System.nanoTime());
        new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0;
                startNanoTime.value = now;

                gc.clearRect(0, 100, APP_WIDTH , APP_HEIGHT - 200);
                gc.clearRect(0, APP_HEIGHT - SPACE, APP_WIDTH , APP_HEIGHT);

                moveEnemies();

                if (tank.getPositionX() < 50) {
                    tank.setPosition(tank.getPositionX() + 1, tank.getPositionY());
                    tank.setVelocity(0, 0);
                } else if (tank.getPositionX() > APP_WIDTH - 100) {
                    tank.setPosition(tank.getPositionX() - 1, tank.getPositionY());
                    tank.setVelocity(0, 0);
                }

                tank.render(gc);
                tank.update(elapsedTime);

                time += 0.010;
                if (time >= 0.5 && !GAME_IS_PAUSED) {
                    updateCurrentEnemies();
                    time = 0;
                }
            }
        }.start();
    }

    private void updateCurrentEnemies() {
        currentEnemies = currentEnemies == enemies ? enemiesMoved : enemies;
    }

    private void moveEnemies() {
        for (int y = 80, i = 0; y < APP_HEIGHT / 2 + SPACE && i < 5; y += SPACE, i++) {
            for (int x = APP_WIDTH/3 - (SPACE*3), j = 0; x < 660 && j < 13; x += SPACE, j++) {
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

    private void moveTankLeft() {
        tank.setVelocity(-200, 0);
    }

    private void moveTankRight() {
        tank.setVelocity(200, 0);
    }

    public class LongValue
    {
        public long value;

        public LongValue(long i)
        {
            value = i;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
