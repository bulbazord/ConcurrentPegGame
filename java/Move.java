public class Move {
    public int originalPosition;
    public int newPosition;
    public int removedPiece;

    public Move(int origP, int newP, int removedP) {
        this.originalPosition = origP;
        this.newPosition = newP;
        this.removedPiece = removedP;
    }

    public String toString() {
        return "(" + this.originalPosition + ", " + this.newPosition + ")";
    }

    public boolean equals(object move) {
        if (move == null) return false;
        if (move == this) return true;
        if (!(move instanceof Move)) return false;
        Move kek = (Move) move;
        if (    kek.originalPosition == this.originalPosition
                && kek.newPosition == this.newPosition
                && kek.removedPiece == this.removedPiece)
        {
            return true;
        }
        return false;
    }
}
