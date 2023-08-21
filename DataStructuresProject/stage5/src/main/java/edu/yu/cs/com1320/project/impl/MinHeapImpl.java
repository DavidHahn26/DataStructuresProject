package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
    int size = 2;

    class Entry implements Comparable<Entry> {
        @Override
        public int compareTo(Entry o) {
            return 0;
        }
    }

    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[this.size];
        this.count = 0;
    }


    //left child is 2k
    //right child is 2k+1
    //parent is k/2 (int division)

    @Override
    public void reHeapify(E element) {
        int i = getArrayIndex(element);
        reHeapify(i);
    }

    private int reHeapify(int i) {
        if ((i > 1 && isGreater(i, getParent(i))) && (i * 2 < this.count && isGreater(getLeft(i), i) && isGreater(getRight(i), i))) { //may have to change direction
            //everything is good
            return i;
        } else {
            if (i > 1 && isGreater(getParent(i), i)) {
                swap(i, getParent(i));
                return reHeapify(getParent(i));
            } else if (i * 2 < this.count && (isGreater(i, getRight(i)) || isGreater(i, getLeft(i)))) {
                if (isGreater(getRight(i), getLeft(i))) {
                    swap(i, getLeft(i));
                    return reHeapify(getLeft(i));
                } else {
                    swap(i, getRight(i));
                    return reHeapify(getRight(i));
                }
            } else {
                return i;
            }
        }
        /*upHeap(i);
        downHeap(i);
        return 0;*/
    }

    @Override
    protected int getArrayIndex(E element) {
        for (int i = 0; i < this.size; i++) {
            if (this.elements[i] != null) {
                if (this.elements[i].equals(element)) {
                    return i;
                }
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        E[] newHeap = (E[]) new Comparable[this.size * 2];
        for (int i = 0; i < this.size; i++) {
            newHeap[i] = this.elements[i];
        }
        this.size *= 2;
        this.elements = newHeap;
    }

    private int getParent(int i) {
        return i / 2;
    }

    private int getLeft(int i) {
        return i * 2;
    }

    private int getRight(int i) {
        return (i * 2) + 1;
    }
}