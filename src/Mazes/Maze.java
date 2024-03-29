/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Mazes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author ovoloshchuk
 */
public class Maze implements Printable{

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        if (pageIndex > 0)
            return NO_SUCH_PAGE;
        Graphics2D g = (Graphics2D)graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        this.paint(g, new Rectangle((int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight()));
        return PAGE_EXISTS;
    }

    public static class MazeException extends Exception {

        public MazeException(String message) {
            super(message);
        }
    }

    private enum Direction {

        RIGHT {

            @Override
            public Direction opposite() {
                return LEFT;
            }

            @Override
            public Point move(Point pt) {
                return new Point(pt.x + 1, pt.y);
            }
        },
        LEFT {

            @Override
            public Direction opposite() {
                return RIGHT;
            }

            @Override
            public Point move(Point pt) {
                return new Point(pt.x - 1, pt.y);
            }
        },
        UP {

            @Override
            public Direction opposite() {
                return DOWN;
            }

            @Override
            public Point move(Point pt) {
                return new Point(pt.x, pt.y - 1);
            }
        },
        DOWN {

            @Override
            public Direction opposite() {
                return UP;
            }

            @Override
            public Point move(Point pt) {
                return new Point(pt.x, pt.y + 1);
            }
        };

        public abstract Direction opposite();

        public abstract Point move(Point pt);
    }

    private static class Cell {

        private EnumMap<Direction, Boolean> walls;

        public Cell() {
            walls = new EnumMap<Direction, Boolean>(Direction.class);
            for (Direction d : Direction.values()) {
                walls.put(d, Boolean.TRUE);
            }
        }

        public boolean hasWall(Direction d) {
            return walls.get(d);
        }

        public void setWall(Direction d, boolean bWall) {
            walls.put(d, bWall);
        }
    }
    private int width = 0;
    private int height = 0;
    int nCellCount = 0;
    private Cell[][] cells = null;
    private ProcessHandler handler = null;
    
    public static interface ProcessHandler {
        public void generationUpdate(int nCellsGenerated, int nTotalCells);
    }

    public Maze(int width, int height) throws MazeException {
        init(width, height);

        generate();
    }
    
    public Maze(int width, int height, ProcessHandler handler) throws MazeException {
        init (width, height);
        this.handler = handler;
        generate();
    }
    
    private void init (int width, int height) throws MazeException {
        if (width <= 0 || height <= 0) {
            throw new MazeException(String.format("Invalid maze initialization arguments: {0}x{1} specified for width*height", width, height));
        }

        this.width = width;
        this.height = height;
        cells = new Cell[width][height];

        for (Cell[] col : cells) {
            for (int i = 0; i < col.length; i++) {
                col[i] = new Cell();
            }
        }
    }

    private void generate() throws MazeException {
        nCellCount = width * height;
        if (nCellCount < 0) {
            throw new MazeException("Maze too large, cell count overflow!");
        }

        Point pt = pickEntryPoint(this.width, this.height);
        setWall(pt, Direction.LEFT, false);
        ArrayList<Point> lsVisitedCells = new ArrayList<Point>(nCellCount);
        lsVisitedCells.add(pt);
        if (this.handler != null)
            this.handler.generationUpdate(1, nCellCount);

        while (lsVisitedCells.size() < nCellCount) {
            pt = pickRandomItem(lsVisitedCells);

            generatePath(pt, lsVisitedCells);
        }
        setWall(pickExitPoint(width, height), Direction.RIGHT, false);
    }

    private void generatePath(Point ptStart, ArrayList<Point> lsVisitedCells) throws MazeException {
        Point ptCurrent = ptStart;
        ArrayList<Direction> lsAvailableDirections = null;
        Direction currentDir = null;
        while (!(lsAvailableDirections = (ArrayList<Direction>) getAvailableDirections(ptCurrent.x, ptCurrent.y, currentDir, lsVisitedCells)).isEmpty()) {
            currentDir = pickRandomItem(lsAvailableDirections);

            setWall(ptCurrent, currentDir, false);

            ptCurrent = currentDir.move(ptCurrent);
            lsVisitedCells.add(ptCurrent);
            this.handler.generationUpdate(lsVisitedCells.size(), nCellCount);            
        }
    }

    private static <T> T pickRandomItem(List<T> ls) {
        int nIndex = (int) (Math.random() * ls.size());
        return ls.get(nIndex);
    }

    private List<Direction> getAvailableDirections(int nCol, int nRow, Direction originalDirection, ArrayList<Point> lsVisitedCells) throws MazeException {
        if (nCol < 0 || nCol >= width || nRow < 0 || nRow >= height) {
            throw new MazeException(String.format("Invalid getAvailableDirections call: {0}/{1} specified for col/row, but maze dimensions are {2}/{3}", nCol, nRow, width, height));
        }

        ArrayList<Direction> result = new ArrayList<Direction>();

        //Can we go up?
        if (nRow > 0
                && originalDirection != Direction.DOWN
                && !lsVisitedCells.contains(new Point(nCol, nRow - 1))) {
            result.add(Direction.UP);
        }

        if (nRow < this.height - 1
                && originalDirection != Direction.UP
                && !lsVisitedCells.contains(new Point(nCol, nRow + 1))) {
            result.add(Direction.DOWN);
        }

        if (nCol > 0
                && originalDirection != Direction.RIGHT
                && !lsVisitedCells.contains(new Point(nCol - 1, nRow))) {
            result.add(Direction.LEFT);
        }

        if (nCol < this.width - 1
                && originalDirection != Direction.LEFT
                && !lsVisitedCells.contains(new Point(nCol + 1, nRow))) {
            result.add(Direction.RIGHT);
        }

        return result;
    }

    private static Point pickEntryPoint(int width, int height) {
        return new Point(0, (int) (Math.random() * height));
    }

    private static Point pickExitPoint(int width, int height) {
        return new Point(width - 1, (int) (Math.random() * height));
    }

    private boolean hasWall(Point pt, Direction direction) throws MazeException {
        if (pt.x < 0 || pt.x >= width || pt.y < 0 || pt.y >= height) {
            throw new MazeException(String.format("Invalid hasWall call: {0}/{1} specified for col/row, but maze dimensions are {2}/{3}", pt.x, pt.y, width, height));
        }

        return cells[pt.x][pt.y].hasWall(direction);
    }

    private void setWall(Point pt, Direction direction, boolean bWall) throws MazeException {
        if (pt.x < 0 || pt.x >= width || pt.y < 0 || pt.y >= height) {
            throw new MazeException(String.format("Invalid setWall call: {0}/{1} specified for pt.x/pt.y, but maze dimensions are {2}/{3}", pt.x, pt.y, width, height));
        }

        cells[pt.x][pt.y].setWall(direction, bWall);

        pt = direction.move(pt);
        if (pt.x >= 0 && pt.x < this.width && pt.y >= 0 && pt.y < this.height) {
            cells[pt.x][pt.y].setWall(direction.opposite(), bWall);
        }
    }

    public void paint(Graphics g, Rectangle bounds) {
        Color clLine = Color.black;
        Color clBackground = Color.white;

        g.setColor(clBackground);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setColor(clLine);

        int nCellSize = (int) Math.min((bounds.width-bounds.x - 1) / (double)this.width, 
                (bounds.height-bounds.y - 1) / (double)this.height);

        for (int nCol = 0; nCol < this.width; nCol++) {
            for (int nRow = 0; nRow < this.height; nRow++) {
                Cell c = cells[nCol][nRow];

                int x_NW = bounds.x + nCol * nCellSize, y_NW = bounds.y + nRow * nCellSize;       //NorthWest
                int x_NE = bounds.x + (nCol + 1) * nCellSize, y_NE = bounds.y + nRow * nCellSize;   //NorthEast
                int x_SW = bounds.x + nCol * nCellSize, y_SW = bounds.y + (nRow + 1) * nCellSize;   //SouthWest
                int x_SE = bounds.x + (nCol + 1) * nCellSize, y_SE = bounds.y + (nRow + 1) * nCellSize;   //SouthEast

                for (Direction d : Direction.values()) {
                    if (c.hasWall(d)) {
                        switch (d) {
                            case LEFT:
                                g.drawLine(x_NW, y_NW, x_SW, y_SW);
                                break;
                            case RIGHT:
                                g.drawLine(x_NE, y_NE, x_SE, y_SE);
                                break;
                            case UP:
                                g.drawLine(x_NW, y_NW, x_NE, y_NE);
                                break;
                            case DOWN:
                                g.drawLine(x_SW, y_SW, x_SE, y_SE);
                                break;
                        }
                    }
                }
            }
        }
    }
}
