import junit.framework.Assert;
import sorteddata.avltree.AVLTestBuilder;
import sorteddata.avltree.AVLTree;
import sorteddata.avltree.AVLTreeSlice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class AVLSliceTests {
	/*
	This code uses JUnit's parameterised unit testing to run your bank of test
	cases against eleven different implementations. On your local machine, none
	of your test cases will pass (since) the AVLTreeSlice implementation provided
	is just dummy code, regardless of the version number. However, on the CI,
	your test cases will be run properly, and once this task is complete, only
	one of the versions should pass.

	If you refer to the test cases from last week, they also used parameterised unit
	testing to simplify a long bank of similar test cases and reduce code duplication.
	Parameterised unit testing has a number of purposes.

	Please do not edit this template code, or the CI will have unpredictable results.
	 */
	@Parameterized.Parameters(name = "Version {0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}
		});
	}

	@Parameterized.Parameter
	public int version;
	@Before
	public void before() {
		AVLTreeSlice.setVersion(version);
	}

	// Please do not edit the code above this line.

	// TODO: Write your test cases below this line.

	/**
	 * This is an example test case to give you some inspiration.
	 * You may complete this test case according to the given TODO,
	 * or delete it -- the choice is yours.
	 */
	@Test(timeout=1000)
	public void exampleTest() {
		AVLTestBuilder<Integer> builder = new AVLTestBuilder<>(Comparator.<Integer>naturalOrder());
		builder.setTreeRoot(builder.make(5,
                                builder.make(3,
                                        builder.make(1),
                                        builder.make(4)),
                                builder.make(8,
                                        builder.make(6),
                                        builder.make(12))));
		AVLTree<Integer> tree = builder.getTree();

		// TODO Take a slice from this tree, and check its contents...
	}

    @Test(timeout=1000)
    public void creationTest() {
        AVLTestBuilder<Integer> builder = new AVLTestBuilder<>(Comparator.<Integer>naturalOrder());
        builder.setTreeRoot(builder.make(5,
                builder.make(3,
                        builder.make(1),
                        builder.make(4)),
                builder.make(8,
                        builder.make(6),
                        builder.make(12))));
        AVLTree<Integer> tree = builder.getTree();

        // TODO Take a slice from this tree, and check its contents...

        // Default state of slice when created
        AVLTreeSlice<Integer> slice = tree.slice(3);
        Iterator<Integer> sliceIterator = slice.getResult().iterator();
        Iterator<Integer> resultIterator = Arrays.asList(6, 8, 12).iterator();
        while (sliceIterator.hasNext()) {
            assertEquals(resultIterator.next(), sliceIterator.next());
        }
    }

    @Test(timeout=1000)
    public void backwardForwardTest() {
        AVLTestBuilder<Integer> builder = new AVLTestBuilder<>(Comparator.<Integer>naturalOrder());
        builder.setTreeRoot(builder.make(5,
                builder.make(3,
                        builder.make(1),
                        builder.make(4)),
                builder.make(8,
                        builder.make(6),
                        builder.make(12))));
        AVLTree<Integer> tree = builder.getTree();

        // TODO Take a slice from this tree, and check its contents...

        // Shift backward to start, then forward 1
        AVLTreeSlice<Integer> slice1 = tree.slice(3);
        int shiftDistance1 = slice1.shiftBackward(100);
        assertTrue(shiftDistance1 < 100);
        Iterator<Integer> sliceIterator1 = slice1.getResult().iterator();
        Iterator<Integer> resultIterator1 = Arrays.asList(1, 3, 4).iterator();
        while (sliceIterator1.hasNext()) {
            assertEquals(resultIterator1.next(), sliceIterator1.next());
        }
        shiftDistance1 = slice1.shiftForward(1);
        assertEquals(1, shiftDistance1);
        sliceIterator1 = slice1.getResult().iterator();
        resultIterator1 = Arrays.asList(3, 4, 5).iterator();
        while (sliceIterator1.hasNext()) {
            assertEquals(resultIterator1.next(), sliceIterator1.next());
        }

        // Shift forward to end, then backward 1
        AVLTreeSlice<Integer> slice2 = tree.slice(3);
        int shiftDistance2 = slice2.shiftForward(100);
        assertTrue(shiftDistance2 < 100);
        Iterator<Integer> sliceIterator2 = slice2.getResult().iterator();
        Iterator<Integer> resultIterator2 = Arrays.asList(6, 8, 12).iterator();
        while (sliceIterator2.hasNext()) {
            assertEquals(resultIterator2.next(), sliceIterator2.next());
        }
        shiftDistance2 = slice2.shiftBackward(1);
        assertEquals(1, shiftDistance2);
        sliceIterator2 = slice2.getResult().iterator();
        resultIterator2 = Arrays.asList(5, 6, 8).iterator();
        while (sliceIterator2.hasNext()) {
            assertEquals(resultIterator2.next(), sliceIterator2.next());
        }
    }

    @Test(timeout=1000)
    public void addElementWhenSliceAtStartTest() {
        AVLTestBuilder<Integer> builder = new AVLTestBuilder<>(Comparator.<Integer>naturalOrder());
        builder.setTreeRoot(builder.make(5,
                builder.make(3,
                        builder.make(1),
                        builder.make(4)),
                builder.make(8,
                        builder.make(6),
                        builder.make(12))));
        AVLTree<Integer> tree = builder.getTree();

        // TODO Take a slice from this tree, and check its contents...

        // Add element to tree when slice at start
        AVLTreeSlice<Integer> slice3 = tree.slice(3);
        slice3.shiftBackward(100);
        tree.insert(2);
        Iterator<Integer> sliceIterator3 = slice3.getResult().iterator();
        Iterator<Integer> resultIterator3 = Arrays.asList(1, 2, 3).iterator();
        while (sliceIterator3.hasNext()) {
            assertEquals(resultIterator3.next(), sliceIterator3.next());
        }
        tree.insert(-1);
        sliceIterator3 = slice3.getResult().iterator();
        resultIterator3 = Arrays.asList(-1, 1, 2).iterator();
        while (sliceIterator3.hasNext()) {
            assertEquals(resultIterator3.next(), sliceIterator3.next());
        }
        tree.insert(7);
        sliceIterator3 = slice3.getResult().iterator();
        resultIterator3 = Arrays.asList(-1, 1, 2).iterator();
        while (sliceIterator3.hasNext()) {
            assertEquals(resultIterator3.next(), sliceIterator3.next());
        }
    }

    @Test(timeout=1000)
    public void addElementWhenSliceAtEndTest() {
        AVLTestBuilder<Integer> builder = new AVLTestBuilder<>(Comparator.<Integer>naturalOrder());
        builder.setTreeRoot(builder.make(5,
                builder.make(3,
                        builder.make(1),
                        builder.make(4)),
                builder.make(8,
                        builder.make(6),
                        builder.make(12))));
        AVLTree<Integer> tree = builder.getTree();

        // TODO Take a slice from this tree, and check its contents...

        // Add element to tree when slice at end
        AVLTreeSlice<Integer> slice4 = tree.slice(3);
        slice4.shiftForward(100);
        tree.insert(10);
        Iterator<Integer> sliceIterator4 = slice4.getResult().iterator();
        Iterator<Integer> resultIterator4 = Arrays.asList(8, 10, 12).iterator();
        while (sliceIterator4.hasNext()) {
            assertEquals(resultIterator4.next(), sliceIterator4.next());
        }
        tree.insert(11);
        sliceIterator4 = slice4.getResult().iterator();
        resultIterator4 = Arrays.asList(10, 11, 12).iterator();
        while (sliceIterator4.hasNext()) {
            assertEquals(resultIterator4.next(), sliceIterator4.next());
        }
        tree.insert(9);
        sliceIterator4 = slice4.getResult().iterator();
        resultIterator4 = Arrays.asList(10, 11, 12).iterator();
        while (sliceIterator4.hasNext()) {
            assertEquals(resultIterator4.next(), sliceIterator4.next());
        }
        tree.insert(13);
        sliceIterator4 = slice4.getResult().iterator();
        resultIterator4 = Arrays.asList(11, 12, 13).iterator();
        while (sliceIterator4.hasNext()) {
            assertEquals(resultIterator4.next(), sliceIterator4.next());
        }
    }

    @Test(timeout=1000)
    public void edgeCaseTest() {
        AVLTestBuilder<Integer> builder = new AVLTestBuilder<>(Comparator.<Integer>naturalOrder());
        builder.setTreeRoot(builder.make(5,
                builder.make(3,
                        builder.make(1),
                        builder.make(4)),
                builder.make(8,
                        builder.make(6),
                        builder.make(12))));
        AVLTree<Integer> tree = builder.getTree();

        // TODO Take a slice from this tree, and check its contents...

        // Edge case: Slice is larger than tree, then add additional elements until tree is larger than slice
        AVLTreeSlice<Integer> slice5 = tree.slice(8);
        Iterator<Integer> sliceIterator5 = slice5.getResult().iterator();
        Iterator<Integer> resultIterator5 = Arrays.asList(1, 3, 4, 5, 6, 8, 12).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
        tree.insert(7);
        sliceIterator5 = slice5.getResult().iterator();
        resultIterator5 = Arrays.asList(1, 3, 4, 5, 6, 7, 8, 12).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
        tree.insert(9);
        sliceIterator5 = slice5.getResult().iterator();
        resultIterator5 = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 12).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
        tree.insert(13);
        sliceIterator5 = slice5.getResult().iterator();
        resultIterator5 = Arrays.asList(4, 5, 6, 7, 8, 9, 12, 13).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
        slice5.shiftBackward(2);
        sliceIterator5 = slice5.getResult().iterator();
        resultIterator5 = Arrays.asList(1, 3, 4, 5, 6, 7, 8, 9).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
        tree.insert(2);
        sliceIterator5 = slice5.getResult().iterator();
        resultIterator5 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
        tree.insert(0);
        sliceIterator5 = slice5.getResult().iterator();
        resultIterator5 = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7).iterator();
        while (sliceIterator5.hasNext()) {
            assertEquals(resultIterator5.next(), sliceIterator5.next());
        }
    }

	/**
	 * As a hint, don't forget about this syntax. This test case will pass
	 * if and only if the code inside raises an ArithmeticException at some
	 * point. If no exception is raised, the test case fails.
	 */
	@Test(timeout=100, expected = ArithmeticException.class)
	public void hintExceptionTest() {
		int y = 5 / 0;
	}

}
