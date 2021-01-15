package com.shiryaeva.maze.generator;

import com.shiryaeva.maze.gui.Cell;
import com.shiryaeva.maze.gui.MainFrame;
import com.shiryaeva.maze.gui.MazeGridPanel;

import javax.swing.Timer;
import java.util.*;

public class KruskalGenerator {

    private final Stack<Cell> stack = new Stack<>();
    private final DisjointSets disjointSet = new DisjointSets();
    private final List<Cell> grid;
    private final MazeGridPanel panel;
    private Cell current;

    public KruskalGenerator(List<Cell> grid, MazeGridPanel panel) {
        this.grid = grid;
        this.panel = panel;
        Cell start = panel.getStart();
        Cell goal = grid.get(grid.size() - 1);
        current = start;
    }

    public void generate() {
        for (Cell cell : grid) {
            disjointSet.create_set(cell.getId());
        }

        stack.addAll(grid);

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
        current = stack.pop();
        current.setVisited(true);

        List<Cell> neighbours = current.getAllNeighbours(grid);

        for (Cell n : neighbours) {
            if (disjointSet.findSet(current.getId()) != disjointSet.findSet(n.getId())) {
                current.removeWalls(n);
                disjointSet.union(current.getId(), n.getId());
            }
        }

        Collections.shuffle(stack);
    }

    static class DisjointSets {

        private final List<Map<Integer, Set<Integer>>> disjointSet;

        public DisjointSets() {
            disjointSet = new ArrayList<>();
        }

        public void create_set(int element) {
            Map<Integer, Set<Integer>> map = new HashMap<>();
            Set<Integer> set = new HashSet<>();

            set.add(element);
            map.put(element, set);

            disjointSet.add(map);
        }

        public void union(int first, int second) {

            int firstRep = findSet(first);
            int secondRep = findSet(second);

            Set<Integer> firstSet = null;
            Set<Integer> secondSet = null;

            for (Map<Integer, Set<Integer>> map : disjointSet) {
                if (map.containsKey(firstRep)) {
                    firstSet = map.get(firstRep);
                } else if (map.containsKey(secondRep)) {
                    secondSet = map.get(secondRep);
                }
            }

            if (firstSet != null && secondSet != null)
                firstSet.addAll(secondSet);

            for (int index = 0; index < disjointSet.size(); index++) {

                Map<Integer, Set<Integer>> map = disjointSet.get(index);

                if (map.containsKey(firstRep)) {
                    map.put(firstRep, firstSet);
                } else if (map.containsKey(secondRep)) {
                    map.remove(secondRep);
                    disjointSet.remove(index);
                }
            }
        }

        public int findSet(int element) {
            for (Map<Integer, Set<Integer>> map : disjointSet) {
                Set<Integer> keySet = map.keySet();

                for (Integer key : keySet) {
                    Set<Integer> set = map.get(key);
                    if (set.contains(element)) {
                        return key;
                    }
                }
            }
            return -1;
        }

    }
}