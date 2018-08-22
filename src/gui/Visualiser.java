package gui;

import geometry.ApproximateNearestNeighbour;
import geometry.Cell;
import geometry.HyperCube;
import geometry.QueryAnswer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Visualiser extends JFrame {
    public Visualiser(Cell rootCell, HyperCube hyperCube, ApproximateNearestNeighbour algorithm) {
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //JPanel panel = new JPanel();
        //add(panel);
        //panel.setPreferredSize(new Dimension(800, 600));
        DrawPanel drawPanel = new DrawPanel(hyperCube, algorithm);
        TreePanel treePanel = new TreePanel(rootCell, drawPanel);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        inputPanel.setPreferredSize(new Dimension(0, 50));
        inputPanel.setLayout(new GridLayout(2, 2, 5, 5));
        JTextField epsilonInput = new JTextField(algorithm.epsilon + "");
        JButton epsilonButton = new JButton("Set epsilon");
        JTextField queryInput = new JTextField();
        JButton queryButton = new JButton("Query");
        inputPanel.add(epsilonInput);
        inputPanel.add(epsilonButton);
        inputPanel.add(queryInput);
        inputPanel.add(queryButton);
        epsilonButton.addActionListener(e -> {
            algorithm.epsilon = Integer.valueOf(epsilonInput.getText());
        });
        queryButton.addActionListener(e -> {
            System.out.println(queryInput.getText());
            int input[] = Arrays.stream(queryInput.getText().split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
            System.out.println();
            if (input.length != algorithm.dimension) {
                JOptionPane.showMessageDialog(null, "Dimensions not match.");
                return;
            }
            if (algorithm.dimension == 2 && algorithm.graphicsON) {
                QueryAnswer queryAnswer = algorithm.answerQuery2D(input[0], input[1]);
                drawPanel.query_x = input[0];
                drawPanel.query_y = input[1];
                drawPanel.visualiseQuery(queryAnswer, algorithm);
            }
        });

        leftPanel.add(treePanel, BorderLayout.CENTER);
        leftPanel.add(inputPanel, BorderLayout.SOUTH);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawPanel);
        splitPane.setPreferredSize(new Dimension(800, 600));
        splitPane.setDividerLocation(200);
        add(splitPane);

        pack();
    }
}
