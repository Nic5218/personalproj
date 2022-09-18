package deque;

public class LinkedListDeque<T> implements Deque<T>{
    private class Node<T> {
        private T item;
        private Node next;
        private Node prev;
        /** constructor: creates a node of type specified at instantiation */
        public Node (T i, Node p, Node n) {
            item = i;
            next = n;
            prev = p;
        }
        /** helper function for getRecursive: finds indexed position recursively, returns node */
        public Node getRecursiveHelper(int index) {
            if (index == 0) {
                return this;
            }
            return this.next.getRecursiveHelper(index-1);
        }
    }
    private Node sentinel;
    private int size = 0;
    private Node pointer;

    /**constructor: create an empty Linked list deque*/
    public LinkedListDeque() {
        sentinel = new Node(null, sentinel, sentinel);
    }

    /**NOTE: add and remove: no looping or recursion*/
    @Override
    public void addFirst(T item) {
        Node first = new Node(item, this.sentinel, this.sentinel.next);
        this.sentinel.next = first;
        if (this.size == 0) {
            this.sentinel.prev = first;
        } else {
            first.next.prev = first;
        }
        if (size == 0) {
            first.next = this.sentinel;
            Node last = first;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        Node last = new Node(item,this.sentinel.prev,this.sentinel);
        this.sentinel.prev = last;

        if (this.size == 0) {
            this.sentinel.next = last;
            last.prev = sentinel;
        } else {
            last.prev.next = last;
        }
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int iter = 0; iter < size; iter++) {
            String message = this.get(iter) + " ";
            System.out.print(message);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (this.sentinel.next == null) {
            return null;
        }
        T first = (T) this.sentinel.next.item;
        this.sentinel.next = this.sentinel.next.next;
        this.sentinel.next.prev = this.sentinel;
        if (size > 0) {
            size--;
        }
        return first;
    }

    @Override
    public T removeLast() {
        if (this.sentinel.prev == null) {
            return null;
        }
        T last = (T) this.sentinel.prev.item;
        this.sentinel.prev = this.sentinel.prev.prev;
        this.sentinel.prev.next = this.sentinel;
        if (size > 0) {
            size--;
        }
        return last;
    }

    /**use iteration here*/
    @Override
    public T get(int index) {
        if (index >= this.size()) {
            return null;
        }
        pointer = sentinel.next;
        T toReturn = null;
        for (int counter = index; counter>=0;counter--) {
            toReturn = (T) pointer.item;
            pointer = pointer.next;
        }
        pointer = sentinel.next;
        return toReturn;
    }

    public T getRecursive(int index) {
        if (index >= this.size()) {
            return null;
        }
        Node location = sentinel.next.getRecursiveHelper(index);
        return (T) location.item;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Deque) {
            if (this.size() != ((Deque<T>)o).size()) {
                return false;
            }
            for (int iter = 0; iter < size; iter++) {
                if (!this.get(iter).equals(((Deque<T>)o).get(iter))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


}
