package chess;

import java.util.Random;

/**
 * This class uses a game tree-based search utilizing the minimax algorithm with
 * alpha-beta pruning to evaluate chess boards.
 *
 * @author Brandon Cheshire, 5722350
 * @date December 14, 2017
 * @version 1.0
 */
public class Engine {
   
    Board chessBoard = new Board();
    Random random = new Random();
    int sideToMove = 1;
    MoveGenerator moveGen = new MoveGenerator();
    long[] boardHashes = new long[3000];
    long[] hashTable = new long[]{random.nextLong(), random.nextLong(),
        random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong(),
        random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong(),
        random.nextLong(), random.nextLong()};
    
    public Engine() { 
        //play();
    }   
    
    /**
     * This method returns the board with the best heuristic value, or null if
     * no such board is found.
     * @param board board to consider
     * @param depth search depth to consider
     * @param max player to consider
     * @return Board board board to return (may be null)
     */
    public Board miniMax(Board board, int depth, int max) {
        Board[] nextBoards = moveGen.nextMoves(board, max);
        Board result = null;
        if (max == 1) {
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < 250; i++) {
                Board next = nextBoards[i];
                if (next != null) {
                    if (!(next.inCheck(max) | isDraw(next.hash(hashTable)))) {
                        int computerScore = alphaBeta(next, depth - 1, 
                                Integer.MIN_VALUE, Integer.MAX_VALUE, -max);
                        if (computerScore > v) {
                            v = computerScore;
                            result = next;
                        }
                    }
                }
            }
        } else {
            int v = Integer.MAX_VALUE;
            for (int i = 0; i < 250; i++) {
                Board next = nextBoards[i];
                if (next != null) {
                    if (!(next.inCheck(max) | isDraw(next.hash(hashTable)))) {
                        int computerScore = alphaBeta(next, depth - 1, 
                                Integer.MIN_VALUE, Integer.MAX_VALUE, -max);
                        if (computerScore < v) {
                            v = computerScore;
                            result = next;
                        }
                    }
                }

            }
        }
        return result;
    } 
    
    /**
     * This method returns the heuristic of the best board, and ignores those
     * that cannot return a better value.
     * @param board board to consider
     * @param depth search depth to consider
     * @param α minimum score that the maximizing player is assured of
     * @param β maximum score that the minimizing player is assured of
     * @param max player to consider
     * @return int heuristic value of best board
     */
    private int alphaBeta(Board board, int depth, int α, int β, int max) {
        if (depth == 0) {
            return board.heuristic();
        }
        Board[] nextBoards = moveGen.nextMoves(board, max);
        if (max == 1) {
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < 250; i++) {
                Board next = nextBoards[i];
                if (next != null) {
                    v = Math.max(alphaBeta(next, depth - 1, α, β, -max), v);
                    α = Math.max(v, α);
                    if (β < α) {
                        break;
                    }
                }
            }
            return v;
        } else {
            int v = Integer.MAX_VALUE;
            for (int i = 0; i < 250; i++) {
                Board next = nextBoards[i];
                if (next != null) {
                    v = Math.min(alphaBeta(next, depth - 1, α, β, -max), v);
                    β = Math.min(β, v);
                    if (β < α) {
                        break;
                    }
                }
            }
            return v;
        }
    }    
    
    /**
     * This method checks whether a potential move results in 3 move draw.
     * @param hashValue hash value of board
     * @return boolean whether the move would result in a 3 move draw
     */
    public boolean isDraw(long hashValue) {
        int collisions = 0;
        for (int i = 0; i < 3000; i++) {
            if (boardHashes[i] == hashValue) {
                collisions++;
                if (collisions > 2) {
                    return true;
                }
            }
        }
        return false;
    }      

} 