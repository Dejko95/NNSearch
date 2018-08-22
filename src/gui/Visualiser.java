package gui;

import geometry.ApproximateNearestNeighbour;
import geometry.Cell;
import geometry.HyperCube;

import javax.swing.*;
import java.awt.*;

public class Visualiser extends JFrame {
    public Visualiser(Cell rootCell, HyperCube hyperCube, ApproximateNearestNeighbour algorithm) {
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //JPanel panel = new JPanel();
        //add(panel);
        //panel.setPreferredSize(new Dimension(800, 600));
        DrawPanel drawPanel = new DrawPanel(hyperCube, algorithm);
        TreePanel treePanel = new TreePanel(rootCell, drawPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, drawPanel);
        splitPane.setPreferredSize(new Dimension(800, 600));
        splitPane.setDividerLocation(200);
        add(splitPane);

        pack();
    }
}
