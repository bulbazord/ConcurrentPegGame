import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;

public class BoardSolver implements Callable<LinkedList<Move>> {

    private static final int[] TOTAL_PEGS_TABLE = {
        0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153
    };

    public int initialPeg;
    public int numberOfRows;
    public int totalPegs;
    public int numberOfPegs;
    public int currentBest;
    public boolean[] board;

    public LinkedList<Move> moves;
    public LinkedList<Move> bestMoves;

    public BoardSolver(int init, int rows, int num) {
        this.initialPeg = init;
        this.numberOfRows = rows;
        this.totalPegs = num;
        this.numberOfPegs = totalPegs;
        this.currentBest = 0;
        this.board = new boolean[totalPegs];
        Arrays.fill(board, true);

        this.moves = new LinkedList<Move>();
    }
    
    /**
     * The method that the thread starts by executing.
     *  Applies the first move, recursively solves, returns best moves.
     *
     *  @return A linkedlist, treated as a stack, representing the best moves.
     */
    public LinkedList<Move> call() {
        Move move = new Move(initialPeg, initialPeg, initialPeg);
        applyMove(move);
        recursiveSolve(move);
        return this.bestMoves;
    }

    /**
     * A method to get the peg number based on the row and displacement.
     *
     * @param row The row of the peg
     * @param displacement The displacement in the row
     *
     * @return The peg number
     */
    public int getPegNumber(int row, int displacement) {
        if (row < 0 || row >= TOTAL_PEGS_TABLE.length || displacement < 0 ||
                displacement > row) {
            return -1;
        }
        return TOTAL_PEGS_TABLE[row] + displacement;
    }

    /**
     * Method to find the row of a peg based on the peg number.
     *
     * @param currentPeg The current peg's number
     *
     * @return The row of the current peg.
     */
    public int getRow(int currentPeg) {
        int i;
        for (i = 0; TOTAL_PEGS_TABLE[i] <= currentPeg; i++);
        return i - 1;
    }

    /**
     * Method to find displacement of a peg within a row
     *
     * @param currentPeg The current peg's number
     *
     * @return The displacement of a peg within a row
     */
    public int getDisplacement(int currentPeg) {
        return currentPeg - TOTAL_PEGS_TABLE[getRow(currentPeg)];
    }

    /**
     * Method that applies a move to the board of pegs.
     *
     * @param move The move to be applied.
     *
     */
    public void applyMove(Move move) {
        board[move.originalPosition]    = false;
        board[move.newPosition]         = true;
        board[move.removedPiece]        = false;
        numberOfPegs--;
        moves.push(move);
    }

    /**
     * The move to undo.
     *
     * @param move The move to undo
     */
    public void reverseMove(Move move) {
        board[move.originalPosition]    = true;
        board[move.newPosition]         = false;
        board[move.removedPiece]        = true;
        numberOfPegs++;
        moves.pop();
    }

    /**
     * Method that takes a move that was made and tries to check every possible
     * move that can be made based on the current state of the board.
     *
     * @param previousMove The last move that was made.
     */
    public void recursiveSolve(Move previousMove) {
        if (numberOfPegs > currentBest) {
            boolean validMove = false;
            for (int i = 0; i < totalPegs; i++) {
                if (board[i]) {
                    validMove |= testNeighborMoves(i);
                }
            }

            if (!validMove) {
                currentBest = numberOfPegs;
                bestMoves = new LinkedList<Move>();
                Iterator<Move> iterator = moves.iterator();
                while (iterator.hasNext()) {
                    bestMoves.push(iterator.next());
                }
            }
        }
        reverseMove(previousMove);
    }

    /**
     * Method that test the potential moves around a given peg.
     *
     * @param currentPeg The peg to move
     *
     * @return True if the peg has moves. False otherwise.
     */
    public boolean testNeighborMoves(int currentPeg) {
        boolean validMove = false;

        int r = getRow(currentPeg);
        int d = getDisplacement(currentPeg);
        int land;
        int jump;

        land = getPegNumber(r - 2, d);
        jump = getPegNumber(r - 1, d);
        validMove |= testAndApply(currentPeg, land, jump);

        land = getPegNumber(r, d + 2);
        jump = getPegNumber(r, d + 1);
        validMove |= testAndApply(currentPeg, land, jump);

        land = getPegNumber(r + 2, d + 2);
        jump = getPegNumber(r + 1, d + 1);
        validMove |= testAndApply(currentPeg, land, jump);

        land = getPegNumber(r + 2, d);
        jump = getPegNumber(r + 1, d);
        validMove |= testAndApply(currentPeg, land, jump);

        land = getPegNumber(r, d - 2);
        jump = getPegNumber(r, d - 1);
        validMove |= testAndApply(currentPeg, land, jump);

        land = getPegNumber(r - 2, d - 2);
        jump = getPegNumber(r - 1, d - 1);
        validMove |= testAndApply(currentPeg, land, jump);

        return validMove;
    }

    /**
     * Method to check for an apply moves.
     *
     * @param current The location where the peg is.
     * @param next The location where the peg will be.
     * @param jump The location that is being jumped over.
     *
     * @return True if the the move is valid. False otherwise.
     */
    public boolean testAndApply(int current, int next, int jump) {
        Move move = new Move(current, next, jump);
        if (testMove(move)) {
            applyMove(move);
            recursiveSolve(move);
            return true;
        }
        return false;
    }

    /**
     * Method to check the validity of a move.
     *
     * @param move The move to check.
     *
     * @return True if the move is valid. False otherwise.
     */
    public boolean testMove(Move move) {
        int lR = getRow(move.newPosition);
        if (lR < 0 || lR >= numberOfRows) {
            return false;
        }
        int lD = getDisplacement(move.newPosition);
        if (lD < 0 || lR > lR) {
            return false;
        }

        return (board[move.originalPosition]
                && !board[move.newPosition]
                && board[move.removedPiece]);
    }

}
