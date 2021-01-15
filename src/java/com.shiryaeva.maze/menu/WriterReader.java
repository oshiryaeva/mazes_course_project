package com.shiryaeva.maze.menu;

import com.shiryaeva.maze.gui.MainFrame;
import com.shiryaeva.maze.gui.MazeGridPanel;

import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriterReader {

    protected static final String EXTENSION = ".txt";
    protected static final String DESCRIPTION = "TXT files";

    private final Logger logger = Logger.getGlobal();

    public void writeFile(JMenuItem item, Object maze) {
        if (MainFrame.generated) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String path = f.getAbsolutePath().toLowerCase();
                    return path.endsWith(EXTENSION);
                }

                @Override
                public String getDescription() {
                    return DESCRIPTION;
                }
            });
            int status = fileChooser.showSaveDialog(item);

            if (status == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try {
                    String fileName = selectedFile.getCanonicalPath();
                    if (!fileName.endsWith(EXTENSION)) {
                        selectedFile = new File(fileName + EXTENSION);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(selectedFile);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(maze);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Сначала необходимо создать лабиринт", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

    }

    public MazeGridPanel readFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите папку");
        logger.log(Level.INFO, "File chooser created");
        int result = fileChooser.showOpenDialog(fileChooser.getRootPane());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            logger.log(Level.INFO, "File: " + file.getName());
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                MazeGridPanel maze = (MazeGridPanel) objectInputStream.readObject();
                logger.log(Level.INFO, "Maze = " + maze.toString());
                objectInputStream.close();
                return maze;
            } catch (ClassNotFoundException | IOException e) {
                logger.log(Level.SEVERE, e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }

}
