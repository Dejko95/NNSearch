package gui;


import geometry.*;
import geometry.Box;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DrawPanel extends JPanel {
    DataPoint borrowedPoint;
    HyperCube hyperCube;
    ArrayList<Box> outerBoxes = new ArrayList<>();
    ArrayList<Box> innerBoxes = new ArrayList<>();
    Box selectedOuterBox = null;
    Box selectedInnerBox = null;
    //ApproximateNearestNeighbour algorithm;

    //Step by step variables
    int query_x;
    int query_y;
    DataPoint closest_point;
    Cell current_cell;
    double radius;
    double radius_epsilon;
    boolean query_visualisation = false;

    public DrawPanel(HyperCube hyperCube, ApproximateNearestNeighbour algorithm) {
        this.hyperCube = hyperCube;
        //this.algorithm = algorithm;

        setBackground(Color.WHITE);
        for (DataPoint point: hyperCube.points) {
            point.prepare2D();
        }
        Box hyperBox = new Box(hyperCube.dimensions);
        for (int i=0; i<hyperCube.dimensions; i++) {
            hyperBox.begin[i] = 0;
            hyperBox.end[i] = hyperCube.bounds[i];
        }
        outerBoxes.add(hyperBox);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() > hyperBox.end[0] || e.getY() > hyperBox.end[1]) return;
                QueryAnswer queryAnswer = algorithm.answerQuery2D(e.getX(), e.getY());
                query_x = e.getX();
                query_y = e.getY();
                int steps = queryAnswer.ordered_cells.size();
                radius = radius_epsilon = -1;
                query_visualisation = true;
                System.out.println("visualisation");
                for (int i=0; i<steps; i++) {
                    System.out.println(i);
                    closest_point = queryAnswer.closest_points.get(i);
                    current_cell = queryAnswer.ordered_cells.get(i);
//                    revalidate();
//                    repaint();
//                    updateUI();
                    paintImmediately(0, 0, getWidth(), getHeight());

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    radius = queryAnswer.shortest_distances.get(i);
                    radius_epsilon = radius / (1 + algorithm.epsilon);
                }
                current_cell = queryAnswer.lastCell;
                paintImmediately(0, 0, getWidth(), getHeight());
                query_visualisation = false;
            }
        });

    }

    void addOuterBox(Box box) {
        if (box == null) return;
        outerBoxes.add(box);
        repaint();
    }

    void removeOuterBox(Box box) {
        if (box == null) return;
        outerBoxes.remove(box);
        repaint();
    }

    void addInnerBox(Box box) {
        if (box == null) return;
        innerBoxes.add(box);
        repaint();
    }

    void removeInnerBox(Box box) {
        if (box == null) return;
        innerBoxes.remove(box);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        System.out.println("repaint");
        Graphics2D g2 = (Graphics2D)g;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.lightGray);
        for (Box box: innerBoxes) {
            Rectangle2D rect = new Rectangle2D.Double(box.begin[0], box.begin[1], box.end[0] - box.begin[0], box.end[1] - box.begin[1]);
            g2.fill(rect);
        }

        g.setColor(Color.BLACK);
        for (Box box: outerBoxes) {
            Rectangle2D rect = new Rectangle2D.Double(box.begin[0], box.begin[1], box.end[0] - box.begin[0], box.end[1] - box.begin[1]);
            g2.draw(rect);
        }

        g.setColor(Color.green);
        if (selectedOuterBox != null) {
            Rectangle2D rect = new Rectangle2D.Double(selectedOuterBox.begin[0], selectedOuterBox.begin[1], selectedOuterBox.end[0] - selectedOuterBox.begin[0], selectedOuterBox.end[1] - selectedOuterBox.begin[1]);
            g2.draw(rect);
        }
        g.setColor(Color.red);
        if (selectedInnerBox != null) {
            Rectangle2D rect = new Rectangle2D.Double(selectedInnerBox.begin[0], selectedInnerBox.begin[1], selectedInnerBox.end[0] - selectedInnerBox.begin[0], selectedInnerBox.end[1] - selectedInnerBox.begin[1]);
            g2.draw(rect);
        }


        g.setColor(Color.BLACK);
        for (DataPoint point: hyperCube.points) {
            g2.fillOval(point.x - 2, point.y - 2, 5, 5);
        }

        g.setColor(Color.blue);
        if (borrowedPoint != null) {
            g2.fillOval(borrowedPoint.x - 2, borrowedPoint.y - 2, 5, 5);
        }

        if (query_visualisation) {
            g2.setColor(Color.MAGENTA);
            g2.fillOval(query_x - 2, query_y - 2, 5, 5);
            Rectangle2D rect = new Rectangle2D.Double(current_cell.outerBox.begin[0], current_cell.outerBox.begin[1],
                    current_cell.outerBox.end[0] - current_cell.outerBox.begin[0], current_cell.outerBox.end[1] - current_cell.outerBox.begin[1]);
            g2.draw(rect);
            if (current_cell.innerBox != null) {
                rect = new Rectangle2D.Double(current_cell.innerBox.begin[0], current_cell.innerBox.begin[1],
                        current_cell.innerBox.end[0] - current_cell.innerBox.begin[0], current_cell.innerBox.end[1] - current_cell.innerBox.begin[1]);
                g2.draw(rect);
            }
            if (radius > 0) {
                Ellipse2D circle = new Ellipse2D.Double(query_x - radius, query_y - radius, 2 * radius, 2 * radius);
                g2.draw(circle);
                //g2.setStroke(new BasicStroke(2));
                Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g2.setStroke(dashed);
                Ellipse2D circle_epsilon = new Ellipse2D.Double(query_x - radius_epsilon, query_y - radius_epsilon, 2 * radius_epsilon, 2 * radius_epsilon);
                g2.draw(circle_epsilon);
                g2.setStroke(new BasicStroke(1));
            }
        }

    }
}