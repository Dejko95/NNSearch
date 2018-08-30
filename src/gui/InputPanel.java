package gui;

import geometry.NearestNeighbour;
import geometry.DataPoint;
import geometry.bbd_tree.QueryAnswer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class InputPanel extends JPanel {
    public InputPanel(NearestNeighbour algorithm, DrawPanel drawPanel) {
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setPreferredSize(new Dimension(200, 100));
        setLayout(new GridLayout(3, 2, 5, 5));
        JTextField epsilonInput = new JTextField(algorithm.epsilon + "");
        JButton epsilonButton = new JButton("Set epsilon");
        JTextField queryInput = new JTextField();
        JButton queryButton = new JButton("Query");
        JButton reset = new JButton("Reset");
        add(epsilonInput);
        add(epsilonButton);
        add(queryInput);
        add(queryButton);
        add(reset);

        reset.addActionListener(e -> {
            JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(InputPanel.this);
            frame.dispose();
            //(JFrame)SwingUtilities.getWindowAncestor(InputPanel.this);
            new NearestNeighbour();
        });

        epsilonButton.addActionListener(e -> {
            algorithm.epsilon = Double.valueOf(epsilonInput.getText());
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
            } else {
                DataPoint queryPoint = new DataPoint(algorithm.dimension);
                queryPoint.coordinates = input;
                QueryAnswer queryAnswer = algorithm.answerQuery(queryPoint);
                DataPoint closestPoint = queryAnswer.closest_points.get(queryAnswer.closest_points.size() - 1);
                JOptionPane.showMessageDialog(null, "Approx. closest point is " + closestPoint + ".\n"
                        + "Number of visited cells is " + queryAnswer.closest_points.size() + ".\n"
                        + "True closest point is " + queryAnswer.trueClosestPoint + ".\n"
                        + "Relative error is " + (queryPoint.distanceFrom(closestPoint) / queryPoint.distanceFrom(queryAnswer.trueClosestPoint) - 1));
            }
        });
    }
}
