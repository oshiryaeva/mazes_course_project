package com.shiryaeva.maze.util;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private Image img;

    public ImagePanel(String img) {
        this(new ImageIcon(img).getImage());
    }

    public ImagePanel() {
        this.img = new ImageIcon(HobbitTheme.SHIRE.getBackground()).getImage();
    }

    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(new BorderLayout());
    }

    public ImagePanel(Image img, Dimension size) {
        this.img = img.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }

    public void setImg(String path) {
        this.img = new ImageIcon(path).getImage();
        repaint();
    }

}
