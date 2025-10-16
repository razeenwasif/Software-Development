package com.example.comp2100miniproject.src.sorteddata.avltree;

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
		//this.balance = 0;
		//#SOLUTION START
		this.balance = left.height() - right.height();
		//#SOLUTION END
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
		//#SOLUTION START
		if (comparator.compare(element, value) < 0) {
			AVLNodeFilled<T> subtree = left.insert(element);
			AVLNodeFilled<T> result = new AVLNodeFilled<>(comparator, value, subtree, right);

			if (result.balance < 2)
				return result;
			if (subtree.balance < 0)
				result = new AVLNodeFilled<>(comparator, value, subtree.leftRotate(), right);
			return result.rightRotate();
		} else if (comparator.compare(element, value) > 0) {
			AVLNodeFilled<T> subtree = right.insert(element);
			AVLNodeFilled<T> result = new AVLNodeFilled<>(comparator, value, left, subtree);

			if (result.balance > -2)
				return result;
			if (subtree.balance > 0)
				result = new AVLNodeFilled<>(comparator, value, left, subtree.rightRotate());
			return result.leftRotate();
		}
		//#SOLUTION END
		return this;
	}



    public AVLNode<T> remove(T element) {

        if (comparator.compare(element, value) < 0) {
            AVLNode<T> subtree = left.remove(element);

            AVLNodeFilled<T> result = new AVLNodeFilled<>(comparator, value, subtree, right);

            if (result.getBalance() < 2)
                return result;
            if (subtree.getBalance() < 0)
                result = new AVLNodeFilled<>(comparator, value, ((AVLNodeFilled) subtree).rightRotate(), right);
            return result.leftRotate();
        }
        else if (comparator.compare(element, value) > 0) {

            AVLNode<T> subtree = right.remove(element);
            AVLNodeFilled<T> result = new AVLNodeFilled<>(comparator, value, left, subtree);

            if (result.balance > -2)
                return result;
            if (subtree.getBalance() > 0)
                result = new AVLNodeFilled<>(comparator, value, left, ((AVLNodeFilled) subtree).leftRotate());
            return result.rightRotate();
        }
        //#SOLUTION END
        return new AVLNodeEmpty<>(comparator);

    }

	/**
	 * Executes a left rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> leftRotate() {
		// TODO: Complete this method
		//#SOLUTION START
		AVLNodeFilled<T> rightFilled = (AVLNodeFilled<T>) right;
		AVLNodeFilled<T> newLeft = new AVLNodeFilled<>(comparator, value, left, rightFilled.left);
		//return new AVLNodeFilled<>(comparator, rightFilled.value, newLeft, rightFilled.right);
		//#SOLUTION END
		return this;
	}

	/**
	 * Executes a right rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> rightRotate() {
		// TODO: Complete this method
		//#SOLUTION START
		AVLNodeFilled<T> leftFilled = (AVLNodeFilled<T>) left;
		AVLNodeFilled<T> newRight = new AVLNodeFilled<>(comparator, value, leftFilled.right, right);
		//return new AVLNodeFilled<>(comparator, leftFilled.value, leftFilled.left, newRight);
		//#SOLUTION END
		return this;
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

    public int getBalance() { return balance; };
}
