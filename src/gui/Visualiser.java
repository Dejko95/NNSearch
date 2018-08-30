package gui;

import geometry.NearestNeighbour;
import geometry.bbd_tree.Cell;
import geometry.HyperCube;

import javax.swing.*;
import java.awt.*;

public class Visualiser extends JFrame {
    public Visualiser(Cell rootCell, HyperCube hyperCube, NearestNeighbour algorithm) {
        setTitle("NNSearch");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //JPanel panel = new JPanel();
        //add(panel);
        //panel.setPreferredSize(new Dimension(800, 600));
        DrawPanel drawPanel = new DrawPanel(hyperCube, algorithm);
        TreePanel treePanel = new TreePanel(rootCell, drawPanel);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        InputPanel inputPanel = new InputPanel(algorithm, drawPanel);

        leftPanel.add(treePanel, BorderLayout.CENTER);
        leftPanel.add(inputPanel, BorderLayout.SOUTH);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawPanel);
        splitPane.setPreferredSize(new Dimension(800, 600));
        splitPane.setDividerLocation(200);
        add(splitPane);

        pack();
    }
}
