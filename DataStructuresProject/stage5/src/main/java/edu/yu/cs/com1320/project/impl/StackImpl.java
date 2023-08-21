package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    class StackItem<T>{
        T t;
        StackItem next;
        StackItem(T t){
            this.t = t;
            this.next = null;
        }
        void setT(T t){
            this.t = t;
        }
        T getT(){
            return this.t;
        }
        void setNext(StackItem item){
            this.next = item;
        }
        StackItem getNext(){
            return this.next;
        }
    }

    StackItem head;
    public StackImpl(){
        this.head = null;
    }

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        StackItem obj = new StackItem(element);
        if (this.head != null) {
            obj.setNext(head);
        }
        this.head = obj;
    }

    /**
     * removes and returns element at the top of the stack
     *
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if(this.head == null){
            return null;
        }
        StackItem popped = this.head;
        this.head = popped.getNext();
        return (T)popped.getT();
    }

    /**
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        return this.head != null ? (T)this.head.getT() : null;
    }

    /**
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        int size = 0;
        if(this.head == null){
            return 0;
        }else{
            size++;
        }
        StackItem current = this.head;
        while(current.getNext() != null){
            current = current.getNext();
            size++;
        }
        return size;
    }
}