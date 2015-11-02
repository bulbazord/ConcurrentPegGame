public class BoardSolver implements Runnable {
    public int initialPeg;
    public int numberOfPegs;
    public BoardSolver(int init, int num) {
        this.initialPeg = init;
        this.numberOfPegs = num;
    }
    
    public void run() {
        System.out.println("Solving board with initial peg " + initialPeg + " missing");
    }
}
