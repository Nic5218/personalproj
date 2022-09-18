package deque;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.Assert.*;


/** Performs some basic linked list deque tests. */
public class LinkedListDequeTest {

    /** You MUST use the variable below for all of your tests. If you test
     * using a local variable, and not this static variable below, the
     * autograder will not grade that test. If you would like to test
     * LinkedListDeques with types other than Integer (and you should),
     * you can define a new local variable. However, the autograder will
     * not grade that test. Please do not import java.util.Deque here!*/

    public static Deque<Integer> lld = new LinkedListDeque<Integer>();
    public static Deque<String> strLld = new LinkedListDeque();

    @Test
    /** Adds a few things to the list, checks that isEmpty() is correct.
     * This is one simple test to remind you how junit tests work. You
     * should write more tests of your own.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        // provided tests
        assertTrue("A newly initialized LLDeque should be empty", lld.isEmpty());
        lld.addFirst(0);

        assertFalse("lld1 should now contain 1 item", lld.isEmpty());

        lld = new LinkedListDeque<Integer>(); //Assigns lld equal to a new, clean LinkedListDeque!
    }

    /** Adds an item, removes an item, and ensures that dll is empty afterwards. */
    @Test
    public void addRemoveTest() {
        lld.addFirst(4);
        assertFalse(lld.isEmpty());
        lld.addLast(3);
        lld.removeFirst();
        lld.removeFirst();
        assertTrue("adds an item and remove it will result in an empty list", lld.isEmpty());
    }

    /** Make sure that removing from an empty LinkedListDeque does nothing */
    @Test
    public void removeEmptyTest() {
        lld.removeFirst();
        assertTrue("remove first does nothing to empty list", lld.isEmpty());

        lld.removeLast();
        assertTrue("remove last does nothing to empty list", lld.isEmpty());
    }

    /** Make sure your LinkedListDeque also works on non-Integer types */
    @Test
    public void multipleParamsTest() {
        assertTrue(strLld.isEmpty());
        strLld.addFirst("music");
        assertEquals("music",strLld.get(0));
        strLld.addFirst("birds");
        strLld.addLast("SUPERMAN!!!");
        strLld.printDeque();
        strLld.removeLast();
        strLld.removeLast();
        strLld.removeFirst();
        assertTrue(lld.isEmpty());
    }

    /** Make sure that removing from an empty LinkedListDeque returns null */
    @Test
    public void emptyNullReturn() {

        assertEquals(null, lld.removeFirst());
        assertEquals(null, lld.removeLast());
    }

    /** Make sure that .size() returns right values for empty and non-empty lists */
    @Test
    public void sizeTest() {
        assertEquals(0, lld.size());
        lld.addFirst(2);
        lld.addLast(6);
        assertEquals(2, lld.size());
        lld = new LinkedListDeque<Integer>();
    }

    /** Adds items at front and back, then prints list, adds more then prints again, then prints empty list */
    @Test
    public void printTest() {
        lld.addFirst(2);
        lld.addLast(6);
        lld.addFirst(31);
        lld.addLast(2048);
        lld.printDeque();
        lld.addLast(31);
        lld.addFirst(-23);
        lld.addFirst(79);
        lld.printDeque();
        lld = new LinkedListDeque<Integer>();
        lld.printDeque();
    }

    /** Adds items randomly, tests that get retrieves right item */
    @Test
    public void getTest() {
        lld.addFirst(5);
        lld.addFirst(2);
        lld.addLast(34);
        int second = lld.get(2);
        assertEquals(34, second);
        lld = new LinkedListDeque<Integer>();
    }

    /** tests specific LLD function getRecursive */
    @Test
    public void getRecursiveTest() {
        lld.addFirst(5);
        lld.addFirst(2);
        lld.addLast(34);
        int third = (int) ((LinkedListDeque)lld).getRecursive(2);
        assertEquals(34, third);
        lld = new LinkedListDeque<Integer>();
    }

    /** tests equals against multiple lists */
    @Test
    public void equalsTest() {
        Deque<Integer> lld2 = new LinkedListDeque<Integer>();
        lld2.addLast(1);
        lld2.addLast(2);
        lld.addFirst(1);
        lld.addLast(2);
        assertTrue(lld.equals(lld2)); // make sure add order doesn't affect result

        Deque<Integer> lld3 = new LinkedListDeque<Integer>();
        lld3.addFirst(1);
        lld3.addLast(4);
        assertFalse(lld.equals(lld3)); // basic test

        Deque<Integer> lld4 = new LinkedListDeque<Integer>();
        lld4.addLast(1);
        lld4.addLast(2);
        lld4.addLast(3);
        assertFalse(lld.equals(lld4)); // make sure length is accounted for

        Deque<Integer> lld5 = new LinkedListDeque<Integer>();
        assertFalse(lld.equals(lld5)); // make sure that it doesn't equal an empty list

        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void unlinkTestAddFRemoveF() {
        lld.addFirst(1);
        lld.addFirst(2);
        lld.addFirst(3);
        lld.addFirst(4);
        lld.removeFirst();
        lld.removeFirst();
        lld.removeFirst();
        lld.removeFirst();
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void unlinkTestAddFRemoveL() {
        lld.addFirst(1);
        lld.addFirst(2);
        lld.addFirst(3);
        lld.addFirst(4);
        lld.removeLast();
        lld.removeLast();
        lld.removeLast();
        lld.removeLast();
        lld = new LinkedListDeque<Integer>();
    }
    @Test
    public void unlinkTestAddLRemoveL() {
        lld.addLast(1);
        lld.addLast(2);
        lld.addLast(3);
        lld.addLast(4);
        lld.removeLast();
        lld.removeLast();
        lld.removeLast();
        lld.removeLast();
        lld = new LinkedListDeque<Integer>();
    }
}
