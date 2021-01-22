package com.shiryaeva.maze.generator;

import com.shiryaeva.maze.gui.Cell;
import com.shiryaeva.maze.gui.MainFrame;
import com.shiryaeva.maze.gui.MazeGridPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimGenerator {

    private final List<Cell> grid;
    private final List<Cell> frontier = new ArrayList<>();
    private final Cell start;
    private final Cell goal;
    private final MazeGridPanel panel;
    private Cell current;

    public PrimGenerator(List<Cell> grid, MazeGridPanel panel) {
        this.grid = grid;
        this.panel = panel;
        start = panel.getStart();
        goal = panel.getGoal();
        current = start;
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

        List<Cell> neighs = current.getUnvisitedNeighboursList(grid);
        frontier.addAll(neighs);
        Collections.shuffle(frontier);

        current = frontier.get(0);

        List<Cell> inNeighs = current.getAllNeighbours(grid);
        inNeighs.removeIf(c -> !c.isVisited());

        if (!inNeighs.isEmpty()) {
            Collections.shuffle(inNeighs);
            current.removeWalls(inNeighs.get(0));
        }

        frontier.removeIf(Cell::isVisited);
    }
}