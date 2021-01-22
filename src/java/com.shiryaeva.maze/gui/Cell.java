package com.shiryaeva.maze.gui;

import com.shiryaeva.maze.util.ColorScheme;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Cell implements Serializable {

    private static final long serialVersionUID = -7047140074941068977L;
    private static final int TOP = 0;
    private static final int RIGHT = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT = 3;
    private final int x;
    private final int y;
    private int distance;
    private int id;
    private Cell parent;
    private Color color;
    private boolean visited = false;
    private boolean path = false;
    private boolean deadEnd = false;
    private boolean[] walls = {true, true, true, true};

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.distance = -1;

    }

    public boolean[] getWalls() {
        return walls;
    }

    public void setWalls(boolean[] walls) {
        this.walls = walls;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isDeadEnd() {
        return deadEnd;
    }

    public void setDeadEnd(boolean deadEnd) {
        this.deadEnd = deadEnd;
    }

    public boolean isPath() {
        return path;
    }

    public void setPath(boolean path) {
        this.path = path;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Cell getParent() {
        return parent;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public void resetCell(Cell cell, Graphics g) {
        cell.setDeadEnd(false);
        cell.setPath(false);
        cell.setVisited(false);
        cell.draw(g);
    }

    synchronized public void draw(Graphics g) {
        int x2 = x * MainFrame.cellWidth;
        int y2 = y * MainFrame.cellHeight;

        if (visited) {
            this.setColor(ColorScheme.VISITED);
            g.setColor(ColorScheme.VISITED);
            g.fillRect(x2, y2, MainFrame.cellWidth, MainFrame.cellHeight);
        }

        if (path) {
            this.setColor(ColorScheme.PATH);
            g.setColor(ColorScheme.PATH);
            g.fillRect(x2, y2, MainFrame.cellWidth, MainFrame.cellHeight);
        } else if (deadEnd) {
            this.setColor(ColorScheme.DEAD_END);
            g.setColor(ColorScheme.DEAD_END);
            g.fillRect(x2, y2, MainFrame.cellWidth, MainFrame.cellHeight);
        }

        g.setColor(ColorScheme.EXTRA_LIGHT);
        this.setColor(ColorScheme.EXTRA_LIGHT);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        if (walls[TOP]) {
            g2.drawLine(x2, y2, x2 + MainFrame.cellWidth, y2);
        }
        if (walls[RIGHT]) {
            g2.drawLine(x2 + MainFrame.cellWidth, y2, x2 + MainFrame.cellWidth, y2 + MainFrame.cellHeight);
        }
        if (walls[BOTTOM]) {
            g2.drawLine(x2 + MainFrame.cellWidth, y2 + MainFrame.cellHeight, x2, y2 + MainFrame.cellHeight);
        }
        if (walls[LEFT]) {
            g2.drawLine(x2, y2 + MainFrame.cellHeight, x2, y2);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    synchronized public void displayAsColor(Graphics g, Color color) {
        int x2 = x * MainFrame.cellWidth;
        int y2 = y * MainFrame.cellHeight;
        g.setColor(color);
        this.setColor(color);
        g.fillRect(x2, y2, MainFrame.cellWidth, MainFrame.cellHeight);
//        Toolkit.getDefaultToolkit().sync();
    }

/*    synchronized public void displayTransparent(Graphics g) {
        int x2 = x * MainFrame.cellWidth;
        int y2 = y * MainFrame.cellHeight;
        g.setColor(ColorScheme.TRANSPARENT);
        this.setColor(ColorScheme.TRANSPARENT);
        g.fillRect(x2, y2, MainFrame.cellWidth, MainFrame.cellHeight);
        Toolkit.getDefaultToolkit().sync();
    }*/

    synchronized public void removeWalls(Cell next) {
        int x = this.x - next.x;

        if (x == 1) {
            walls[LEFT] = false;
            next.walls[RIGHT] = false;
        } else if (x == -1) {
            walls[RIGHT] = false;
            next.walls[LEFT] = false;
        }

        int y = this.y - next.y;

        if (y == 1) {
            walls[TOP] = false;
            next.walls[BOTTOM] = false;
        } else if (y == -1) {
            walls[BOTTOM] = false;
            next.walls[TOP] = false;
        }
    }

    private Cell randomNeighbour(List<Cell> neighbours) {
        if (!neighbours.isEmpty()) {
            return neighbours.get(new Random().nextInt(neighbours.size()));
        }
        return null;
    }

    private Cell checkNeighbourInGridBounds(List<Cell> grid, Cell neighbour) {
        if (grid.contains(neighbour)) {
            return grid.get(grid.indexOf(neighbour));
        }
        return null;
    }

/*    public Cell getUnvisitedNeighbour(List<Cell> grid) {

        List<Cell> neighbours = getUnvisitedNeighboursList(grid);

        if (neighbours.size() == 1) {
            return neighbours.get(0);
        }
        return randomNeighbour(neighbours);
    }*/

    public List<Cell> getUnvisitedNeighboursList(List<Cell> grid) {

        List<Cell> neighbours = new ArrayList<>(4);

        Optional<Cell> top = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x, y - 1)));
        Optional<Cell> right = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x + 1, y)));
        Optional<Cell> bottom = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x, y + 1)));
        Optional<Cell> left = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x - 1, y)));

        if (top.isPresent() && !top.get().isVisited())
            neighbours.add(top.get());
        if (right.isPresent() && !right.get().isVisited())
            neighbours.add(right.get());
        if (bottom.isPresent() && !bottom.get().isVisited())
            neighbours.add(bottom.get());
        if (left.isPresent() && !left.get().isVisited())
            neighbours.add(left.get());

        return neighbours;
    }

    public List<Cell> getValidMoveNeighbours(List<Cell> grid) {
        List<Cell> neighbours = new ArrayList<>(4);

        Optional<Cell> top = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x, y - 1)));
        Optional<Cell> right = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x + 1, y)));
        Optional<Cell> bottom = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x, y + 1)));
        Optional<Cell> left = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x - 1, y)));

        if (top.isPresent()) {
            if (!walls[TOP]) neighbours.add(top.get());
        }

        if (right.isPresent()) {
            if (!walls[RIGHT]) neighbours.add(right.get());
        }

        if (bottom.isPresent()) {
            if (!walls[BOTTOM]) neighbours.add(bottom.get());
        }

        if (left.isPresent()) {
            if (!walls[LEFT]) neighbours.add(left.get());
        }

        return neighbours;
    }

    public Cell touchIt(List<Cell> grid) {
        Optional<Cell> top = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x, y - 1)));
        Optional<Cell> right = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x + 1, y)));
        Optional<Cell> bottom = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x, y + 1)));
        Optional<Cell> left = Optional.ofNullable(checkNeighbourInGridBounds(grid, new Cell(x - 1, y)));

        if (right.isPresent() && getValidMoveNeighbours(grid).contains(right.get()) && !right.get().isDeadEnd()) {
            return right.get();
        } else if (top.isPresent() && getValidMoveNeighbours(grid).contains(top.get()) && !top.get().isDeadEnd()) {
            return top.get();
        } else if (left.isPresent() && getValidMoveNeighbours(grid).contains(left.get()) && !left.get().isDeadEnd()) {
            return left.get();
        } else if (bottom.isPresent() && getValidMoveNeighbours(grid).contains(bottom.get()) && !bottom.get().isDeadEnd()) {
            return bottom.get();
        }
        return null;
    }

/*    public Cell getPathOrLeftNeighbour(List<Cell> grid) {
        Cell top = checkNeighbourInGridBounds(grid, new Cell(x, y - 1));
        Cell right = checkNeighbourInGridBounds(grid, new Cell(x + 1, y));
        Cell bottom = checkNeighbourInGridBounds(grid, new Cell(x, y + 1));
        Cell left = checkNeighbourInGridBounds(grid, new Cell(x - 1, y));
        Cell next = bottom;

        if (bottom != null) {
            if (!bottom.deadEnd) {
                if (!walls[BOTTOM])
                    next = bottom;
            } else
                next = left;
        }

        if (left != null) {
            if (!left.deadEnd) {
                next = left;
            } else
                next = top;
        }

        if (top != null) {
            if (!top.deadEnd) {
                next = top;
            } else
                next = right;
        }


        if (right != null) {
            if (!right.deadEnd) {
                if (!walls[RIGHT])
                    next = right;
            } else
                next = bottom;
        }

        return next;
    }*/

    public Cell getPathNeighbour(List<Cell> grid) {
        List<Cell> neighbours = new ArrayList<>();

        Cell top = checkNeighbourInGridBounds(grid, new Cell(x, y - 1));
        Cell right = checkNeighbourInGridBounds(grid, new Cell(x + 1, y));
        Cell bottom = checkNeighbourInGridBounds(grid, new Cell(x, y + 1));
        Cell left = checkNeighbourInGridBounds(grid, new Cell(x - 1, y));

        if (top != null && !top.deadEnd && !walls[TOP])
            neighbours.add(top);

        if (right != null && !right.deadEnd && !walls[RIGHT])
            neighbours.add(right);

        if (bottom != null && !bottom.deadEnd && !walls[BOTTOM])
            neighbours.add(bottom);

        if (left != null && !left.deadEnd && !walls[LEFT])
            neighbours.add(left);

        if (neighbours.size() == 1) {
            return neighbours.get(0);
        }

        return randomNeighbour(neighbours);
    }

    public List<Cell> getAllNeighbours(List<Cell> grid) {
        List<Cell> neighbours = new ArrayList<>();

        Cell top = checkNeighbourInGridBounds(grid, new Cell(x, y - 1));
        Cell right = checkNeighbourInGridBounds(grid, new Cell(x + 1, y));
        Cell bottom = checkNeighbourInGridBounds(grid, new Cell(x, y + 1));
        Cell left = checkNeighbourInGridBounds(grid, new Cell(x - 1, y));

        if (top != null) neighbours.add(top);
        if (right != null) neighbours.add(right);
        if (bottom != null) neighbours.add(bottom);
        if (left != null) neighbours.add(left);

        return neighbours;
    }

/*    public Cell getTopNeighbour(List<Cell> grid) {
        return checkNeighbourInGridBounds(grid, new Cell(x, y - 1));
    }*/

/*    public Cell getRightNeighbour(List<Cell> grid) {
        return checkNeighbourInGridBounds(grid, new Cell(x + 1, y));
    }*/

    public Cell getBottomNeighbour(List<Cell> grid) {
        return checkNeighbourInGridBounds(grid, new Cell(x, y + 1));
    }

    public Cell getLeftNeighbour(List<Cell> grid) {
        return checkNeighbourInGridBounds(grid, new Cell(x - 1, y));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cell other = (Cell) obj;
        if (x != other.x)
            return false;
        return y == other.y;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}