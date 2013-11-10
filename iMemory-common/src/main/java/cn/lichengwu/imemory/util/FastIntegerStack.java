package cn.lichengwu.imemory.util;

import java.util.EmptyStackException;

/**
 * A simple/fast/save memory stack.
 * </p>
 * only {@linkplain Integer} can store in this stack.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 8:45 PM
 */
public class FastIntegerStack {

    private int[] elementData;

    private int currentIndex = -1;


    /**
     * @param initCapacity
     */
    public FastIntegerStack(int initCapacity) {
        if (initCapacity <= 0) {
            throw new IllegalArgumentException("init capacity must greater than 0");
        }
        elementData = new int[initCapacity];
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return the object at the top of this stack (the last item
     *         of the <tt>Vector</tt> object).
     * @throws java.util.EmptyStackException if this stack is empty.
     */
    public int peek() {
        if (empty()) {
            throw new EmptyStackException();
        }
        return elementData[currentIndex];
    }

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return The object at the top of this stack (the last item
     *         of the <tt>Vector</tt> object).
     * @throws EmptyStackException if this stack is empty.
     */
    public int pop() {
        if (empty()) {
            throw new EmptyStackException();
        }
        int e = elementData[currentIndex];
        elementData[currentIndex--] = -1;
        return e;
    }

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param e the item to be pushed onto this stack.
     *
     * @return
     */
    public void push(int e) {
        ensureCapacity();
        elementData[++currentIndex] = e;
    }


    /**
     * Tests if this stack is empty.
     */
    public boolean empty() {
        return currentIndex < 0;
    }

    /**
     * auto enlarge the stack
     */
    private void ensureCapacity() {
        if (currentIndex == elementData.length - 1) {
            int[] newElementData = new int[elementData.length * 3 / 2];
            System.arraycopy(elementData, 0, newElementData, 0, elementData.length);
            elementData = newElementData;
        }
    }


}
