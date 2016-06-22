package com.tonyjs.spaceinvadersfx;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by tonysaavedra on 6/21/16.
 */
public class MenuBox extends Pane {
    private VBox box;

    public MenuBox(int width, int height) {
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.BLACK);

        Rectangle lineTop = new Rectangle(width, 2);
        lineTop.setFill(Color.rgb(21, 255, 42));
        lineTop.setStroke(Color.BLACK);

        Rectangle lineBot = new Rectangle(width, 2);
        lineBot.setTranslateY(height - 2);
        lineBot.setFill(Color.rgb(21, 255, 42));
        lineBot.setStroke(Color.BLACK);

        box = new VBox(5);
        box.setTranslateX(25);
        box.setTranslateY(25);

        getChildren().addAll(bg, lineTop, lineBot, box);
    }

    public void addItems(MenuItem... items) {
        for (MenuItem item : items)
            addItem(item);
    }

    public void addItem(MenuItem item) {
        box.getChildren().addAll(item);
    }
}
