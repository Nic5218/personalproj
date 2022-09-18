package game2048;

import java.awt.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        // TODO: Fill in this constructor.

        Board myBoard = new Board(size); // create a board (like this?)
        this._board = myBoard;
        this._score = 0;
        this._maxScore = 0; //is it a bool or int?
        this._gameOver = false;

    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        // TODO: Fill in this constructor.
        /**This constructor creates a new instance of the game with a Board state that
         * reflects the given rawValues array. The instance variables of the object should be
         * updated based on the provided input. Note that rawValues is in row major form,
         * meaning it is indexed (row, column) and (0, 0) refers to the bottom left corner.*/
        Board myBoard2 = new Board(rawValues, score);
        this._board = myBoard2;
        this._score = score;
        this._maxScore = maxScore;
        this._gameOver = gameOver;

    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return _board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return _board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (_gameOver) {
            _maxScore = Math.max(_score, _maxScore);
        }
        return _gameOver;
    }

    /** Return the current score. */
    public int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        _score = 0;
        _gameOver = false;
        _board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        _board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Fill in this function.
        //check if two tiles have the same val, if do, merge; if diff, skip and check next pair
        //check 1. if empty space up, if same val as above
        _board.setViewingPerspective(side);
        //move without merge
        for (int arrInx = 0; arrInx < _board.size(); arrInx++) {
            for(int tileInx = _board.size() -1; tileInx >= 0 ; tileInx--) {
                Tile thisTile = _board.tile(arrInx, tileInx);
                if (thisTile == null) {
                    continue;
                } else {
                    int nextPos = findNextPos(_board, arrInx, tileInx);
                    if (nextPos >= tileInx) {
                        _board.move(arrInx, nextPos, thisTile);
                        changed = true;
                    }
                }
            }
        //move without merge ends here

            //merge tiles, start from top (row index 3)
            for(int mergeRow = _board.size() - 1; mergeRow >=0; mergeRow--) {
                Tile currTile = _board.tile(arrInx, mergeRow);
                int validPos = mergeRow - 1;
                Tile tileToMerge = _board.tile(arrInx, validPos);
                if (validPos < 0 || currTile == null || tileToMerge == null) {
                    break;
                }
                if (currTile.value() == tileToMerge.value()) {
                    _board.move(arrInx, mergeRow, tileToMerge);
                    _score += currTile.value() * 2;
                    //eliminate spaces after merging, start from the tile after the merged tile
                    movesUp(_board, arrInx, validPos);
                    changed = true;
                }
            }
        }
        //merge tiles ends here

        //set board back to North side
        _board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }


    /**helper: find next position to move to within the board range
     * NOTE: does not merge
     * return the row index of where to move*/
    public int findNextPos(Board b, int tileCol, int tileRow) {
        int nextPos = b.size() - 1;
        while (nextPos >= tileRow) {
            if (b.tile(tileCol, nextPos) == null) {
                break;
            }
            nextPos--;
        }
            return nextPos;

    }

    /**helper: given a tileToMerge and find out how many steps should the next tiles move up*/
    public static void movesUp(Board b, int arrInx, int validPos) {
        for (int moveSpace = validPos -1; moveSpace >=0; moveSpace--) {
            Tile tMovesUp = b.tile(arrInx, moveSpace);
            if (tMovesUp != null) {
                b.move(arrInx, moveSpace + 1, tMovesUp);
            } else if (tMovesUp == null) {
                break;
            } else {
                continue;
            }
        }

    }


    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        _gameOver = checkGameOver(_board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        /**you’ll want to use the tile(int col, int row) method
         * of the Board class. No other board methods are necessary.*/
        Boolean ifNull = false;
        Tile aTile = b.tile(0,0);
        if(aTile == null) {
            return true;
        } else {
            for(int tileCol = 0; tileCol < b.size(); tileCol++) {
                for(int tileRow = 0; tileRow < b.size(); tileRow++) {
                    if(b.tile(tileCol, tileRow) == null) {
                        ifNull = true;
                        return ifNull;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.

        for(int tileCol = 0; tileCol < b.size(); tileCol++) {
            for(int tileRow = 0; tileRow < b.size(); tileRow++) {
                if(b.tile(tileCol, tileRow) == null) {
                    continue;
                } else if(b.tile(tileCol, tileRow).value() == MAX_PIECE) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.

        if(emptySpaceExists(b)) {
            return true;
        } else {
            if(listOfNeighbors(b) == true) {
                return true;
            }
            b.setViewingPerspective(Side.EAST);
            if(listOfNeighbors(b) == true) {
                b.setViewingPerspective(Side.NORTH);
                return true;
            }

            b.setViewingPerspective(Side.SOUTH);
            if(listOfNeighbors(b) == true) {
                b.setViewingPerspective(Side.NORTH);
                return true;
            }

            b.setViewingPerspective(Side.WEST);
            if(listOfNeighbors(b) == true) {
                b.setViewingPerspective(Side.NORTH);
                return true;
            }
        }

        return false;
    }

    /**helper function that checks neighbors in one direction*/
    public static boolean listOfNeighbors(Board b) {
        for(int tileRow = 0; tileRow < b.size(); tileRow++) {
            for(int tileCol = 0; tileCol < b.size() - 1; tileCol++) {
                if(b.tile(tileCol, tileRow).value() == b.tile(tileCol + 1, tileRow).value()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns the model as a string, used for debugging. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    /** Returns whether two models are equal. */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    /** Returns hash code of Model’s string. */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
