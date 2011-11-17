/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import Mazes.Maze;
import Mazes.Maze.MazeException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author seth
 */
public class JMainWindow extends JFrame implements ActionListener{

    private JSpinner spColumns;
    private MazePanel pMaze;
    
    public JMainWindow() throws HeadlessException {
        super();        
        this.center();
        this.init();
    }
    
    private void init() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel p = new JPanel(new MigLayout());
        
        pMaze = new MazePanel();
        JPanel pControls = new JPanel(new MigLayout());
        spColumns = new JSpinner();
        ((SpinnerNumberModel)(spColumns.getModel())).setMinimum(10);
        ((SpinnerNumberModel)(spColumns.getModel())).setMaximum(99);
        ((JFormattedTextField)((JSpinner.NumberEditor)spColumns.getEditor()).getTextField()).setColumns(2);
        spColumns.setValue(45);
        
        JButton btnStart = new JButton("Generate");
        btnStart.addActionListener(this);
        
        
        p.add(pMaze, "w 80%, h 100%");
        
        pControls.add(new JLabel("Number of columns:"));
        pControls.add(spColumns, "wrap");
        pControls.add(btnStart, "span, growx");
        
        p.add(pControls, "w 20%, h 100%");        
        
        this.add(p);
        this.setVisible(true);
    }
    
    private void center() {
        Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(3*dScreen.width/4, 3*dScreen.height/4);
        Dimension dThis = this.getSize();
        this.setLocation((dScreen.width-dThis.width)/2, (dScreen.height-dThis.height)/2);
    }
    
    private void generateMaze() {
        int nMazeWidth = (Integer)spColumns.getValue();
        int nMazeHeight = (int) (nMazeWidth * 1.4212);
        int nCanvasWidth = pMaze.getWidth();
        int nCanvasHeight = pMaze.getHeight();
        //int nMazeHeight = (int) (nCanvasHeight * ((double)nMazeWidth/nCanvasWidth));
        try {
            Maze m = new Maze(nMazeWidth, nMazeHeight);
            pMaze.setMaze(m);
            
        } catch (MazeException ex) {
            Logger.getLogger(JMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        generateMaze();
    }
}
