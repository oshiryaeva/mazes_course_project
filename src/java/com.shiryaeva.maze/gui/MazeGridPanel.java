package com.shiryaeva.maze.gui;

import com.shiryaeva.maze.generator.GenerationAlgorithm;
import com.shiryaeva.maze.generator.KruskalGenerator;
import com.shiryaeva.maze.generator.PrimGenerator;
import com.shiryaeva.maze.generator.SidewinderGenerator;
import com.shiryaeva.maze.solver.LeeSolver;
import com.shiryaeva.maze.solver.OneHandSolver;
import com.shiryaeva.maze.solver.SolvingAlgorithm;
import com.shiryaeva.maze.util.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MazeGridPanel extends JPanel implements KeyListener, MouseListener, Serializable {

    private static final long serialVersionUID = 6899798390690377644L;
    private final List<Cell> grid = new ArrayList<>();
    private final Logger logger = Logger.getGlobal();
    private List<Cell> currentCells = new ArrayList<>();
    private int rows;
    private int cols;
    private Cell start;
    private Cell goal;
    private transient Cell manualCurrent;
    private transient Cell manualPrevious;
    private transient boolean clickable;
    private transient boolean manualSolve;

    public MazeGridPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setOpaque(false);
        createGrid();
        start = grid.get(0);
        manualCurrent = start;
        goal = grid.get(grid.size() - 1);
        this.clickable = MainFrame.manualEntranceExit;
        this.manualSolve = !MainFrame.autoSolve;
        addMouseListener(this);
        addKeyListener(this);
    }

    private boolean isOnEdge(Cell cell) {
        return cell.getX() == 0 || cell.getY() == 0 || cell.getX() == (rows - 1) || cell.getY() == (cols - 1);
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    private Cell getCellFromKeyboard(int c) {
        int tx = manualCurrent.getX(), ty = manualCurrent.getY();
        logger.log(Level.INFO, "manualCurrent: " + tx + " , " + ty);
        Cell candidate;
        switch (c) {
            case KeyEvent.VK_LEFT:
                tx--;
                break;
            case KeyEvent.VK_RIGHT:
                tx++;
                break;
            case KeyEvent.VK_UP:
                ty--;
                break;
            case KeyEvent.VK_DOWN:
                ty++;
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            default: {
                tx = 0;
                ty = 0;
                break;
            }
        }
        candidate = getCellByCoordinates(tx, ty);
        if (candidate != null && !isOutOfBorder(tx, ty) && !candidate.isDeadEnd() && manualCurrent.getValidMoveNeighbours(grid).contains(candidate)) {
            manualPrevious = manualCurrent;
            manualCurrent = candidate;
        }
        return manualCurrent;
    }

    private boolean isOutOfBorder(int x, int y) {
        return x > rows || y > cols || x < 0 || y < 0;
    }

    private Cell getCellByCoordinates(int x, int y) {
        return grid.stream()
                .filter(c -> (x == (c.getX()) && y == c.getY()))
                .findAny()
                .orElse(null);
    }

    private int getCellIdByCoordinates(int clickX, int clickY) {
        int totalX = this.getWidth();
        int totalY = this.getHeight();
        int cellWidth = totalX / rows;
        int cellHeight = totalY / cols;

        logger.log(Level.INFO, "totalY = " + totalY);
        logger.log(Level.INFO, "cellHeight = " + cellHeight);

        int cellPosX = 0;
        for (int i = cellWidth; i < totalX; i += cellWidth) {
            if (clickX < i) {
                logger.log(Level.INFO, "cellPosX = " + cellPosX);
                break;
            }
            cellPosX++;
        }
        int cellPosY = 0;
        for (int i = cellHeight; i < totalY; i += cellHeight) {
            if (clickY < i) {
                logger.log(Level.INFO, "cellPosY = " + cellPosY);
                break;
            }
            cellPosY++;
        }

        int cellId = 0;
        for (int i = 0; i <= cellPosX; i++) {
            if (cellPosX == 0)
                cellId = cellPosY;
            if (cellPosX == i)
                cellId = cellPosY + (cols * i);
        }
        logger.log(Level.INFO, "cellId = " + cellId);

        return cellId;
    }

    private void createGrid() {
        int rowCounter = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                Cell cell = new Cell(x, y);
                cell.setId(y + rowCounter);
                grid.add(cell);
            }
            rowCounter += rows;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MainFrame.WIDTH + 10, MainFrame.HEIGHT + 10);
    }

    public void generate(GenerationAlgorithm algorithm) {
        switch (algorithm) {
            case PRIM:
                new PrimGenerator(grid, this).generate();
                break;
            case KRUSKAL:
                new KruskalGenerator(grid, this).generate();
                break;
            case SIDEWINDER:
                new SidewinderGenerator(grid, this).generate();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown algorigthm: %s", algorithm));
        }
    }

    public void solve(SolvingAlgorithm algorithm) {
        switch (algorithm) {
            case LEE:
                new LeeSolver(grid, this).solve();
                break;
            case ONE_HAND:
                new OneHandSolver(grid, this).solve();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown algorigthm: %s", algorithm));
        }
    }

    public void setCurrent(Cell current) {
        if (currentCells.isEmpty()) {
            currentCells.add(current);
        } else {
            currentCells.set(0, current);
        }
    }

    public void setCurrentCells(List<Cell> currentCells) {
        this.currentCells = currentCells;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Cell c : grid) {
            c.draw(g);
        }
        for (Cell c : currentCells) {
            if (c != null) c.displayAsColor(g, ColorScheme.EXTRA_MEDIUM);
        }
        start.displayAsColor(g, ColorScheme.ENTRANCE);
        goal.displayAsColor(g, ColorScheme.EXIT);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    private void resetStart() {
        start.resetCell(start, getGraphics());
        this.start = null;
        revalidate();
    }

    public Cell getStart() {
        return start;
    }

    public void setStart(Cell start) {
        logger.log(Level.INFO, "start.id = " + start.getId());
        resetStart();
        this.start = start;
        manualCurrent = start;
        start.displayAsColor(getGraphics(), ColorScheme.ENTRANCE);
        revalidate();
    }

    public Cell getGoal() {
        return goal;
    }

    public void setGoal(Cell goal) {
        this.goal = goal;
        goal.displayAsColor(getGraphics(), ColorScheme.EXIT);
    }

    public void setAutoSolve(boolean auto) {
        this.clickable = !auto;
        manualSolve = !auto;
    }

    public Cell getManualCurrent() {
        return manualCurrent;
    }

    public void setManualCurrent(Cell manualCurrent) {
        this.manualCurrent = manualCurrent;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!MainFrame.autoSolve) {
            int c = e.getKeyCode();
            manualCurrent.setVisited(true);
            Cell candidate = getCellFromKeyboard(c);
            if (candidate.equals(manualPrevious)) {
                candidate.displayAsColor(getGraphics(), ColorScheme.TRANSPARENT);
                validate();
            }
            manualPrevious = manualCurrent;
            manualCurrent = candidate;
            if (manualCurrent.getColor() != ColorScheme.PATH) {
                manualCurrent.displayAsColor(getGraphics(), ColorScheme.PATH);
                validate();
            }
            if (manualCurrent.equals(goal)) {
                JOptionPane.showMessageDialog(null, "Выход найден", "Поздравляем", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (clickable && !MainFrame.generated) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Cell startCandidate = grid.get(getCellIdByCoordinates(e.getX(), e.getY()));
                if (isOnEdge(startCandidate))
                    setStart(startCandidate);
            }
            if (SwingUtilities.isRightMouseButton(e)) {
                Cell goalCandidate = grid.get(getCellIdByCoordinates(e.getX(), e.getY()));
                if (isOnEdge(goalCandidate))
                    setGoal(goalCandidate);
            }
        }
        if (clickable && manualSolve) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Cell candidate = grid.get(getCellIdByCoordinates(e.getX(), e.getY()));
                if (candidate.equals(manualPrevious)) {
                    candidate.displayAsColor(getGraphics(), ColorScheme.TRANSPARENT);
                    validate();
                }
                if (!candidate.isDeadEnd() && manualCurrent.getAllNeighbours(grid).contains(candidate) && manualCurrent.getValidMoveNeighbours(grid).contains(candidate)) {
                    manualPrevious = manualCurrent;
                    manualCurrent = candidate;
                }
                manualCurrent.setVisited(true);
                manualCurrent = grid.get(getCellIdByCoordinates(e.getX(), e.getY()));
                if (manualCurrent.getColor() != ColorScheme.PATH) {
                    manualCurrent.displayAsColor(getGraphics(), ColorScheme.PATH);
                    validate();
                }
                if (manualCurrent.equals(goal)) {
                    JOptionPane.showMessageDialog(null, "Выход найден", "Поздравляем", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public List<Cell> getGrid() {
        return grid;
    }

}
