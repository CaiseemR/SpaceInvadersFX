package com.tonyjs.spaceinvadersfx;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by tonysaavedra on 6/21/16.
 */
public class MainApp extends Application {
    public static boolean GAME_SET;
    private Parent createContent() {
        Pane root = new Pane();

        ImageView imageView = new ImageView(new Image(getClass().getResource("/images/background.png").toExternalForm()));
        imageView.setFitWidth(600);
        imageView.setFitHeight(350);

        Rectangle mask = new Rectangle(600, 500);
        mask.setOpacity(0);
        mask.setMouseTransparent(true);

        MenuBox menuBox = new MenuBox(400, 100);
        menuBox.setTranslateX(100);
        menuBox.setTranslateY(350);

        MenuItem startButton = new MenuItem("START", 250);
        startButton.setOnAction(() -> {
            if (!GAME_SET) {
                GAME_SET = true;
                FadeTransition ft = new FadeTransition(Duration.seconds(0.8), mask);
                ft.setToValue(1);

                ft.setOnFinished(e -> {
                    mask.setOpacity(0);
                    SpaceInvadersFX game = new SpaceInvadersFX();
                    Stage st = new Stage();
                    try {
                        game.start(st);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
                ft.play();
            }
        });

        MenuItem quitGame = new MenuItem("QUIT", 250);
        quitGame.setOnAction(() -> {
            System.exit(0);
        });

        menuBox.addItem(startButton);
        menuBox.addItem(quitGame);
        root.getChildren().addAll(imageView, menuBox, mask);
        return root;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
