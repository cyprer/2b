package bstmap;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private Node root;
    private int size;
    private HashSet<K> keySet = new HashSet<K>();

    @Override
    public Iterator<K> iterator() {
        return null;
    }

   private class Node {
        private K key;
        private V value;
        private Node left,right;
        private int size;

        public Node(K k,V v) {
            this.key = k;
            this.value = v;
            keySet.add(k);
        }
    }

    public BSTMap() {
        root =null;
        size = 0;
    }

    public void clear() {
        root = null;
        size = 0;
        keySet.clear();
    }

    public int size() {
        return size;
    }

    public boolean containsKey(K key) {
        return containsKey(root,key);
    }

    private boolean containsKey(Node T,K key) {
        if (T == null) return false;
        if (key == null) throw new IllegalArgumentException("invalid key");
        if (T.key.equals(key)) return true;
        else if (T.key.compareTo(key) < 0) return containsKey(T.right,key);
        else if (T.key.compareTo(key) > 0) return containsKey(T.left,key);
        else return false;
    }

    public V get(K key) {
        return get(root,key);
    }

    private V get(Node T,K key) {
        if (!containsKey(key)) return null;
        if (T == null) return null;
        if (T.key.equals(key)) return T.value;
        else if (T.key.compareTo(key) < 0) return get(T.right,key);
        else if (T.key.compareTo(key) > 0) return get(T.left,key);
        else return null;
    }

    public void put(K key,V value) {
        root = put(root, key, value);
        size++;
    }

    private Node put(Node T,K key,V value) {
        if (T == null) {
            keySet.add(key); // 只有在成功插入新节点时才更新 keySet
            return new Node(key, value);
        }
        if (T.key.equals(key)) throw new IllegalArgumentException("already have this key");
        else if (T.key.compareTo(key) < 0) T.right = put(T.right, key, value);
        else if (T.key.compareTo(key) > 0) T.left = put(T.left, key, value);
        return T;
    }

    public Set<K> keySet(){
        return keySet;
    }
    public V remove(K key) {
        if (!containsKey(key)) throw new IllegalArgumentException("invalid key");
        remove(root, key);
        size--;
        return get(key);
    }

    private Node remove(Node T, K key) {
        if (T == null) return null;
        int cmp = key.compareTo(T.key);
        if (cmp < 0) {
            T.left = remove(T.left, key);
        } else if (cmp > 0) {
            T.right = remove(T.right, key);
        } else if (T.left != null && T.right != null) {
            // 两个子节点：找到右子树的最小值来替换当前节点
            Node cur = T;
            Node temp = findMin(T.right);
            T = T.right;
            return cur;
        } else {
            // 一个或零个子节点：直接用子节点替换当前节点
            Node ret = (T.left != null) ? T.left : T.right;
            T.left = T.right = null;
            return ret;
        }
        return T;
    }

    private Node findMin(Node T) {
        if (T == null) return null;
        if (T.left == null) return T;
        return findMin(T.left);
    }

    public V remove(K key, V value) {
        if (!containsKey(key)) throw new IllegalArgumentException("invalid key");
        if (get(key) != value) throw new IllegalArgumentException("invalid value");
        else if (get(key) == value) {
            remove(key);
        }
        return get(key);
    }

    public void printInOrder(){
        printInOrderByMidTravel(root);
    }

    private void printInOrderByMidTravel(Node x){
        if (x == null) return;
        printInOrderByMidTravel(x.left);
        System.out.println("key: " + x.key + " value: " + x.value);
        printInOrderByMidTravel(x.right);
    }
}























