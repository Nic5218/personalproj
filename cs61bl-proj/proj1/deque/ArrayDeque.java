package deque;

import java.lang.reflect.Array;

public class ArrayDeque<T> implements Deque<T> {

    /**size is the number of FULL boxes*/
    private int size;
    private T[] arr;

    /**index of the first: where the next available spot for addFirst*/
    private int first;
    private int last;

    /**index of where the current first VALUE is*/
    private int currFirst;

    private int currLast;
    //ArrayDeque's under the hood structure is array
    // access the elements by index


    /**constructor: Creates an empty array deque*/
    public ArrayDeque() {
        this.arr = (T[]) new Object[8];
        this.size = 0;
        this.first = 0;
        this.last = 1;
        this.currFirst = 0;
        this.currLast = 1;
    }

    @Override
    public void addFirst(T item) {
        //if your front item is at position zero, and you addFirst,
        // the new front should loop back around to the end of the array
        // (so the new front item in the deque will be the last item in the underlying array).

        // REMEMBER TO UPDATE SIZE
        if (size == arr.length) {
            this.sizeUp();
        }
        if (first == 0) {
            arr[first] = item;
            currFirst = first;
            first = arr.length - 1;
            if (size == 0) {
                currLast = currFirst;
            }
            size += 1;
        } else {
            //this.sizeUp(); // now arr updated to be the newly sized array
            arr[first] = item;
            currFirst = first;
            first--;
            size += 1;
        }
    }


    @Override
    public void addLast(T item) {
        if (size == 0) {
            //last, currLast = 1
            //last = 1;
            arr[1] = item;
            currLast = 1;
            last = 2;
            currFirst = 1;
            size += 1;
        } else if (size() == arr.length - 1 && currFirst == 1) {
            arr[0] = item;
            currLast = 0;
            last++;
            size += 1;
        } else if (size < arr.length) {
            arr[last] = item;
            currLast = last;
            if (last == arr.length - 1) {
                last = 0;
            } else {
                last++;
            }
            size += 1;
        } else {
            this.sizeUp();
            arr[last] = item;
            currLast = last;
            last++;
            size += 1;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int k = 0; k < size; k++) {
            System.out.print(this.get(k) + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        //TODO: need fix: remove empty does nothing
        //remove the value from that index position
        //increment the pointer
        if (size == 0) {
            return null;
        }
        T returnedFirst = (T) new Object();
        //adLast() & nothing else
        if (size == 1) {
            returnedFirst = arr[currFirst];
            arr[currFirst] = null;
            first = 0;
            currFirst = 0;
            last = 1;
            currLast = 1;
            size -= 1;

            //when curr first is at end of the list
        } else if (currFirst == arr.length - 1) {
            returnedFirst = arr[currFirst];
            arr[currFirst] = null;
            first = currFirst;
            currFirst = 0;
            size -= 1;
        } else {
            returnedFirst = arr[currFirst];
            arr[currFirst] = null;
            first = currFirst;
            currFirst++;
            //first ++;
            size -= 1;
        }

        if (arr.length >= 16 && size < 0.25 * arr.length) {
            this.sizeDown();
        }

        return returnedFirst;
    }

    @Override
    public T removeLast() {
        //TODO: need fix: remove empty does nothing
        if (size == 0) {
            return null;
        }
        //case 1: when size == 1
        //case 2: when currLast == 0;
        T returnedLast = (T) new Object();
        if (size == 1) {
            returnedLast = arr[currLast];
            arr[currLast] = null;
            currLast = 1;
            last = 1;
            currFirst = 0;
            first = 0;
        } else if (currLast == 0) {
            returnedLast = arr[currLast];
            arr[currLast] = null;
            currLast = arr.length - 1;
            last = 0;
        } else {
            returnedLast = arr[currLast];
            arr[currLast] = null;
            currLast--;
            last = currLast + 1;
        }
        size -= 1;

        if (arr.length >= 16 && size < 0.25 * arr.length) {
            this.sizeDown();
        }

        return returnedLast;
    }

    @Override
    // the order you put in the elem
    public T get(int index) {
        //TODO: need fix -> fixed? with else if case
        if (index < 0 || index >= arr.length) {
            return null;
        } else {
            return arr[(currFirst + index) % arr.length];
            //NOTE: first points to the pos where next addF will go, so we need to +1
        }
    }

    /**Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic Tâ€™s equals method) in the same order.*/
    public boolean equals(Object o) {
        if (o instanceof Deque) {
            if (this.size() != ((Deque<T>) o).size()) {
                return false;
            }
            for (int iter = 0; iter < size; iter++) {
                if (!this.get(iter).equals(((Deque<T>) o).get(iter))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

//    public boolean isEmpty() {
//        if (size == 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**Helper: to resize an ArrayDeque*/
    private void sizeUp() {
        if (size >= arr.length) {
            T[] sizedUpArr = (T[]) new Object[arr.length * 2];
//            for (int i = 0; i < arr.length; i++) {
//                //TODO: RHS: copy over with the get index (in the order of addF and addL)
//                //issue: addF, addL mixed together
//                sizedUpArr[i] = arr[i];
//            }

            //first copy over addFirsts (except index 0)
            int currFirst2 = currFirst;
            for (int f = 0; f < arr.length - currFirst; f++) {
                sizedUpArr[f] = arr[currFirst2];
                currFirst2++;
            }

            //2nd copy over addLasts (including index 0)
            int startPos = arr.length - currFirst; //add from index 0 to currLast
            for (int l = 0; l < size - (arr.length - currFirst); l++) {
                sizedUpArr[startPos] = arr[l];
                startPos++;
            }
            arr = sizedUpArr;
            //pointers are correct
            currFirst = 0;
            currLast = size - 1;
            first = arr.length - 1; //first at the end of list
            last = size; //after copying all elem, last points after all the current elem
        }
    }

    private void sizeDown() {
        T[] sizedDown = (T[]) new Object[arr.length / 2];
        int currFirst2 = currFirst;
        for (int f = 0; f < sizedDown.length; f++) {
            sizedDown[f] = arr[currFirst2];
            currFirst2++;
        }

        //2nd copy over addLasts (including index 0)
        int startPos = arr.length - currFirst; //add from index 0 to currLast
        for (int l = 0; l < size - (arr.length - currFirst); l++) {
            sizedDown[startPos] = arr[l];
            startPos++;
        }

        arr = sizedDown;
        currFirst = 0;
        currLast = size - 1;
        first = arr.length - 1;
        last = size;

    }

}