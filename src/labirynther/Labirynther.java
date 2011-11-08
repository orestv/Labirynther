/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package labirynther;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import Mazes.Maze;

/**
 *
 * @author ovoloshchuk
 */
public class Labirynther {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int width = 1024, height = 768;
        
        int nRows = 88;
        int nCols = (int) (nRows/((double)width/height));
        
        int labWidth = nRows, labHeight = nCols;

        Maze lab = new Maze(labWidth, labHeight);
        
        int actualWidth = width - (width % labWidth);
        int actualHeight = height - (height % labHeight);

        BufferedImage image = new BufferedImage(actualWidth, actualHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        Rectangle R = new Rectangle(width, height);

        Maze.paint(lab, g);

        try {
            ImageIO.write(image, "png", new File("result.png"));
        } catch (IOException ex) {
            Logger.getLogger(Labirynther.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
