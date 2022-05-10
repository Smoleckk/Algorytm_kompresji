package pl.edu.pw.ee;

public class Node {
    private int ch;
    private int freq;
    private Node left;
    private Node right;

    public Node(int ch, int freq, Node left, Node right) {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
    public Node(int ch, int freq) {
        this.ch = ch;
        this.freq = freq;
        this.left = null;
        this.right = null;
    }

    public int getChar() {
        return ch;
    }
    public void setChar(int character) {
        this.ch = character;
    }

    public void setFreq(int frequency) {
        this.freq = frequency;
    }

    public int getFreq() {
        return freq;
    }
    
    public Node getLeft() {
        return left;
    }

    public void setLeft(Node leftNode) {
        left = leftNode;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node rightNode) {
        right = rightNode;
    }
}