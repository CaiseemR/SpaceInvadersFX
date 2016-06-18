package com.tonyjs.spaceinvadersfx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    private int SCENE_WIDTH = 600;
    private int APP_HEIGHT = 600;
    private int APP_WIDTH = 800;
    private int SPACE = 40;
    private int playerLives = 3;
    private int score = 0;
    private Text playerLivesLabel, scoreLabel, pointsLabel;
    private GraphicsContext gc;
    private Sprite secondTank, thirdTank;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Space Invaders FX");
        primaryStage.setResizable(false);

        Group root = new Group();

        Canvas gameCanvas = new Canvas(APP_WIDTH, APP_HEIGHT);

        gc = gameCanvas.getGraphicsContext2D();

        Scene mainScene = new Scene(root);
        mainScene.setFill(Color.BLACK);

        setGUI();
        spawnEnemies();
        setBarriers();
        setPlayer();

        root.getChildren().addAll(gameCanvas, scoreLabel, pointsLabel, playerLivesLabel);

        primaryStage.setScene(mainScene);
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
                    enemies[i][j] = spawnSmallAlien(x, y);
                } else if (y < 200) {
                    enemies[i][j] = spawnMediumAlien(x, y);
                } else {
                    enemies[i][j] = spawnLargeAlien(x, y);
                }
            }
        }
    }

    private Sprite spawnSmallAlien(int x, int y) {
        Sprite smallAlien = new Sprite();
        smallAlien.setImage("/images/small_invader_a.png");
        smallAlien.setPosition(x, y);
        smallAlien.render(gc);
        return smallAlien;
    }

    private Sprite spawnMediumAlien(int x, int y) {
        Sprite mediumAlien = new Sprite();
        mediumAlien.setImage("/images/medium_invader_a.png");
        mediumAlien.setPosition(x, y);
        mediumAlien.render(gc);
        return mediumAlien;
    }

    private Sprite spawnLargeAlien(int x, int y) {
        Sprite largeAlien = new Sprite();
        largeAlien.setImage("/images/large_invader_a.png");
        largeAlien.setPosition(x, y);
        largeAlien.render(gc);
        return largeAlien;
    }

    private void setBarriers() {
        for (int x =180; x < SCENE_WIDTH; x += 3*SPACE) {
            Sprite barrier = new Sprite();
            barrier.setImage("/images/barrier.png");
            barrier.setPosition(x, APP_HEIGHT - 100);
            barrier.render(gc);
        }
    }


    private void setPlayer() {
        Sprite tank = new Sprite();
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

    public static void main(String[] args) {
        launch(args);
    }
}
