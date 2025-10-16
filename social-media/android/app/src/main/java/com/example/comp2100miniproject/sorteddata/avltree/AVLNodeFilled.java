package com.example.comp2100miniproject.sorteddata.avltree;

import java.util.Comparator;

class AVLNodeFilled<T> extends AVLNode<T> {
	final AVLNode<T> left, right;
	final T value;
	private final int height, balance, size;
	public AVLNodeFilled(Comparator<T> comparator, T value, AVLNode<T> left, AVLNode<T> right) {
		super(comparator);
		this.value = value;
		this.left = left;
		this.right = right;
		this.size = left.size() + right.size() + 1;
		this.height = Math.max(left.height(), right.height())+1;

		// TODO: Overwrite the following line to correctly compute the tree's balance factor
		this.balance = left.height() - right.height();
	}

	public int height() {
		return height;
	}
	public int balanceFactor() {
		return balance;
	}
	public int size() {
		return size;
	}

	public String toString() {
		if (left instanceof AVLNodeEmpty<T> && right instanceof AVLNodeEmpty<T>)
			return value.toString();
		else
			return "%s -> (%s, %s)".formatted(value.toString(), left.toString(), right.toString());
	}

	public AVLNodeFilled<T> insert(T element) {
		// TODO: Complete this method
		int cmp = comparator.compare(element, value);
        AVLNode<T> newLeft = left, newRight = right;

        if (cmp < 0) {
            newLeft = left.insert(element);
        } else if (cmp > 0) {
            newRight = right.insert(element);
        } else {
            // duplicate values not inserted
            return this;
        }

        AVLNodeFilled<T> newNode = new AVLNodeFilled<T>(comparator, value, newLeft, newRight);

        // Rebalance
        if (newNode.balance > 1) {
            if (newLeft.balanceFactor() >= 0) {
                return newNode.rightRotate();
            } else {
                return new AVLNodeFilled<>(comparator, value,
                        ((AVLNodeFilled<T>) newLeft).leftRotate(),
                        newRight).rightRotate();
            }
        } else if (newNode.balance < -1) {
            if (newRight.balanceFactor() <= 0) {
                return newNode.leftRotate();
            } else {
                return new AVLNodeFilled<>(comparator, value, newLeft,
                        ((AVLNodeFilled<T>) newRight).rightRotate()).leftRotate();
            }
        }
        return newNode;
	}

	/**
	 * Executes a left rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> leftRotate() {
		// TODO: Complete this method
		AVLNodeFilled<T> newRoot = (AVLNodeFilled<T>) this.right;
        AVLNode<T> movedSubtree = newRoot.left;
        return new AVLNodeFilled<>(
                comparator,
                newRoot.value,
                new AVLNodeFilled<>(comparator, this.value, this.left, movedSubtree),
                newRoot.right
        );
	}

	/**
	 * Executes a right rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> rightRotate() {
		// TODO: Complete this method
        AVLNodeFilled<T> newRoot = (AVLNodeFilled<T>) this.left;
        AVLNode<T> movedSubtree = newRoot.right;
        return new AVLNodeFilled<>(
                comparator,
                newRoot.value,
                newRoot.left,
                new AVLNodeFilled<>(comparator, this.value, movedSubtree, this.right)
        );
	}

	public T getAtIndex(int i) {
		if (i < left.size()) return left.getAtIndex(i);
		else if (i == left.size()) return value;
		return right.getAtIndex(i - left.size() - 1);
	}

	public boolean contains(T element) {
		if (comparator.compare(value, element) < 0) {
			return right.contains(element);
		} else if (comparator.compare(element, value) < 0) {
			return left.contains(element);
		}
		return true;
	}

	public T get(T element) {
		if (comparator.compare(value, element) < 0) {
			return right.get(element);
		} else if (comparator.compare(element, value) < 0) {
			return left.get(element);
		}
		return value;
	}
}
