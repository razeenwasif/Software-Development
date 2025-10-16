import sorteddata.avltree.AVLTestBuilder;
import sorteddata.avltree.AVLTree;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import java.util.Comparator;
import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AVLIteratorTests {
	/**
	 * This is an example test. You should definitely look at AVLTestFactory to see
	 * how we produce the trees to test (and for an explanation as to why we don't
	 * rely on the AVLTree class). You may remove or modify this test case as you wish,
	 * and add as many other test cases as you wish, provided you stay within the 20
	 * function call limit.
	 */

	// TODO: write your other test cases here
    @Test()
    public void nonEmptyTreeTest() {
        AVLTestBuilder<String> source = new AVLTestBuilder<String>(Comparator.naturalOrder());
        source.setTreeRoot(source.make("egg",
                source.make("banana",
                        source.make("apple",
                                source.empty(),
                                source.make("bag")),
                        source.make("date",
                                source.make("cherry"),
                                source.empty())),
                source.make("honeydew",
                        source.make("fig",
                                source.empty(),
                                source.make("guava")),
                        source.make("kiwi"))
        ));
        AVLTree<String> tree = source.getTree();

        // First test
        Iterator<String> it1 = tree.getRange(null, 5, false);
        assertTrue(it1.hasNext());
        assertEquals("apple", it1.next());
        assertEquals("bag", it1.next());
        assertEquals("banana", it1.next());
        assertEquals("cherry", it1.next());
        assertEquals("date", it1.next());
        assertEquals("egg", it1.next());
        assertFalse("Iterator should be empty", it1.hasNext());

        // Second test
        Iterator<String> it2 = tree.getRange("banana", 4, true);
        assertEquals("banana", it2.next());
        assertEquals("bag", it2.next());
        assertEquals("apple", it2.next());
        assertFalse("Iterator should be empty", it2.hasNext());

        // Third test
        Iterator<String> it3 = tree.getRange("honeydew", 4, true);
        assertEquals("honeydew", it3.next());

        // Fourth test
        Iterator<String> it4 = tree.getRange("honeydew", 4, false);
        assertEquals("honeydew", it4.next());
    }

    @Test()
    public void emptyTreeTest() {
        AVLTestBuilder<String> source = new AVLTestBuilder<String>(Comparator.naturalOrder());
        source.setTreeRoot(source.empty());
        AVLTree<String> tree = source.getTree();

        // Empty tree
        Iterator<String> it1 = tree.getRange(null, -1, false);
        assertFalse("Iterator should be empty", it1.hasNext());
    }
}
