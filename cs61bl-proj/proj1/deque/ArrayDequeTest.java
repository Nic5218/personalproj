package deque;

import jh61b.junit.In;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/* Performs some basic array deque tests. */
public class ArrayDequeTest {

    /** You MUST use the variable below for all of your tests. If you test
     * using a local variable, and not this static variable below, the
     * autograder will not grade that test. If you would like to test
     * ArrayDeques with types other than Integer (and you should),
     * you can define a new local variable. However, the autograder will
     * not grade that test. */

    public static Deque<Integer> ad = new ArrayDeque<Integer>();

    // copied from provided tests (LinkedList)
    @Test
    /** Adds a few things to the list, checks that isEmpty() is correct.
     * This is one simple test to remind you how junit tests work. You
     * should write more tests of your own.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        // provided tests
        assertTrue("A newly initialized ArrayDeque should be empty", ad.isEmpty());
        ad.addFirst(0);
        ad.addFirst(234);
        ad.addLast(65);
        ad.addLast(24);

        assertFalse("ad should now contain 1 item", ad.isEmpty());

        ad = new ArrayDeque<Integer>(); //Assigns lld equal to a new, clean LinkedListDeque!


    }

    /** Adds an item, removes an item, and ensures that dll is empty afterwards. */
    @Test
    public void addRemoveTest() {
        ad.addFirst(4);
        ad.addFirst(5);
        ad.removeFirst();
        ad.removeLast();
        assertTrue("adds an item and remove it will result in an empty list", ad.isEmpty());

        ad = new ArrayDeque<Integer>();

    }
    /** Make sure that removing from an empty ArrayDeque
     *  does nothing */
    @Test
    public void removeEmptyTest() {
        ad.removeFirst();
        assertTrue("remove first does nothing to empty list", ad.isEmpty());

        ad.removeLast();
        assertTrue("remove last does nothing to empty list", ad.isEmpty());

        ad = new ArrayDeque<Integer>();

    }
    /** Make sure your ArrayDeque also works on non-Integer types */
    @Test
    public void multipleParamsTest() {
        Deque<String> ad1 = new ArrayDeque<String>();
        assertTrue(ad1.isEmpty());
        ad1.addFirst("music");
        assertEquals("music", ad1.get(0));
        ad1.removeLast();
        assertTrue(ad1.isEmpty());


    }
    /** Make sure that removing from an empty ArrayDeque returns null */
    @Test
    public void emptyNullReturn() {
        assertEquals(null, ad.removeFirst());

        assertEquals(null, ad.removeLast());

        ad = new ArrayDeque<Integer>();

    }

    // more self-added tests
    @Test
    public void sizeTest() {
        assertEquals(0, ad.size());
        ad.addFirst(2);
        ad.addLast(6);
        assertEquals(2, ad.size());

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void printDequeTest() {
        ad.addFirst(5);
        ad.addLast(34);
        ad.printDeque();
        ad.addFirst(6);
        ad.addFirst(7);
        ad.addLast(8);
        ad.addLast(9);
        ad.printDeque();

        ad = new ArrayDeque<Integer>();

        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        ad.addFirst(8);
        ad.addFirst(9);
        ad.addFirst(10);
        ad.addLast(11);
        ad.printDeque();

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void getTest() {
        ad.addFirst(5);
        ad.addLast(34);
        assertEquals((Integer) 5, ad.get(0));
        assertEquals((Integer) 34, ad.get(1));
        ad.addFirst(17);
        ad.addLast(18);
        assertEquals((Integer) 17, ad.get(0));
        assertEquals((Integer) 18, ad.get(3));

        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void equalsTest() {
        Deque<Integer> ad0 = new ArrayDeque<Integer>();
        Deque<Integer> ad1 = new ArrayDeque<Integer>();
        ad0.addFirst(1);
        ad1.addFirst(1);
        ad0.addFirst(3);
        ad1.addFirst(3);
        ad0.addLast(9);
        ad1.addLast(9);

        assertTrue(ad0.equals(ad1));

        ad1.removeFirst();
        assertFalse(ad0.equals(ad1));
        ad1.addFirst(10);
        assertFalse(ad0.equals(ad1));

        Deque<Integer> lld = new LinkedListDeque<Integer>();
        lld.addFirst(1);
        lld.addFirst(3);
        lld.addLast(9);

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void addFirst() {
        ad.addFirst(27);
        ad.addFirst(17);
        ad.addFirst(14);
        ad.addLast(11);

        assertEquals((Integer) 14, ad.get(0));
        assertEquals((Integer) 17, ad.get(1));

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void addLast() {
        ad.addLast(27);
        ad.addLast(17);
        ad.addLast(14);
        ad.addFirst(11);
        // 11, 27 17 14

        assertEquals((Integer) 11, ad.get(0));
        assertEquals((Integer) 27, ad.get(1));
        assertEquals((Integer) 14, ad.get(3));

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void removeFirst() {
        ad.addFirst(5);
        ad.addLast(34);
        ad.addFirst(17);
        ad.addLast(18);
        assertEquals((Integer) 17, ad.removeFirst());
        assertEquals((Integer) 5, ad.removeFirst());

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void removeLast() {
        ad.addFirst(5);
        ad.addLast(34);
        ad.addFirst(17);
        ad.addLast(18);
        assertEquals((Integer) 18, ad.removeLast());
        assertEquals((Integer) 34, ad.removeLast());

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void sizeUpTest() {
        //TODO: failed test
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);

        ad.addLast(4);
        ad.addLast(5);

        ad.addFirst(6);

        ad.addLast(7);
        ad.addLast(8);

        ad.printDeque();

        ad.addFirst(9);
        ad.addLast(10);

        ad.printDeque();

        assertEquals((Integer) 9, ad.get(0));
        assertEquals((Integer) 10, ad.get(9));

        ad = new ArrayDeque<Integer>();

        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        ad.addFirst(8);
        ad.addFirst(9);
        ad.printDeque();

        assertEquals((Integer) 1, ad.get(8));

        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void sizeDownTest() {
        //TODO: failed test
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        ad.addFirst(8);
        ad.addFirst(9);

        //5
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();

        ad.printDeque();

        //2
        ad.removeFirst();
        ad.removeFirst();

        assertEquals((Integer) 1, ad.get(1));

        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void addGetTest() {
        //add 0 through n-1 with add last, get(0) -> first add last
        //TODO: failed test
        Deque<Integer> ad1 = new ArrayDeque<Integer>();

        ad1.addLast(0);
        ad1.addLast(1);
        ad1.addLast(2);
        ad1.addLast(3);
        ad1.addLast(4);
        ad1.addLast(5);
        ad1.addLast(6);
        ad1.addLast(7);

        //adding when exceeding len 8
        ad1.addLast(8);

        assertEquals((Integer) 0, ad1.get(0));

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void AGGetTest() {
        Deque<Integer> ad1 = new ArrayDeque<Integer>();
        ad1.addLast(0);
        assertEquals((Integer) 0, ad1.get(0));
        ad1.removeLast();
        ad1.addLast(3);
        ad1.addLast(4);
        ad1.removeFirst();

        assertEquals((Integer) 4, ad1.get(0));
        //all good above
        //  4

        ad1.addLast(7);
        ad1.addFirst(8);
        ad1.addLast(9);
        ad1.addLast(10);
        ad1.addFirst(11);
        ad1.addFirst(12);
        assertEquals((Integer) 7, ad1.get(4));
        //all good above
        // 12 11 8 4 >>7 9 10

        ad1.addLast(14);
        assertEquals((Integer) 12, ad1.removeFirst());
        assertEquals((Integer) 11, ad1.removeFirst());
        ad1.addLast(17);
        //(12) (11) (8) 4 7 9 10 14 >>>17

        assertEquals((Integer) 8, ad1.removeFirst());
        //TODO: test failed here: Index -1 out of bounds for length 8
        assertEquals((Integer) 17, ad1.removeLast());
        //4 7 9 10 14

        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void AGGetTest2() {
        ad.addLast(0);
        ad.removeLast();

        ad.addLast(2);
        ad.removeFirst();
        ad.addFirst(4);
        ad.removeLast();
        ad.addLast(6);
        assertEquals((Integer) 6, ad.get(0));
        assertEquals((Integer) 6, ad.removeFirst());
        ad.addLast(9);
        ad.addLast(10);
        ad.removeLast();
        assertEquals((Integer) 9, ad.get(0));
        ad.addLast(13);
        ad.addLast(14);
        ad.removeFirst();
        ad.addLast(16);
        assertEquals((Integer) 13, ad.get(0));

        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void AGGetTest3() {

        ad.addFirst(0);
        assertEquals((Integer)0, ad.removeLast());

        ad.addFirst(2);
        assertEquals((Integer)2, ad.get(0));      //==> 2
        ad.addLast(4);
        assertEquals((Integer)2, ad.removeFirst());

        assertEquals((Integer)4, ad.removeFirst());

        ad.addLast(7);
        ad.addLast(8);
        assertEquals((Integer)7, ad.removeFirst());

        ad.addFirst(10);
        ad.addLast(11);
        ad.addFirst(12);
        assertEquals((Integer)11, ad.get(3));
        assertEquals((Integer)12, ad.removeFirst());

        assertEquals((Integer)11, ad.removeLast());


        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void addLremoveF() {
        ad.addLast(0);
        ad.addLast(1);
        assertEquals((Integer) 0, ad.removeFirst());    // ==> 0
        ad.addLast(3);
        ad.addLast(4);
        ad.addLast(5);
        assertFalse(ad.isEmpty());
        ad.addLast(7);
        ad.addLast(8);
        ad.addLast(9);
        // remove F 7 times
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        assertTrue(ad.isEmpty());
        ad.addLast(465);
        assertEquals((Integer)465, ad.get(0));


        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void addFremoveF() {
        ad.addFirst(0);
        ad.addFirst(1);
        assertEquals((Integer) 1, ad.removeFirst());    // ==> 0
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        assertFalse(ad.isEmpty());
        ad.addFirst(7);
        ad.addFirst(8);
        ad.addFirst(9);
        // remove F 7 times
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        ad.removeFirst();
        assertTrue(ad.isEmpty());
        ad.addFirst(465);
        assertEquals((Integer)465, ad.get(0));

        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void AGGet4() {
        ad.addLast(0);
        assertEquals((Integer)0, ad.get(0));      //==> 0
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        assertEquals((Integer)4, ad.removeFirst());     //==> 4
        assertEquals((Integer)3, ad.removeFirst());     //==> 3
        assertEquals((Integer)0, ad.removeLast());       //==> 0
        assertEquals((Integer)2, ad.removeFirst());      //==> 2
        ad.addLast(9);
        ad.addFirst(10);
        assertEquals((Integer) 10, ad.removeFirst());      //==> 10
        ad.addFirst(12);
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void AGGet5() {
        ad.addLast(0);
        assertEquals((Integer) 0, ad.removeLast());

        ad.addFirst(2);
        assertEquals((Integer) 2, ad.removeLast());

        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        assertEquals((Integer) 4, ad.removeLast());

        ad.addLast(8);
        assertEquals((Integer) 8, ad.removeLast());
        assertEquals((Integer) 6, ad.removeFirst());

    }

}
