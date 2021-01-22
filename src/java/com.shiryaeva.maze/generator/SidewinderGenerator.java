package com.shiryaeva.maze.generator;

import com.shiryaeva.maze.gui.Cell;
import com.shiryaeva.maze.gui.MainFrame;
import com.shiryaeva.maze.gui.MazeGridPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SidewinderGenerator {

    private final List<Cell> grid;
    private final List<Cell> run = new ArrayList<>();
    private final Random r = new Random();
    private final MazeGridPanel panel;
    private Cell current;
    private final Cell start;
    private final Cell goal;
    private int index;

    public SidewinderGenerator(List<Cell> grid, MazeGridPanel panel) {
        this.grid = grid;
        this.panel = panel;
        this.start = panel.getStart();
        this.goal = panel.getGoal();
        index = 0;
        current = grid.get(index);

    }

    public void generate() {
        final Timer timer = new Timer(MainFrame.speed, null);
        timer.addActionListener(e -> {
            if (!grid.parallelStream().allMatch(Cell::isVisited)) {
                carve();
            } else {
                current = null;
                MainFrame.setGenerated(true);
                timer.stop();
            }
            panel.setCurrent(current);
            panel.repaint();
            timer.setDelay(MainFrame.speed);
        });
        timer.start();
    }

    private void carve() {
        current.setVisited(true);
        goal.setVisited(true);
        Cell bottom = current.getBottomNeighbour(grid);
        Cell left = current.getLeftNeighbour(grid);

        if (left == null) {
            if (bottom != null) {
                current.removeWalls(bottom);
            }
        } else {
            run.add(current);
            if (bottom != null && r.nextBoolean()) {
                current.removeWalls(bottom);
            } else {
                current = run.get(r.nextInt(run.size()));
                left = current.getLeftNeighbour(grid);
                if (left != null) {
                    current.removeWalls(left);
                }
                run.clear();
            }
        }

        if (grid.size() - 1 >= index + 1) {
            current = grid.get(++index);
        }
    }
}