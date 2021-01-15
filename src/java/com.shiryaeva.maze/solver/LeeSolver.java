package com.shiryaeva.maze.solver;

import com.shiryaeva.maze.gui.Cell;
import com.shiryaeva.maze.gui.MainFrame;
import com.shiryaeva.maze.gui.MazeGridPanel;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LeeSolver {

    private final Queue<Cell> queue = new LinkedList<>();
    private final List<Cell> grid;
    private final MazeGridPanel panel;
    private final Cell start;
    private final Cell goal;
    private Cell current;

    public LeeSolver(List<Cell> grid, MazeGridPanel panel) {
        this.grid = grid;
        this.panel = panel;
        start = panel.getStart();
        goal = panel.getGoal();
        current = start;
        current.setDistance(0);
        queue.offer(current);
    }

    public void solve() {
        final Timer timer = new Timer(MainFrame.speed, null);
        timer.addActionListener(e -> {
            if (!current.equals(goal)) {
                flood();
            } else {
                drawPath();
                MainFrame.setSolved(true);
                timer.stop();
            }
            panel.setCurrent(current);
            panel.repaint();
            timer.setDelay(MainFrame.speed);
        });
        timer.start();
    }

    private void flood() {
        current.setDeadEnd(true);
        current = queue.poll();
        List<Cell> adjacentCells = current.getValidMoveNeighbours(grid);
        for (Cell c : adjacentCells) {
            if (c.getDistance() == -1) {
                c.setDistance(current.getDistance() + 1);
                c.setParent(current);
                queue.offer(c);
            }
        }
    }

    private void drawPath() {
        while (current != start) {

//            while (current != grid.get(0) || current != start || current != goal) {
            current.setPath(true);
            current = current.getParent();
        }
    }
}