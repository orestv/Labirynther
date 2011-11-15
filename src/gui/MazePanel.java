/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import Mazes.Maze;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author seth
 */
public class MazePanel extends JPanel{
    private Maze maze;

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (maze != null)
            maze.paint(g, this.getSize());
    }
    
    
    
}
