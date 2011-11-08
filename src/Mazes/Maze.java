/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Mazes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author ovoloshchuk
 */
public class Maze {

    private static class Node {

        private boolean bWallDown = true;
        private boolean bWallRight = true;

        public Node() {
        }

        public boolean hasWallDown() {
            return bWallDown;
        }

        public boolean hasWallRight() {
            return bWallRight;
        }

        public void setWallDown(boolean bWallDown) {
            this.bWallDown = bWallDown;
        }

        public void setWallRight(boolean bWallRight) {
            this.bWallRight = bWallRight;
        }
    }
    private int width = 0;
    private int height = 0;
    private Node[][] nodes = null;

    private enum Direction {

        LEFT,
        RIGHT,
        UP,
        DOWN;
        private static final Direction[] dirs = {LEFT, RIGHT, UP, DOWN};
        
        private Direction opposite(){
            switch(this) {
                case RIGHT:
                    return LEFT;
                case LEFT:
                    return RIGHT;
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
            }
            return this;
        }

        public Direction next() {
            Direction result = this;
            do {
                result = dirs[(int) (Math.random() * 4)];
            } while (result == this.opposite());
            return result;
        }
    }

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;

        nodes = new Node[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                nodes[row][col] = new Node();
            }
        }
        
        int row = (int) (Math.random()*height)+1;
        int col = 1;
        Direction dir = Direction.LEFT;
        
        while (row >= 0 && col >= 0 && row <= height && col <= width) {
            dir = dir.next();
            switch(dir) {
                case RIGHT:
                    nodes[row][col+1].setWallDown(false);
                    col++;
                    break;
                case UP:
                    nodes[row][col].setWallRight(false);
                    row--;
                    break;
                case LEFT:
                    nodes[row][col].setWallDown(false);
                    col--;
                    break;
                case DOWN:
                    nodes[row+1][col].setWallRight(false);
                    row++;
                    break;
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean hasWallRight(int row, int col) {
        return nodes[row][col].hasWallRight();
    }

    public boolean hasWallDown(int row, int col) {
        return nodes[row][col].hasWallDown();
    }

    public static void paint(Maze maze, Graphics2D g) {
        Color clrBackground = new Color(240, 240, 240);
        Color clrLine = Color.BLACK;

        Rectangle bounds = g.getDeviceConfiguration().getBounds();
        Rectangle walls = (Rectangle) bounds.clone();
        walls.height--;
        walls.width--;

        g.setColor(clrBackground);
        g.fill(bounds);

        g.setColor(clrLine);
        g.draw(walls);

        double stepX, stepY;
        stepX = bounds.getWidth() / (maze.getWidth() - 1);
        stepY = bounds.getHeight() / (maze.getHeight() - 1);

        g.setColor(clrLine);
        for (int row = 0; row < maze.getHeight(); row++) {
            for (int col = 0; col < maze.getWidth(); col++) {
                boolean bDown = maze.hasWallDown(row, col);
                boolean bRight = maze.hasWallRight(row, col);

                if (bDown) {
                    g.drawLine(col * (int) stepX, row * (int) stepY, col * (int) stepX, (row + 1) * (int) stepY);
                }

                if (bRight) {
                    g.drawLine(col * (int) stepX, row * (int) stepY, (col + 1) * (int) stepX, row * (int) stepY);
                }
            }
        }
    }
}
