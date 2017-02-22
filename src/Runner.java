import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrew on 2/10/2017.
 */
class Tree {
    public static class Node {
        int key;
        Node left = null;
        Node right = null;
        int height;
        int MSL;
        boolean isUsed = false;

        Node(int key) {
            this.key = key;
            height = 0;
            MSL = 0;
        }
    }

    public List<Integer> unmarkedKeyList = new LinkedList<>();
    public List<Integer> keyList = new LinkedList<>();

    public Node root = null;
    public int maxMSL = 0;
    List<Node> mslList = new ArrayList<>();

    public void getUnmarkedKeyList(Node currentNode) {
        if (currentNode != null) {
            if (!currentNode.isUsed) {
                unmarkedKeyList.add(currentNode.key);
            }
            getUnmarkedKeyList(currentNode.left);
            getUnmarkedKeyList(currentNode.right);
        }
    }

    public void getKeyList(Node currentNode) {
        if (currentNode != null) {
            keyList.add(currentNode.key);
            getKeyList(currentNode.left);
            getKeyList(currentNode.right);
        }
    }

    public void getMslHeight(Node currentNode) {
        if (currentNode != null) {
            getMslHeight(currentNode.left);
            getMslHeight(currentNode.right);
            currentNode.height = getHeight(currentNode);
            currentNode.MSL = getMSL(currentNode);
            if (currentNode.MSL > maxMSL) {
                maxMSL = currentNode.MSL;
            }
            System.out.println(currentNode.key + " height " + currentNode.height + " MSL " + currentNode.MSL);
        }
    }

    public void getMaxMslList(Node currentNode) {
        if (currentNode != null) {
            getMaxMslList(currentNode.left);
            getMaxMslList(currentNode.right);
            if (currentNode.MSL == maxMSL) {
                mslList.add(currentNode);
            }
        }
    }

    public void printUsed(Node currentNode) {
        if (currentNode != null) {
            printUsed(currentNode.left);
            printUsed(currentNode.right);
            System.out.println(currentNode.key + " used " + currentNode.isUsed);
        }
    }

    public Node find_min(Node currentNode) {
        if (currentNode.left != null) {
            return find_min(currentNode.left);
        } else
            return currentNode;
    }

    public Node deleteRecursively(Node currentNode, int x) {
        if (currentNode == null)
            return null;
        if (x < currentNode.key) {
            currentNode.left = deleteRecursively(currentNode.left, x);
            return currentNode;
        }
        if (x > currentNode.key) {
            currentNode.right = deleteRecursively(currentNode.right, x);
            return currentNode;
        }
        if (currentNode.left == null) {
            return currentNode.right;
        } else if (currentNode.right == null) {
            return currentNode.left;
        } else {
            int min_key = find_min(currentNode.right).key;
            currentNode.key = min_key;
            currentNode.right = deleteRecursively(currentNode.right, min_key);
            return currentNode;
        }
    }


    public void deleteNode(int delNodeKey) {
        root = deleteRecursively(root, delNodeKey);
    }

    public void insert(int insNode) {
        root = nodeInsert(root, insNode);
    }

    private static Node nodeInsert(Node node, int insNodeKey) {
        if (node == null) {
            return new Node(insNodeKey);
        }
        if (insNodeKey < node.key) {
            node.left = nodeInsert(node.left, insNodeKey);
        } else if (insNodeKey > node.key) {
            node.right = nodeInsert(node.right, insNodeKey);
        }
        return node;
    }

    public int getHeight(Node node) {
        if (node.left == null && node.right == null) {
            return 0;
        } else {
            if (node.left == null) {
                return node.right.height + 1;
            }
            if (node.right == null) {
                return node.left.height + 1;
            }
            return Math.max(node.right.height, node.left.height) + 1;
        }
    }

    public int getMSL(Node node) {
        if (node.left == null && node.right == null) {
            return 0;
        } else {
            if (node.left == null) {
                return node.right.height + 1;
            }
            if (node.right == null) {
                return node.left.height + 1;
            }
            return node.right.height + node.left.height + 2;
        }
    }

    public void markNodes(Node currentNode) {
        while ((currentNode.left != null) || (currentNode.right != null)) {
            currentNode.isUsed = true;
            if (currentNode.left == null) {
                currentNode = currentNode.right;
                continue;
            }
            if (currentNode.right == null) {
                currentNode = currentNode.left;
                continue;
            }
            if (currentNode.left.height > currentNode.right.height) {
                currentNode = currentNode.left;
                continue;
            }
            if (currentNode.left.height < currentNode.right.height) {
                currentNode = currentNode.right;
                continue;
            }
            if (currentNode.left.height == currentNode.right.height) {
                markNodes(currentNode.left);
                currentNode = currentNode.right;
            }
        }
        currentNode.isUsed = true;
    }
}


public class Runner {
    private static final String INPUT = "input.txt";
    private static final String OUTPUT = "output.txt";

    public static void main(String[] args) {
        try {
            Tree currentTree = getTree(INPUT);
            currentTree.getMslHeight(currentTree.root);
            currentTree.getMaxMslList(currentTree.root);
            for (Tree.Node cn : currentTree.mslList) {
                if (cn.right != null) {
                    currentTree.markNodes(cn.right);
                }
                if (cn.left != null) {
                    currentTree.markNodes(cn.left);
                }
                cn.isUsed = true;
            }
            currentTree.getUnmarkedKeyList(currentTree.root);
            Collections.sort(currentTree.unmarkedKeyList);
            currentTree.printUsed(currentTree.root);
            currentTree.deleteNode(currentTree.unmarkedKeyList.get(0));
            currentTree.getKeyList(currentTree.root);
            currentTree.keyList.forEach(System.out::println);
            writeData(currentTree.keyList);

        } catch (IOException currentEx) {
            currentEx.printStackTrace();
        }
    }

    private static Tree getTree(String files) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(files)));
        Tree currentTree = new Tree();
        String currentElement = reader.readLine();
        while (currentElement != null) {
            currentTree.insert(Integer.parseInt(currentElement));
            currentElement = reader.readLine();
        }
        reader.close();
        return currentTree;
    }

    private static void writeData(List<Integer> keyList) throws IOException {
        FileWriter treeWriter = new FileWriter(new File(OUTPUT));
        for (int currentKey : keyList) {
            treeWriter.write(String.valueOf(currentKey));
            treeWriter.write(System.lineSeparator());
        }
        treeWriter.close();
    }
}
