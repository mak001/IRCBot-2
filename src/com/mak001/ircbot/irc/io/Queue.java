package com.mak001.ircbot.irc.io;

import java.util.LinkedList;

/**
 * Based off the Queue from PIRC, the only methods that were copied straight
 * from it are {@link #hasNext()}, {@link #clear()}, and {@link #size()}. The
 * rest was re-written to run with a LinkedList instead of a Vector
 * 
 * @author MAK001
 */
public class Queue {
	private LinkedList<String> queue = new LinkedList<String>();

	/**
	 * Creates a Queue
	 */
	public Queue() {
	}

	/**
	 * Adds a string to the Queue.
	 * 
	 * @param string
	 *            - the string to add to the Queue
	 */
	public void add(String string) {
		synchronized (queue) {
			queue.add(string);
			queue.notify();
		}
	}

	/**
	 * Adds a string to the front of the Queue.
	 * 
	 * @param string
	 *            - the string to add to the Queue
	 */
	public void addToFront(String string) {
		synchronized (queue) {
			queue.add(0, string);
			queue.notify();
		}
	}

	/**
	 * Returns the Object at the front of the Queue. This Object is then removed
	 * from the Queue. If the Queue is empty, then this method shall block until
	 * there is an Object in the Queue to return.
	 * 
	 * @return The next item from the front of the queue.
	 */
	public String next() {
		String s = null;

		synchronized (queue) {
			if (queue.size() == 0) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}
			s = queue.getFirst();
			queue.remove(0);
		}
		return s;
	}

	/**
	 * Returns true if the Queue is not empty. If another Thread empties the
	 * Queue before <b>next()</b> is called, then the call to <b>next()</b>
	 * shall block until the Queue has been populated again.
	 * 
	 * @return True only if the Queue not empty.
	 */
	public boolean hasNext() {
		return (this.size() != 0);
	}

	/**
	 * Clears the contents of the Queue.
	 */
	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}

	/**
	 * Returns the size of the Queue.
	 * 
	 * @return The current size of the queue.
	 */
	public int size() {
		return queue.size();
	}

}
