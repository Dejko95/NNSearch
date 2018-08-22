package gui;

import geometry.Cell;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CellTreeModel implements TreeModel {
    Cell rootCell = null;

    public CellTreeModel(Cell rootCell) {
        this.rootCell = rootCell;
    }

    @Override
    public Object getRoot() {
        return rootCell;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Cell cellNode = (Cell)parent;
        return index == 0 ? cellNode.lowChild : cellNode.highChild;
    }

    @Override
    public int getChildCount(Object parent) {
        Cell cellNode = (Cell)parent;
        return (cellNode.lowChild != null ? 1 : 0) + (cellNode.highChild != null ? 1 : 0);
    }

    @Override
    public boolean isLeaf(Object node) {
        Cell cellNode = (Cell)node;
        return cellNode.lowChild == null && cellNode.highChild == null;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("???");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Cell parentCell = (Cell)parent;
        Cell childCell = (Cell)child;
        if (parentCell.lowChild == childCell) return 0;
        if (((Cell) parent).highChild == childCell) return 1;
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }
}
