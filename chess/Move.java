package chess;

/**
 * This class represents a move for a chess board.
 * @author Brandon Cheshire, 5722350
 * @date December 14, 2017
 * @version 1.0
 */
public class Move {

    long from;          // source square
    long to;            // destination square
    int  special = 0;   // special code for special moves

    public Move(long from, long to) {
        this.from = from;
        this.to = to;
    }     

    public Move(long from, long to, int special) {
        this.from = from;
        this.to = to;
        this.special = special;
    } 
    
} 
