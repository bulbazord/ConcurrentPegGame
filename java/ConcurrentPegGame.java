public class ConcurrentPegGame {

    public int rows;

    public ConcurrentPegGame(int rows) {
        this.rows = rows;
    }

    public void solve() {
        System.out.println("Solving!");
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
