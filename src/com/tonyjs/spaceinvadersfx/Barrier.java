package com.tonyjs.spaceinvadersfx;

import javafx.scene.paint.Color;

/**
 * Created by tonysaavedra on 6/20/16.
 */
public class Barrier {
    private int[][] barrier;
    private Color color;
    private double locationX;
    private double locationY;

    public Barrier() {
        this.barrier = new int[][]{
                {0, 0, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 0, 1, 1, 1},
                {1, 1, 0, 0, 0, 0, 0, 0, 1, 1}
        };
        this.color = Color.rgb(21, 255, 42);
    }

    public int[][] getBarrier() {
        return barrier;
    }

    public Color getColor() {
        return color;
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void deleteBricksAround(int row, int col) {
        barrier[row][col] = 0;
        if (row < barrier.length - 1) {
            barrier[row + 1][col] = 0;
            if (col < barrier[0].length - 1) {
                barrier[row][col + 1] = 0;
            }
            if (col > 0) {
                barrier[row][col - 1] = 0;
            }
        }
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }
}
