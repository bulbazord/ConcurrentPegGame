import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrentPegGame {

    public int rows;

    public ConcurrentPegGame(int rows) {
        this.rows = rows;
    }

    /**
     * The initial method to begin finding a solution.
     * First we calculate the number of pegs and create a thread pool.
     * I had been suggested to do a multiple of the number of available
     * processors, and experimentally I found 1x would be best.
     * We don't need to check every start state because of symmetry, so we
     * only check approximately half of the board. We then submit those
     * start state solvers as tasks to run in the thread pool, then force
     * the thread pool to solve them all and terminate. We wait for it all
     * to terminate, and then find the one that yielded the smallest number
     * of moves. If something goes wrong, we do nothing. Otherwise, we print
     * out the relevant information according to the specification.
     */
    public void solve() {
        // Initialize useful data
        int numberOfPegs = (rows * (rows + 1)) / 2;
        ExecutorService boardSolverPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        List<Future<LinkedList<Move>>> results = new ArrayList<Future<LinkedList<Move>>>();
        int maxRowsToCheck = (rows / 2) + 1;
        int maxPegsToCheck = (maxRowsToCheck * (maxRowsToCheck + 1)) / 2;

        /* Throw jobs into the thread pool queue.
         * Force it to finish, and wait for it to finish all jobs. */
        for (int i = 0; i < maxPegsToCheck; i++) {
            BoardSolver bs = new BoardSolver(i, rows, numberOfPegs);
            results.add(boardSolverPool.submit(bs));
        }
        boardSolverPool.shutdown();
        try {
            boardSolverPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ie) {
            System.out.println("While waiting for it all to finish, we were interrupted");
        }


        // Find best result among the threads.
        int min = -1;
        LinkedList<Move> best = null;
        try {
            best = results.get(0).get();
            min = best.size();
            for (int i = 0; i < maxPegsToCheck; i++) {
                LinkedList<Move> res = results.get(i).get();
                res.pop(); // Get rid of the first "move", which is the removal of the first peg
                if (res.size() < min) {
                    best = res;
                    min = best.size();
                }
            }
        } catch (Exception e) {
            min = -1;
            best = null;
        }

        // Print out best result
        if (best != null) {
            System.out.println("(" + (numberOfPegs - 1 - best.size()) + ", " + (min + 1) + ")");
            for (Move move : best) {
                System.out.println(move);
            }
        }
    }

    public static void usage() {
        System.out.println("Usage: java ConcurrentPegGame -s <rows>");
        System.out.println("<rows> must be an integer between 5 and 10 inclusive");
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Error: Expected 2 arguments.");
            usage();
            System.exit(0);
        }

        if (!args[0].equals("-s")) {
            System.out.println("Error: First argument should be -s.");
            usage();
            System.exit(0);
        }

        int rows = 0;
        try {
            rows = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("Error: Expected -s arg to be an integer");
            usage();
            System.exit(0);
        }

        if ((rows < 5) || (rows > 10)) {
            System.out.println("Error: Invalid arguments");
            usage();
            System.exit(0);
        }

        ConcurrentPegGame cpg = new ConcurrentPegGame(rows);
        cpg.solve();
    }
}
