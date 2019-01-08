package application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
/**
 * Filename:   BPTree.java
 * Project:    p5
 * Course:     cs400 Fall 2018 
 * Authors:   Vinnie Angellotti, David Portugal, Charles Houghtaling, Monica Schmidt  
 * Due Date:  10/12 
 * 
 *
 * Additional credits:
 *
 * Bugs or other notes: range search not working optimally
 *
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;

    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;


    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
                            "Illegal branching factor: " + branchingFactor);
        }
        this.branchingFactor = branchingFactor;
        root = new LeafNode();
    }


    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
        root.insert(key, value);
    }


    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(">=") && 
                        !comparator.contentEquals("==") && 
                        !comparator.contentEquals("<=") )
            return new ArrayList<V>();
        return root.rangeSearch(key, comparator);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }


    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {

        // List of keys
        List<K> keys;

        /**
         * Package constructor
         */
        Node() {
            this.keys = new ArrayList<K>();
        }

        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();

        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();

        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * Returns true/false from the isOverflow method.
         * @return true/false
         */
        abstract boolean isOverflow();

        /**
         * Returns a String representation of the tree.
         */
        public String toString() {
            return keys.toString();
        }

    } // End of abstract class Node

    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;

        /**
         * Package constructor
         */
        InternalNode() {
            super();
            this.children = new ArrayList<Node>();
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey();
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return children.size() > branchingFactor;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
            Node child = getChild(key);
            child.insert(key, value);
            if (child.isOverflow()) {
                Node sib = child.split();
                insChild(sib.getFirstLeafKey(), sib);
            }
            if (root.isOverflow()) {
                Node sib = split();
                InternalNode nRoot = new InternalNode();
                nRoot.keys.add(sib.getFirstLeafKey());
                nRoot.children.add(this);
                nRoot.children.add(sib);
                root = nRoot;
            }
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            int f = (keys.size() / 2) + 1;
            int t = keys.size();
            InternalNode sib = new InternalNode();
            sib.keys.addAll(keys.subList(f, t));
            sib.children.addAll(children.subList(f, t + 1));
            keys.subList(f - 1, t).clear();
            children.subList(f, t + 1).clear();
            return sib;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            return getChild(key).rangeSearch(key, comparator);
        }

        /**
         * Gets the child of K key
         * @param key key
         * @return Node the child node
         */
        private Node getChild(K key) { 
            int loc = Collections.binarySearch(keys, key);
            int childIndex = loc >= 0 ? loc + 1 : -loc -1;
            return children.get(childIndex);
        }
        
        /**
         * Inserts the child node at K key
         * @param key key
         * @param child child node
         */
        private void insChild(K key, Node child) {
            int loc = Collections.binarySearch(keys, key);
            int childIndex = loc >= 0 ? loc + 1 : -loc -1;
            if (loc >= 0) {
                children.set(childIndex, child);
            } else {
                keys.add(childIndex, key);
                children.add(childIndex + 1, child);
            }
        }

    } // End of class InternalNode


    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {

        // List of values
        List<V> values;

        // Reference to the next leaf node
        LeafNode next;

        // Reference to the previous leaf node
        LeafNode previous;

        /**
         * Package constructor
         */
        LeafNode() {
            super();
            this.values = new ArrayList<V>();
        }


        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return values.size() > branchingFactor - 1;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
            int loc = Collections.binarySearch(keys, key);
            int valInd = loc >= 0 ? loc : -loc -1;
            if (loc >= 0) {
                values.set(valInd, value);
            } else {
                keys.add(valInd, key);
                values.add(valInd, value);
            }
            if (root.isOverflow()) {
                Node sib = split();
                InternalNode nRoot = new InternalNode();
                nRoot.keys.add(sib.getFirstLeafKey());
                nRoot.children.add(this);
                nRoot.children.add(sib);
            }
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            LeafNode sib = new LeafNode();
            int f = (keys.size() + 1) / 2;
            int t = keys.size();
            sib.keys.addAll(keys.subList(f, t));
            sib.values.addAll(values.subList(f, t));
            keys.subList(f, t).clear();
            values.subList(f, t).clear();
            sib.next = next;
            next = sib;
            return sib;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            List<V> result = new LinkedList<V>();
            LeafNode node = this;
            while (node != null) {
                Iterator<K> kIt = node.keys.iterator();
                Iterator<V> vIt = node.values.iterator();
                while (kIt.hasNext()) {
                    K key1 = kIt.next();
                    V value = vIt.next();
                    if (comparator.contentEquals(">=")) {
                        if (key.compareTo(key1) <= 0) {
                            result.add(value);
                        }
                    } else if (comparator.contentEquals("==")) {
                        if (key.compareTo(key1) == 0) {
                            result.add(value);
                        }
                    } else if (comparator.contentEquals("<=")) {
                        if (key.compareTo(key1) >= 0) {
                            result.add(value);
                        }
                    } else {
                        System.out.println("Invalid comparator");
                    }
                }
                node = node.next;
            }
            return result;
        }
    } // End of class LeafNode


    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree


