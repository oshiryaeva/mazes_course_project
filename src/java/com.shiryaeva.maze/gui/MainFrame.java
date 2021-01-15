package com.shiryaeva.maze.gui;

import com.shiryaeva.maze.generator.GenerationAlgorithm;
import com.shiryaeva.maze.menu.WriterReader;
import com.shiryaeva.maze.solver.SolvingAlgorithm;
import com.shiryaeva.maze.util.ColorScheme;
import com.shiryaeva.maze.util.HobbitTheme;
import com.shiryaeva.maze.util.ImagePanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    private static final int DEFAULT_GRID = 7;
    public static int cellWidth = 20;
    public static int cellHeight = 20;
    public static int speed = 50;
    public static boolean gridDisplayed;
    public static boolean manualEntranceExit;
    public static boolean generated;
    public static boolean solved;
    public static boolean autoSolve;
    private final Logger logger = Logger.getGlobal();
    private MazeGridPanel grid;
    private ImagePanel mazeBorder;
    private int cols;
    private int rows;
    private JSpinner rowsSpinner;
    private JSpinner colsSpinner;
    private JPanel genAlgPanel;
    private JLabel genAlgLabel;
    private JComboBox<GenerationAlgorithm> generationAlgorithmBox;
    private JPanel entranceExitSettingsPanel;
    private JLabel entExitLabel;
    private JRadioButton exitAutoRadioButton;
    private JRadioButton exitManualRadioButton;
    private JButton createMazeButton;
    private JLabel solveModeLabel;
    private JRadioButton autoSolveRadioButton;
    private JRadioButton manualSolveRadioButton;
    private JLabel solveAlgLabel;
    private JComboBox<SolvingAlgorithm> solveAlgBox;
    private JPanel visualizationPanel;
    private JLabel visualSpeed;
    private JSlider visualizationSlider;
    private JButton solveMazeButton;

    public MainFrame() {
        cellWidth = Math.floorDiv(WIDTH, DEFAULT_GRID);
        cellHeight = Math.floorDiv(HEIGHT, DEFAULT_GRID);
        setRows(DEFAULT_GRID);
        setCols(DEFAULT_GRID);
        autoSolve = true;

        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }
            createAndShowGUI();
        });

    }

    public static void main(String[] args) {
        new MainFrame();
    }

    public static void setGenerated(boolean generated) {
        MainFrame.generated = generated;
    }

    public static void setSolved(boolean solved) {
        MainFrame.solved = solved;
    }

    private JMenuBar initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem openMenu = new JMenuItem("Открыть файл", UIManager.getIcon("FileChooser.fileIcon"));
        openMenu.setHorizontalAlignment(SwingConstants.LEFT);
        openMenu.addActionListener(e -> loadMazeFromFile(new WriterReader().readFile()));
        JMenuItem saveMenu = new JMenuItem("Сохранить в файл", UIManager.getIcon("FileChooser.floppyDriveIcon"));
        saveMenu.setHorizontalAlignment(SwingConstants.LEFT);
        saveMenu.addActionListener(e -> new WriterReader().writeFile(saveMenu, grid));
        fileMenu.add(openMenu);
        fileMenu.add(saveMenu);
        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutMenu = new JMenuItem("О системе");
        aboutMenu.setHorizontalAlignment(SwingConstants.LEFT);
        aboutMenu.addActionListener(e -> {
            File htmlFile = new File("about.html");
            try {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        JMenuItem devMenu = new JMenuItem("О разработчике");
        devMenu.setHorizontalAlignment(SwingConstants.LEFT);
        String message = "Разработано студенткой группы 6138_020402D" + "\n"
                + "Ольгой Ширяевой" + "\n"
                + "под руководством " + "\n"
                + "доцента, к/н Л.С. Зеленко" + "\n"
                + "Самара, 2020";
        devMenu.addActionListener(e -> JOptionPane.showMessageDialog(devMenu, message, "О разработчике", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutMenu);
        helpMenu.add(devMenu);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void loadMazeFromFile(MazeGridPanel newGrid) {
        mazeBorder.remove(grid);
        grid = newGrid;
        generated = true;
        solved = false;
        gridDisplayed = false;
        enableGenerationParams(false);
        enableSolvingParams(true);
        mazeBorder.add(grid);
        mazeBorder.repaint();
        mazeBorder.revalidate();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Генератор лабиринтов");
        frame.setJMenuBar(initMenu());
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        frame.setContentPane(container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        grid = new MazeGridPanel(rows, cols);
        grid.setBackground(ColorScheme.TRANSPARENT);

        mazeBorder = new ImagePanel();
        mazeBorder.setSize(WIDTH, HEIGHT);
        final int BORDER_SIZE = 10;
        mazeBorder.setBounds(0, 0, WIDTH + BORDER_SIZE, HEIGHT + BORDER_SIZE);
        mazeBorder.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        mazeBorder.setImg(HobbitTheme.SHIRE.getBackground());
        mazeBorder.add(grid, BorderLayout.CENTER);

        JPanel leftPanel = initParamsPanel();
        Dimension d = leftPanel.getPreferredSize();
        d.height = mazeBorder.getPreferredSize().height;
        leftPanel.setPreferredSize(d);
        container.add(leftPanel);
        container.add(mazeBorder);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        grid.setVisible(false);
    }

    private void setTheme(HobbitTheme theme) {
        switch (theme) {
            case SHIRE -> mazeBorder.setImg(HobbitTheme.SHIRE.getBackground());
            case EREBOR -> mazeBorder.setImg(HobbitTheme.EREBOR.getBackground());
            case MIRKWOOD -> mazeBorder.setImg(HobbitTheme.MIRKWOOD.getBackground());
            case RIVENDELL -> mazeBorder.setImg(HobbitTheme.RIVENDELL.getBackground());
        }
        logger.log(Level.INFO, "Theme updated: " + theme.getTheme());
    }

    private void updateCellWidth() {
        cellWidth = Math.floorDiv(WIDTH, getRows());
        cellHeight = Math.floorDiv(HEIGHT, getCols());
    }

    private void updateGridSize() {
        mazeBorder.remove(grid);
        updateCellWidth();
        grid = new MazeGridPanel(rows, cols);
        grid.setBackground(ColorScheme.TRANSPARENT);
        mazeBorder.add(grid);
        gridDisplayed = true;
        mazeBorder.revalidate();
    }

    @SuppressWarnings("unchecked")
    private JPanel initParamsPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setInheritsPopupMenu(true);
        leftPanel.setMaximumSize(new Dimension(300, 600));
        leftPanel.setMinimumSize(new Dimension(260, 600));
        leftPanel.setPreferredSize(new Dimension(270, 600));
        leftPanel.putClientProperty("html.disable", Boolean.FALSE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        JPanel mazeGeneration = new JPanel();
        mazeGeneration.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        leftPanel.add(mazeGeneration, gbc);

        mazeGeneration.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Параметры генерации", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mazeGeneration.add(sizePanel, gbc);

        rowsSpinner = new JSpinner();
        rowsSpinner.setModel(new SpinnerNumberModel(DEFAULT_GRID, 7, 37, 2));
        rowsSpinner.addChangeListener(e -> setRows((int) rowsSpinner.getValue()));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        sizePanel.add(rowsSpinner, gbc);

        JLabel rowsLabel = new JLabel();
        rowsLabel.setText("Ширина");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        sizePanel.add(rowsLabel, gbc);

        JLabel colsLabel = new JLabel();
        colsLabel.setText("Высота");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        sizePanel.add(colsLabel, gbc);

        colsSpinner = new JSpinner();
        colsSpinner.setModel(new SpinnerNumberModel(DEFAULT_GRID, 7, 37, 2));
        colsSpinner.addChangeListener(e -> setCols((int) colsSpinner.getValue()));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        sizePanel.add(colsSpinner, gbc);

        genAlgPanel = new JPanel();
        genAlgPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mazeGeneration.add(genAlgPanel, gbc);

        genAlgLabel = new JLabel();
        genAlgLabel.setText("Алгоритм генерации");
        genAlgLabel.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        genAlgPanel.add(genAlgLabel, gbc);

        generationAlgorithmBox = new JComboBox(GenerationAlgorithm.values());
        generationAlgorithmBox.setMaximumRowCount(generationAlgorithmBox.getModel().getSize());
        generationAlgorithmBox.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        genAlgPanel.add(generationAlgorithmBox, gbc);

        JButton showGridButton = new JButton();
        showGridButton.setText("Показать сетку");

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        mazeGeneration.add(showGridButton, gbc);

        entranceExitSettingsPanel = new JPanel();
        entranceExitSettingsPanel.setLayout(new GridBagLayout());
        entranceExitSettingsPanel.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mazeGeneration.add(entranceExitSettingsPanel, gbc);

        entExitLabel = new JLabel();
        entExitLabel.setText("Вход/выход");
        entExitLabel.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        entranceExitSettingsPanel.add(entExitLabel, gbc);

        exitAutoRadioButton = new JRadioButton();
        exitAutoRadioButton.setText("Авто");
        exitAutoRadioButton.setSelected(true);
        exitAutoRadioButton.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        entranceExitSettingsPanel.add(exitAutoRadioButton, gbc);

        exitManualRadioButton = new JRadioButton();
        exitManualRadioButton.setText("Вручную");
        exitManualRadioButton.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        entranceExitSettingsPanel.add(exitManualRadioButton, gbc);

        ButtonGroup entExitGroup = new ButtonGroup();
        entExitGroup.add(exitAutoRadioButton);
        entExitGroup.add(exitManualRadioButton);

        createMazeButton = new JButton();
        createMazeButton.setText("Создать лабиринт");
        createMazeButton.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        mazeGeneration.add(createMazeButton, gbc);

        JPanel mazeSolving = new JPanel();
        mazeSolving.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        leftPanel.add(mazeSolving, gbc);
        mazeSolving.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Параметры прохождения", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        JPanel solvePanel = new JPanel();
        solvePanel.setLayout(new GridBagLayout());
        solvePanel.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mazeSolving.add(solvePanel, gbc);

        solveModeLabel = new JLabel();
        solveModeLabel.setText("Прохождение");
        solveModeLabel.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        solvePanel.add(solveModeLabel, gbc);

        autoSolveRadioButton = new JRadioButton();
        autoSolveRadioButton.setText("Авто");
        autoSolveRadioButton.setSelected(true);
        autoSolveRadioButton.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        solvePanel.add(autoSolveRadioButton, gbc);

        manualSolveRadioButton = new JRadioButton();
        manualSolveRadioButton.setText("Вручную");
        manualSolveRadioButton.setEnabled(false);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        solvePanel.add(manualSolveRadioButton, gbc);

        ButtonGroup solveGroup = new ButtonGroup();
        solveGroup.add(autoSolveRadioButton);
        solveGroup.add(manualSolveRadioButton);

        JPanel solveAlgPanel = new JPanel();
        solveAlgPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mazeSolving.add(solveAlgPanel, gbc);

        solveAlgLabel = new JLabel();
        solveAlgLabel.setText("Алгоритм поиска пути");
        solveAlgLabel.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        solveAlgPanel.add(solveAlgLabel, gbc);

        solveAlgBox = new JComboBox(SolvingAlgorithm.values());
        solveAlgBox.setMaximumRowCount(generationAlgorithmBox.getModel().getSize());
        solveAlgBox.setSelectedItem(SolvingAlgorithm.LEE);
        solveAlgBox.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        solveAlgPanel.add(solveAlgBox, gbc);

        visualizationPanel = new JPanel();
        visualizationPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mazeSolving.add(visualizationPanel, gbc);

        visualSpeed = new JLabel();
        visualSpeed.setText("Скорость визуализации");
        visualSpeed.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.05;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        visualizationPanel.add(visualSpeed, gbc);

        visualizationSlider = new JSlider();
        visualizationSlider.setInverted(true);
        visualizationSlider.setMaximum(100);
        visualizationSlider.setMinimum(0);
        visualizationSlider.setMinorTickSpacing(50);
        visualizationSlider.setPaintLabels(true);
        visualizationSlider.setPaintTicks(true);
        visualizationSlider.setSnapToTicks(true);
        visualizationSlider.setValue(50);
        visualizationSlider.setValueIsAdjusting(true);
        visualizationSlider.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        visualizationPanel.add(visualizationSlider, gbc);

        manualSolveRadioButton.addActionListener(e -> {
            autoSolve = false;
            enableSolvingControlsIfManual(false);
            grid.setAutoSolve(false);
            grid.setClickable(true);
            grid.requestFocus();
        });

        autoSolveRadioButton.addActionListener(e -> {
            autoSolve = true;
            enableSolvingControlsIfManual(true);
        });

        solveMazeButton = new JButton();
        solveMazeButton.setText("Искать путь");
        solveMazeButton.setEnabled(false);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        mazeSolving.add(solveMazeButton, gbc);

        JPanel themesPanel = new JPanel();
        themesPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        leftPanel.add(themesPanel, gbc);
        themesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Тема оформления", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        JPanel themesRadio = new JPanel();
        themesRadio.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        themesPanel.add(themesRadio, gbc);

        JRadioButton shireRadioButton = new JRadioButton();
        shireRadioButton.setText(HobbitTheme.SHIRE.getTheme());
        shireRadioButton.addActionListener(e -> setTheme(HobbitTheme.SHIRE));
        shireRadioButton.setSelected(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        themesRadio.add(shireRadioButton, gbc);

        JRadioButton rivendellRadioButton = new JRadioButton();
        rivendellRadioButton.setText(HobbitTheme.RIVENDELL.getTheme());
        rivendellRadioButton.addActionListener(e -> setTheme(HobbitTheme.RIVENDELL));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        themesRadio.add(rivendellRadioButton, gbc);

        JRadioButton ereborRadioButton = new JRadioButton();
        ereborRadioButton.setText(HobbitTheme.EREBOR.getTheme());
        ereborRadioButton.addActionListener(e -> setTheme(HobbitTheme.EREBOR));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        themesRadio.add(ereborRadioButton, gbc);

        JRadioButton mirkwoodRadioButton = new JRadioButton();
        mirkwoodRadioButton.setText(HobbitTheme.MIRKWOOD.getTheme());
        mirkwoodRadioButton.addActionListener(e -> setTheme(HobbitTheme.MIRKWOOD));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        themesRadio.add(mirkwoodRadioButton, gbc);

        ButtonGroup themesGroup = new ButtonGroup();
        themesGroup.add(shireRadioButton);
        themesGroup.add(rivendellRadioButton);
        themesGroup.add(mirkwoodRadioButton);
        themesGroup.add(ereborRadioButton);


        showGridButton.addActionListener(e -> {
            updateGridSize();
            exitAutoRadioButton.setEnabled(true);
            exitManualRadioButton.setEnabled(true);
            generationAlgorithmBox.setEnabled(true);
            createMazeButton.setEnabled(true);
            enableGenerationParams(true);
        });

        visualizationSlider.addChangeListener(e -> speed = visualizationSlider.getValue());

        exitAutoRadioButton.addActionListener(e -> {
            grid.setClickable(false);
            manualEntranceExit = false;
        });
        exitManualRadioButton.addActionListener(e -> {
            grid.setClickable(true);
            manualEntranceExit = true;
        });
        createMazeButton.addActionListener(e -> {
            if (gridDisplayed) {
                generated = true;
                solved = false;
                gridDisplayed = false;
                grid.generate((GenerationAlgorithm) Objects.requireNonNull(generationAlgorithmBox.getSelectedItem()));
                enableSolvingParams(true);
            }
        });

        solveMazeButton.addActionListener(e -> {
            if (generated && autoSolve && !solved) {
                grid.solve((SolvingAlgorithm) Objects.requireNonNull(solveAlgBox.getSelectedItem()));
            } else if (generated && !autoSolve && !solved) {
                grid.setAutoSolve(false);
            } else if (!generated) {
                JOptionPane.showMessageDialog(leftPanel.getRootPane(), "Лабиринт ещё не готов. Пожалуйста, подождите.");
            }
        });

        return leftPanel;
    }

    public void enableGenerationParams(boolean enabled) {
        entExitLabel.setEnabled(enabled);
        entranceExitSettingsPanel.setEnabled(enabled);
        exitAutoRadioButton.setEnabled(enabled);
        exitManualRadioButton.setEnabled(enabled);
        genAlgLabel.setEnabled(enabled);
        genAlgPanel.setEnabled(enabled);
        generationAlgorithmBox.setEnabled(enabled);
        createMazeButton.setEnabled(enabled);
    }

    public void enableSolvingParams(boolean enabled) {
        solveModeLabel.setEnabled(enabled);
        autoSolveRadioButton.setEnabled(enabled);
        manualSolveRadioButton.setEnabled(enabled);
        solveAlgLabel.setEnabled(enabled);
        solveAlgBox.setEnabled(enabled);
        visualizationPanel.setEnabled(enabled);
        visualizationSlider.setEnabled(enabled);
        visualSpeed.setEnabled(enabled);
        solveMazeButton.setEnabled(enabled);
    }

    public void enableSolvingControlsIfManual(boolean enabled) {
        solveAlgLabel.setEnabled(enabled);
        solveAlgBox.setEnabled(enabled);
        visualSpeed.setEnabled(enabled);
        visualizationPanel.setEnabled(enabled);
        visualizationSlider.setEnabled(enabled);
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

    public MazeGridPanel getGrid() {
        return grid;
    }
}
