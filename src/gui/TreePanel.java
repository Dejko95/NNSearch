package gui;

import geometry.Cell;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TreePanel extends JPanel {
    public TreePanel(Cell rootCell, DrawPanel drawPanel) {
        setBackground(Color.WHITE);
        CellTreeModel treeModel = new CellTreeModel(rootCell);
        JTree tree = new JTree();
        tree.setModel(treeModel);
        add(tree);
        tree.collapseRow(0);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Cell selectedCell = (Cell)tree.getLastSelectedPathComponent();
                drawPanel.selectedOuterBox = selectedCell.outerBox;
                drawPanel.selectedInnerBox = selectedCell.innerBox;
                drawPanel.borrowedPoint = selectedCell.borrowed;
                drawPanel.repaint();
            }
        });

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                //System.out.println(event.getSource());
                Cell selectedCell = (Cell)event.getPath().getLastPathComponent();
                drawPanel.addOuterBox(selectedCell.highChild.outerBox);
                //drawPanel.addInnerBox(selectedCell.highChild.innerBox);
                drawPanel.addOuterBox(selectedCell.lowChild.outerBox);
                //drawPanel.addInnerBox(selectedCell.lowChild.innerBox);

                drawPanel.addInnerBox(selectedCell.innerBox);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                Cell selectedCell = (Cell)event.getPath().getLastPathComponent();
                drawPanel.removeOuterBox(selectedCell.highChild.outerBox);
                //drawPanel.removeInnerBox(selectedCell.highChild.innerBox);
                drawPanel.removeOuterBox(selectedCell.lowChild.outerBox);
                //drawPanel.removeInnerBox(selectedCell.lowChild.innerBox);

                drawPanel.removeInnerBox(selectedCell.innerBox);
            }
        });

    }
}
