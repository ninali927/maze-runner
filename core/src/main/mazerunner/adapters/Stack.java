package main.mazerunner.adapters;

import main.exceptions.EmptyException;

/**
 * Stack ADT.
 *
 * @param <T> base type.
 */
public interface Stack<T> {

  /**
   * Peeks at top value without removing it.
   *
   * @return the value at the top of this stack.
   * @throws EmptyException when empty() == true.
   */
  T top() throws EmptyException;

  /**
   * Removes the top element.
   *
   * @throws EmptyException when empty() == true.
   */
  void pop() throws EmptyException;

  /**
   * Adds a new element to top of stack.
   *
   * @param t value to be added to the top of this stack.
   *          Post: top() == t
   */
  void push(T t);

  /**
  * Checks if empty.
  *
  * @return true if this stack is empty and false otherwise.
  */
  boolean isEmpty();

  /**
   * Returns the number of elements in this stack.
   *
   * @return the number of elements in this stack.
   */
  int size();
}
