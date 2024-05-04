package src.structures.tree;

import src.Student;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

//TODO: Change recursive to loop
public class Tree implements Serializable {
    private Node root;

    public void add(Student data) {
        Node newNode = new Node(data);
        if(root != null)
            add(root, null, newNode);
        else
            root = newNode;
    }

    private Node add(Node pointer, Node parent, Node newNode){
        if(pointer == null) {
            newNode.setParent(parent);
            return newNode;
        }
        if(pointer.compareTo(newNode) < 0){
            pointer.setRight(add(pointer.getRight(), pointer, newNode));
        }
        else if(pointer.compareTo(newNode) >= 0) {
            pointer.setLeft(add(pointer.getLeft(), pointer, newNode));
        }

        int balance =  (pointer.getLeft() != null ? pointer.getLeft().getHeight(): 0) - (pointer.getRight() != null ? pointer.getRight().getHeight(): 0);
        if(balance > 1 && pointer.getLeft().compareTo(newNode) > 0)
            return leftRotate(pointer);
        else if(balance < -1 && pointer.getRight().compareTo(newNode) < 0)
            return rightRotate(pointer);
        else if(balance > 1 && pointer.getLeft().compareTo(newNode) < 0){
            pointer.setLeft(leftRotate(pointer.getLeft()));
            return rightRotate(pointer);
        }
        else if(balance < -1 && pointer.getRight().compareTo(newNode) > 0){
            pointer.setRight(rightRotate(pointer.getRight()));
            return leftRotate(pointer);
        }
        return pointer;
    }

    public int getHeight(Node node){
        if (node == null)
            return 0;
        return node.getHeight();
    }

    Node rightRotate(Node y) {
        Node x = y.getLeft();
        Node T2 = x.getRight();

        // Perform rotation
        x.setRight(y);
        y.setParent(x);
        y.setLeft(T2);
        T2.setParent(y);

        x.setHeight(Math.max(getHeight(x.getLeft()), getHeight(x.getRight())) + 1);
        y.setHeight(Math.max(getHeight(y.getLeft()), getHeight(y.getRight())) + 1);

        // Return new root
        return x;
    }

    // A utility function to left rotate subtree rooted with x
    // See the diagram given above.
    Node leftRotate(Node x) {
        Node y = x.getRight();
        Node T2 = y.getLeft();

        // Perform rotation
        y.setLeft(x);
        x.setParent(y);
        x.setRight(T2);
        T2.setParent(x);

        x.setHeight(Math.max(getHeight(x.getLeft()), getHeight(x.getRight())) + 1);
        y.setHeight(Math.max(getHeight(y.getLeft()), getHeight(y.getRight())) + 1);

        // Return new root
        return y;
    }

    int getBalance(Node N) {
        if (N == null)
            return 0;

        return getHeight(N.getLeft()) - getHeight(N.getRight());
    }

    public boolean remove(int id){
        Node node = findNode(id);
        if(node == null)
            return false;
        root = delete(root, node);
        return true;
    }

    //TODO: Fix root cant be deleted
    private Node delete(Node pointer, Node node){
        if(node == null)
            return null;
        if(node.compareTo(pointer) < 0){
            Node left = delete(pointer.getLeft(), node);
            pointer.setLeft(left);

        }
        else if(node.compareTo(pointer) > 0){
            pointer.setRight(delete(pointer.getRight(), node));
        }
        else {
            if(pointer.getLeft() == null || pointer.getRight() == null){
                Node temp = null;
                if(pointer.getLeft() == null)
                    temp = pointer.getRight();
                else
                    temp = pointer.getLeft();
                if(temp == null){
                    if(pointer.getParent().getLeft() == pointer)
                        pointer.getParent().setLeft(null);
                    else
                        pointer.getParent().setRight(null);
                    pointer = null;
                }
                else
                    pointer = temp;
            }
            else {
                Node temp = minValueNode(pointer.getRight());
                pointer.setData(temp.getData());
                pointer.setRight(delete(pointer.getRight(), temp));
            }
        }

        if(pointer == null)
            return pointer;

        pointer.setHeight(Math.max(getHeight(pointer.getLeft()), getHeight(pointer.getRight())) + 1);
        int balance = getBalance(pointer);

        if(balance > 1 && getBalance(pointer.getLeft()) >= 0)
            return rightRotate(pointer);
        if(balance < -1 && getBalance(pointer.getRight()) <= 0)
            return leftRotate(pointer);
        if(balance > 1 && getBalance(pointer.getLeft()) < 0) {
            pointer.setLeft(leftRotate(pointer.getLeft()));
            return rightRotate(pointer);
        }
        if(balance < -1 && getBalance(pointer.getRight()) > 0) {
            pointer.setRight(rightRotate(pointer.getRight()));
            return leftRotate(pointer);
        }
        return pointer;
    }

    private Node minValueNode(Node node){
        Node current = node;
        while (current.getLeft() != null)
            current = current.getLeft();
        return current;
    }

    public Student find(int id){
        Node node = findNode(id);
        if(node != null)
            return node.getData();
        return null;
    }

    private Node findNode(int data){
        Node pointer = root;
        while (pointer != null){
            if(pointer.getData().getId() == data)
                return pointer;
            else if(pointer.getData().getId() > data)
                pointer = pointer.getLeft();
            else
                pointer = pointer.getRight();
        }
        return null;
    }

    public String inOrder(){
        return root.inOrder();
    }

    public void toFile() throws IOException {
        File file = new File("data");
        file.mkdir();
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("data/test.dat"));
        stream.writeObject(this);
    }

    public static Tree fromFile() throws IOException, ClassNotFoundException{
        if(!new File("data/test.dat").exists())
            return new Tree();
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream("data/test.dat"));
        return (Tree) stream.readObject();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Tree tree = new Tree();
        Random random = new Random();
        for (int i = 1; i < 25; i++) {
            tree.add(new Student(random.nextInt(100), random.nextDouble(4), "Student ", Integer.toString(i), new Date(random.nextInt(1995, 2005), random.nextInt(1, 12), random.nextInt(1, 28)), "Computer Science", "American"));
        }
        System.out.println(tree.inOrder());

        tree.toFile();
        Tree tree2 = Tree.fromFile();

        System.out.println("Second tree: ");

        System.out.println(tree2.inOrder());

    }
}
