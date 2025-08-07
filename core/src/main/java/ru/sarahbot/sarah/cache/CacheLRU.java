package ru.sarahbot.sarah.cache;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;


public class CacheLRU<K, V> implements CacheInstance<K, V> {
  private HashMap<K, LRUList<K, V>.Node<K, V>> lookupMap;
  private LRUList<K, V> queue;
  private Integer size;
  private Integer refreshTime; 

  public CacheLRU(Integer size) {
    this.size = size;
    this.lookupMap = new HashMap<>();
    this.queue = new LRUList<>();
  }

  public CacheLRU(Integer size, Integer minutesToRefresh) {
    this.size = size;
    this.lookupMap = new HashMap<>();
    this.queue = new LRUList<>();
    this.refreshTime = minutesToRefresh;
  }

  

  @Override
  public void put(K key, V value) {
    if (value == null || key == null) {
      throw new RuntimeException("null not allowed");
    }

    if (lookupMap.containsKey(key)) {
      queue.remove(lookupMap.get(key));
      lookupMap.remove(key);
    }

    LRUList<K, V>.Node<K, V> node = queue.new Node<>();
    node.setKey(key);
    node.setValue(value);
    node.setCreateDate(Instant.now());

    lookupMap.put(key, node);
    update(node);
  }

  @Override
  public V get(K key) {
    var node = lookupMap.get(key);
    if (node != null) {
      if(this.refreshTime != null) {
        Instant checkTime = Instant.now().minus(this.refreshTime, ChronoUnit.MINUTES);
        if(node.getCreateDate().isBefore(checkTime)) {
          lookupMap.remove(key);
          queue.remove(node);
          return null;
        }
      }

      update(node);
      return node.getValue();
    }
    return null;
  }

  void update(LRUList<K, V>.Node<K, V> node) {
    queue.remove(node);
    queue.addToHead(node);

    if (queue.getSize() > this.size) {
      var nodeToRemove = queue.removeFromTail();
      lookupMap.remove(nodeToRemove.getKey());
    }
  }


  class LRUList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;
    private int size = 0;

    class Node<K, V> {
      private Node<K, V> left;
      private Node<K, V> right;

      private K key;
      private V value;
      private Instant createDate;

      /**
       * @return the left
       */
      public Node<K, V> getLeft() {
        return left;
      }

      /**
       * @param left the left to set
       */
      public void setLeft(Node<K, V> left) {
        this.left = left;
      }

      /**
       * @return the right
       */
      public Node<K, V> getRight() {
        return right;
      }

      /**
       * @param right the right to set
       */
      public void setRight(Node<K, V> right) {
        this.right = right;
      }

      /**
       * @return the key
       */
      public K getKey() {
        return key;
      }

      /**
       * @param key the key to set
       */
      public void setKey(K key) {
        this.key = key;
      }

      /**
       * @return the value
       */
      public V getValue() {
        return value;
      }

      /**
       * @param value the value to set
       */
      public void setValue(V value) {
        this.value = value;
      }

      /**
       * @param time set creation date;
       */
      public void setCreateDate(Instant time){
        this.createDate = time;
      }


      public Instant getCreateDate() {
        return this.createDate;
      }
    }

    public Node<K, V> addToHead(Node<K, V> node) {
      if (this.head == null) {
        node.setLeft(null);
        node.setRight(null);
        this.head = node;
      } else {
        Node<K, V> nodeRight = this.head;
        nodeRight.setLeft(node);
        node.setRight(nodeRight);
        node.setLeft(null);
        this.head = node;
      }

      if (this.tail == null) {
        this.tail = this.head;
      }

      this.size++;
      return node;
    }

    public Node<K, V> removeFromTail() {
      if (this.tail == null) {
        return null;
      } else {
        return remove(this.tail);
      }
    }

    public Node<K, V> remove(Node<K, V> node) {
      if ((node.getLeft() == null && node.getRight() == null && node != this.head)
          || this.size == 0) {
        node.setLeft(null);
        node.setRight(null);
        return node;
      }

      var left = node.getLeft();
      var right = node.getRight();

      if (left != null && right != null) {
        left.setRight(right);
        right.setLeft(left);
      }
      if (left != null && right == null) {
        left.setRight(null);
        this.tail = left;
      }
      if (left == null && right != null) {
        right.setLeft(null);
        this.head = right;
      }
      if (left == null && right == null && this.head == node) {
        this.head = null;
      }
      if (left == null && right == null && this.tail == node) {
        this.tail = null;
      }


      this.size--;
      node.setLeft(null);
      node.setRight(null);

      return node;
    }

    public int getSize() {
      return this.size;
    }
  }
}
