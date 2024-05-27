package org.example;

import java.util.Optional;
import java.util.Random;

public class RedBlackBST<K extends Comparable<K>, V> {
    private Node root;
    Node lastAccessedNode = null;

    int redCount = 0;
    int blackCount = 0;

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        private K key;
        private V val;
        private Node left;
        private Node right;
        private int size;
        private boolean color;

        public Node(K key, V val, int size, boolean color) {
            this.key = key;
            this.val = val;
            this.size = size;
            this.color = color;
        }
    }

    public double redProcent() {
        blackCount = 0;
        redCount = 0;
        redProcent(root);
        return (double) redCount / blackCount * 100;
    }

    private void redProcent(Node node) {
        if (node == null) {
            ++blackCount;
            return;
        }
        if (node.color) {
            ++redCount;
        } else {
            ++blackCount;
        }
        redProcent(node.left);
        redProcent(node.right);
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public void delete(K key) {
        if (!contains(key)) return;
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = delete(root, key);
        if (!isEmpty()) root.color = BLACK;
    }

    private Node delete(Node node, K key) {
        if (key.compareTo(node.key) < 0) {
            if (!isRed(node.left) && !isRed(node.left.left)) {
                node = moveRedLeft(node);
            }
            node.left = delete(node.left, key);
        } else {
            if (isRed(node.left)) {
                node = rotateRight(node);
            }
            if (key.compareTo(node.key) == 0 && (node.right == null)) {
                return null;
            }
            if (!isRed(node.right) && !isRed(node.right.left)) {
                node = moveRedRight(node);
            }
            if (key.compareTo(node.key) == 0) {
                node.val = get(node.right, min(node.right).get().key).get().val;
                node.key = min(node.right).get().key;
                node.right = deleteMin(node.right).get();
            } else {
                node.right = delete(node.right, key);
            }
        }
        return balance(node);
    }

    public void deleteMax() {
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = deleteMax(root).get();
        if (!isEmpty()) {
            root.color = BLACK;
        }
    }

    private Optional<Node> deleteMax(Node node) {
        if (isRed(node.left)) {
            node = rotateRight(node);
        }
        Optional<Node> optNode = Optional.ofNullable(node.left);
        if (optNode.isEmpty()) {
            return optNode;
        }
        if (!isRed(node.right) && !isRed(node.right.left)) {
            node = moveRedRight(node);
        }
        node.right = deleteMin(node.right).get();
        return Optional.ofNullable(balance(node));
    }

    public void deleteMin() {
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = deleteMin(root).get();
        if (!isEmpty()) {
            root.color = BLACK;
        }
    }

    private Optional<Node> deleteMin(Node node) {
        Optional<Node> optNode = Optional.ofNullable(node.left);
        if (optNode.isEmpty()) {
            return optNode;
        }
        if (!isRed(node.left) && !isRed(node.left.left)) {
            node = moveRedLeft(node);
        }
        node.left = deleteMin(node.left).get();
        return Optional.ofNullable(balance(node));
    }

    private Node balance(Node node) {
        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    public boolean isEmpty() {
        return isEmpty(root);
    }

    private boolean isEmpty(Node node) {
        return node == null;
    }

    private boolean isRed(Node node) {
        return node != null && node.color == RED;
    }

    private Node rotateLeft(Node oldRoot) {
        Node newRoot = oldRoot.right;
        oldRoot.right = newRoot.left;
        newRoot.left = oldRoot;
        newRoot.color = oldRoot.color;
        oldRoot.color = RED;
        newRoot.size = oldRoot.size;
        oldRoot.size = 1 + size(oldRoot.left) + size(oldRoot.right);
        return newRoot;
    }

    private Node rotateRight(Node oldRoot) {
        Node newRoot = oldRoot.left;
        oldRoot.left = newRoot.right;
        newRoot.right = oldRoot;
        newRoot.color = oldRoot.color;
        oldRoot.color = RED;
        newRoot.size = oldRoot.size;
        oldRoot.size = 1 + size(oldRoot.left) + size(oldRoot.right);
        return newRoot;
    }

    private void flipColors(Node node) {
        node.color = RED;
        node.left.color = BLACK;
        node.right.color = BLACK;
    }

    public int size() {
        return size(root);
    }

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    public V get(K key) {
        if (lastAccessedNode != null && lastAccessedNode.key.equals(key)) {
            return lastAccessedNode.val;
        } else {
            Optional<Node> node = get(root, key);
            return node.map(n -> n.val).orElse(null);
        }
    }

    private Optional<Node> get(Node node, K key) {
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                lastAccessedNode = node;
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public void put(K key, V val) {
        if (lastAccessedNode != null && lastAccessedNode.key.equals(key)) {
            lastAccessedNode.val = val;
        } else {
            root = put(root, key, val);
            root.color = BLACK;
        }
    }

    private Node put(Node node, K key, V val) {
        if (node == null) {
            lastAccessedNode = new Node(key, val, 1, RED);
            return lastAccessedNode;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, val);
        } else if (cmp > 0) {
            node.right = put(node.right, key, val);
        } else {
            node.val = val;
        }

        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }

        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    public K min() {
        if (root == null) {
            return null;
        }
        return min(root).get().key;
    }

    private Optional<Node> min(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        lastAccessedNode = node;
        return Optional.of(node);
    }

    public K max() {
        if (root == null) {
            return null;
        }
        return max(root).get().key;
    }

    private Optional<Node> max(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        lastAccessedNode = node;
        return Optional.of(node);
    }

    public static void main(String[] args) {
        RedBlackBST<Integer, String> tree = new RedBlackBST<>();
        Random random = new Random();

        // Добавляем 10000 случайных ключей и значений в дерево
        for (int i = 0; i < 10000; i++) {
            tree.put(random.nextInt(10000), "a");
        }

        // Выводим процент красных узлов в дереве
        System.out.println("Процент красных узлов: " + tree.redProcent() + "%");

        // Проверяем наличие некоторых ключей
        System.out.println("Содержит ли дерево ключ 5000? " + tree.contains(5000));
        System.out.println("Содержит ли дерево ключ 9999? " + tree.contains(9999));

        // Удаляем минимальный и максимальный ключи
        tree.deleteMin();
        tree.deleteMax();

        // Выводим минимальный и максимальный ключи
        System.out.println("Минимальный ключ: " + tree.min());
        System.out.println("Максимальный ключ: " + tree.max());

        // Удаляем случайный ключ
        int randomKey = random.nextInt(10000);
        tree.delete(randomKey);
        System.out.println("Удалён ключ: " + randomKey);
        System.out.println("Содержит ли дерево ключ " + randomKey + "? " + tree.contains(randomKey));
    }
}