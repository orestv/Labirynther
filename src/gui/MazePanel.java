/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import Mazes.Maze;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author seth
 */
public class MazePanel extends JPanel {

    private Maze maze;

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        double nRatio = 1.4212;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (maze != null) {

            Rectangle rMazeSize = this.getBounds();
            rMazeSize.x += 2;
            rMazeSize.y += 2;
            int nProbableHeight = (int) ((rMazeSize.width - rMazeSize.x) / nRatio);
            if (rMazeSize.height - rMazeSize.y < nProbableHeight) {
                int nProbableWidth = (int) (rMazeSize.height - rMazeSize.y * nRatio);
                rMazeSize.width = nProbableWidth;
            } else {
                rMazeSize.height = nProbableHeight;
            }
            maze.paint(g, rMazeSize);
        }
    }
}
