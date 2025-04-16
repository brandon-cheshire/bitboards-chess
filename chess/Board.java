package chess;

import java.util.Scanner;

/**
 * This class represents a chess board in a chess system.
 * @author Brandon Cheshire, 5722350
 * @date December 14, 2017
 * @version 1.0
 */
public class Board {      
    
    long wPawns =   0x000000000000FF00L;
    long wKnights = 0x0000000000000042L;
    long wBishops = 0x0000000000000024L;
    long wRooks =   0x0000000000000081L;
    long wQueens =  0x0000000000000010L;
    long wKing =    0x0000000000000008L;
    long bPawns =   0x00FF000000000000L;
    long bKnights = 0x4200000000000000L;
    long bBishops = 0x2400000000000000L;
    long bRooks =   0x8100000000000000L;
    long bQueens =  0x1000000000000000L;
    long bKing =    0x0800000000000000L;
    long whites =   wPawns|wKnights|wBishops|wRooks|wQueens|wKing;
    long blacks =   bPawns|bKnights|bBishops|bRooks|bQueens|bKing;
    long occupied = whites| blacks;
    long empty =    ~occupied;
    long epSquare = 0;
    MoveGenerator   moveGen = new MoveGenerator();
    Scanner         scan = new Scanner(System.in);    
    
    static final int KWEIGHT = 10000;
    static final int QWEIGHT = 900;
    static final int RWEIGHT = 500;    
    static final int BWEIGHT = 300;
    static final int NWEIGHT = 300;
    static final int PWEIGHT = 100;
    
    static final int[] PAWNTABLE =       {0,  0,  0,  0,  0,  0,  0,  0,
                                         50, 50, 50, 50, 50, 50, 50, 50,
                                         10, 10, 20, 30, 30, 20, 10, 10,
                                          5,  5, 10, 25, 25, 10,  5,  5,
                                          0,  0,  0, 20, 20,  0,  0,  0,
                                          5, -5,-10,  0,  0,-10, -5,  5,
                                          5, 10, 10,-20,-20, 10, 10,  5,
                                          0,  0,  0,  0,  0,  0,  0,  0};
    
    static final int[] KNIGHTTABLE =   {-50,-40,-30,-30,-30,-30,-40,-50,
                                        -40,-20,  0,  0,  0,  0,-20,-40,
                                        -30,  0, 10, 15, 15, 10,  0,-30,
                                        -30,  5, 15, 20, 20, 15,  5,-30,
                                        -30,  0, 15, 20, 20, 15,  0,-30,
                                        -30,  5, 10, 15, 15, 10,  5,-30,
                                        -40,-20,  0,  5,  5,  0,-20,-40,
                                        -50,-40,-30,-30,-30,-30,-40,-50,};
    
    static final int[] BISHOPTABLE =   {-20,-10,-10,-10,-10,-10,-10,-20,
                                        -10,  0,  0,  0,  0,  0,  0,-10,
                                        -10,  0,  5, 10, 10,  5,  0,-10,
                                        -10,  5,  5, 10, 10,  5,  5,-10,
                                        -10,  0, 10, 10, 10, 10,  0,-10,
                                        -10, 10, 10, 10, 10, 10, 10,-10,
                                        -10,  5,  0,  0,  0,  0,  5,-10,
                                        -20,-10,-10,-10,-10,-10,-10,-20,};
    static final int[] ROOKTABLE   =   {0,  0,  0,  0,  0,  0,  0,  0,
                                        5, 10, 10, 10, 10, 10, 10,  5,
                                       -5,  0,  0,  0,  0,  0,  0, -5,
                                       -5,  0,  0,  0,  0,  0,  0, -5,
                                       -5,  0,  0,  0,  0,  0,  0, -5,
                                       -5,  0,  0,  0,  0,  0,  0, -5,
                                       -5,  0,  0,  0,  0,  0,  0, -5,
                                        0,  0,  0,  5,  5,  0,  0,  0};
    
    static final int[] QUEENTABLE  =   {-20,-10,-10, -5, -5,-10,-10,-20,
                                        -10,  0,  0,  0,  0,  0,  0,-10,
                                        -10,  0,  5,  5,  5,  5,  0,-10,
                                         -5,  0,  5,  5,  5,  5,  0, -5,
                                          0,  0,  5,  5,  5,  5,  0, -5,
                                        -10,  5,  5,  5,  5,  5,  0,-10,
                                        -10,  0,  5,  0,  0,  0,  0,-10,
                                        -20,-10,-10, -5, -5,-10,-10,-20};
    
    static final int[] MIDKINGTABLE =  {-30,-40,-40,-50,-50,-40,-40,-30,
                                        -30,-40,-40,-50,-50,-40,-40,-30,
                                        -30,-40,-40,-50,-50,-40,-40,-30,
                                        -30,-40,-40,-50,-50,-40,-40,-30,
                                        -20,-30,-30,-40,-40,-30,-30,-20,
                                        -10,-20,-20,-20,-20,-20,-20,-10,
                                         20, 20,  0,  0,  0,  0, 20, 20,
                                         20, 30, 10,  0,  0, 10, 30, 20};
    
    static final int[] LATEKINGTABLE = {-50,-40,-30,-20,-20,-30,-40,-50,
                                        -30,-20,-10,  0,  0,-10,-20,-30,
                                        -30,-10, 20, 30, 30, 20,-10,-30,
                                        -30,-10, 30, 40, 40, 30,-10,-30,
                                        -30,-10, 30, 40, 40, 30,-10,-30,
                                        -30,-10, 20, 30, 30, 20,-10,-30,
                                        -30,-30,  0,  0,  0,  0,-30,-30,
                                        -50,-30,-30,-30,-30,-30,-30,-50};   
    
    public boolean[] castlingRights = {true,true,true,true};

    private static final int BQ = 0;
    private static final int BK = 1;
    private static final int WQ = 2;
    private static final int WK = 3;
            
    /**
     * This constructor generates a default initial board configuration.
     */
    public Board ( ) {   
    }    
    
    /**
     * This constructor takes in sets of longs and booleans to generate a custom
     * initial chess board (ex. "0,0,0,0,0,0,0x000000000000FF00L,0,0,0,0,0,0,true,
     * true,true,true" to generate a board with only white pawns).
     * @param bP positions of black pawns
     * @param bN positions of black knights
     * @param bB positions of black bishops
     * @param bR positions of black rooks
     * @param bQ position of black queen
     * @param bK position of black king
     * @param wP positions of white pawns
     * @param wN positions of white knights
     * @param wB positions of white bishops
     * @param wR positions of white rooks
     * @param wQ position of white queen
     * @param wK position of white king
     * @param eP position of en passant square
     * @param BQ black queen side castling rights
     * @param BK black kingside castling rights
     * @param WQ white queen side castling rights
     * @param WK white kingside castling rights
     */
    public Board (long bP, long bN, long bB, long bR,long bQ, long bK, 
            long wP, long wN, long wB, long wR, long wQ, long wK, long eP,
            boolean BQ, boolean BK, boolean WQ, boolean WK) {        
        bPawns =   bP;
        bKnights = bN;
        bBishops = bB;
        bRooks =   bR;
        bQueens =  bQ;
        bKing =    bK;
        wPawns =   wP;
        wKnights = wN;
        wBishops = wB;
        wRooks =   wR;
        wQueens =  wQ;
        wKing =    wK;
        whites =   wP | wK | wB | wR | wN | wQ | wK;
        blacks =   bP | bK | bB | bR | bN | bQ | bK;
        occupied = whites | blacks;
        empty =    ~occupied; 
        epSquare = eP;
        castlingRights[0] = BQ;
        castlingRights[1] = BK;
        castlingRights[2] = WQ;
        castlingRights[3] = WK;
    }      
        
     /**
     * This method makes a move on a board.
     * @param move move to make
     */
    public void makeMove(Move move) {
        long from = move.from;
        long to = move.to;
        int special = move.special;
        long fromTo = from | to;
        epSquare = 0;

        if (special == 1) {
            wPawns ^= from;
            whites ^= from;
            wQueens ^= to;
            whites ^= to;            
        } else if (special == 2) {
            wPawns ^= from;
            whites ^= from;
            wBishops ^= to;
            whites ^= to;
        } else if (special == 3) {
            wPawns ^= from;
            whites ^= from;
            wKnights ^= to;
            whites ^= to;
        } else if (special == 4) {
            wPawns ^= from;
            whites ^= from;
            wRooks ^= to;
            whites ^= to;
        } else if (special == 5) {
            bPawns ^= from;
            blacks ^= from;
            bQueens ^= to;
            blacks ^= to;
        } else if (special == 6) {
            bPawns ^= from;
            blacks ^= from;
            bBishops ^= to;
            blacks ^= to;
        } else if (special == 7) {
            bPawns ^= from;
            blacks ^= from;
            bKnights ^= to;
            blacks ^= to;
        } else if (special == 8) {
            bPawns ^= from;
            blacks ^= from;
            bRooks ^= to;
            blacks ^= to;
        } else if (special == 9) { //black queenside castling
            bKing ^= (0x0800000000000000L | 0x2000000000000000L);
            bRooks ^= (0x8000000000000000L | 0x1000000000000000L);
            blacks ^= ((0x0800000000000000L | 0x2000000000000000L) | 
                    (0x8000000000000000L | 0x1000000000000000L));
            castlingRights[BQ] = false;
            castlingRights[BK] = false;
        } else if (special == 10) { //black kingside castling
            bKing ^= (0x0800000000000000L | 0x0200000000000000L);
            bRooks ^= (0x0100000000000000L | 0x0400000000000000L);
            blacks ^= ((0x0800000000000000L | 0x0200000000000000L) | 
                    (0x0100000000000000L | 0x0400000000000000L));
            castlingRights[BQ] = false;
            castlingRights[BK] = false;
        } else if (special == 11) { //white queenside castling
            wKing ^= (0x08L | 0x20L);
            wRooks ^= (0x80L | 0x10L);
            whites ^= ((0x08L | 0x20L) | (0x80L | 0x10L));
            castlingRights[WQ] = false;
            castlingRights[WK] = false;
        } else if (special == 12) { //white kingside castling
            wKing ^= (0x08L | 0x02L);
            wRooks ^= (0x01L | 0x04L);
            whites ^= ((0x08L | 0x02L) | (0x01L | 0x04L));
            castlingRights[WQ] = false;
            castlingRights[WK] = false;   
        } else if (special == 13) { //white en passant capture
            wPawns ^= fromTo;
            whites ^= fromTo;
            bPawns ^= to >>> 8;
            blacks ^= to >>> 8;
            epSquare = 0;
        } else if (special == 14) { //black en passant capture
            bPawns ^= fromTo;
            blacks ^= fromTo;
            wPawns ^= to << 8;
            whites ^= to << 8;
            epSquare = 0;
        }
        else {
            if ((to & wPawns) != 0) {
                whites ^= to;
                wPawns ^= to;
            }
            if ((to & wRooks) != 0) {
                whites ^= to;
                wRooks ^= to;
            }
            if ((to & wKnights) != 0) {
                whites ^= to;
                wKnights ^= to;
            }
            if ((to & wBishops) != 0) {
                whites ^= to;
                wBishops ^= to;
            }
            if ((to & wQueens) != 0) {
                whites ^= to;
                wQueens ^= to;
            }
            if ((to & wKing) != 0) {
                whites ^= to;
                wKing ^= to;
            }
            if ((to & bPawns) != 0) {
                blacks ^= to;
                bPawns ^= to;
            }
            if ((to & bRooks) != 0) {
                blacks ^= to;
                bRooks ^= to;
            }
            if ((to & bKnights) != 0) {
                blacks ^= to;
                bKnights ^= to;
            }
            if ((to & bBishops) != 0) {
                blacks ^= to;
                bBishops ^= to;
            }
            if ((to & bQueens) != 0) {
                blacks ^= to;
                bQueens ^= to;
            }
            if ((to & bKing) != 0) {
                blacks ^= to;
                bKing ^= to;
            }
            // moving
            if ((from & wPawns) != 0) {                
                wPawns ^= fromTo;
                whites ^= fromTo;
                if ((from & MoveGenerator.RANK_2) != 0 && 
                        (to & MoveGenerator.RANK_4) != 0) {
                    epSquare = (to >>> 8);
                }
            }
            if ((from & wRooks) != 0) {
                if ((from & MoveGenerator.FILE_A) != 0 ) {
                    castlingRights[WQ] = false;
                } else {
                    castlingRights[WK] = false;
                }
                wRooks ^= fromTo;
                whites ^= fromTo;                
            }
            if ((from & wKnights) != 0) {
                wKnights ^= fromTo;
                whites ^= fromTo;
            }
            if ((from & wBishops) != 0) {
                wBishops ^= fromTo;
                whites ^= fromTo;
            }
            if ((from & wQueens) != 0) {
                whites ^= fromTo;
                wQueens ^= fromTo;
            }
            if ((from & wKing) != 0) {
                whites ^= fromTo;
                wKing ^= fromTo;
                castlingRights[WK] = false;
                castlingRights[WQ] = false;                
            }
            if ((from & bPawns) != 0) {
                bPawns ^= fromTo;
                blacks ^= fromTo;                
                if ((from & MoveGenerator.RANK_7) != 0 && 
                        (to & MoveGenerator.RANK_5) != 0) {
                    epSquare = (to << 8);
                }
            }
            if ((from & bRooks) != 0) {
                if ((from & MoveGenerator.FILE_A) != 0 ) {
                    castlingRights[BQ] = false;
                } else {
                    castlingRights[BK] = false;
                }
                blacks ^= fromTo;
                bRooks ^= fromTo;
            }
            if ((from & bKnights) != 0) {
                blacks ^= fromTo;
                bKnights ^= fromTo;
            }
            if ((from & bBishops) != 0) {
                blacks ^= fromTo;
                bBishops ^= fromTo;
            }
            if ((from & bQueens) != 0) {
                blacks ^= fromTo;
                bQueens ^= fromTo;
            }
            if ((from & bKing) != 0) {
                blacks ^= fromTo;
                bKing ^= fromTo;
                castlingRights[BK] = false;
                castlingRights[BQ] = false;
            }
        }       
            occupied = whites | blacks;
            empty = ~occupied;
    }
    
    /**
     * This method handles user promotion.
     * @param move move to consider for promotion
     */
    public void userPromotion (Move move) {
        long from = move.from;
        long to = move.to;
        System.out.println("Pawn promotion, choose exchange piece: "
                + "q = Queen; b = Bishop; k = knight; r = Rook");
        char c = scan.next().charAt(0);
        switch (c) {
            case 'q':
                wPawns ^= from;
                whites ^= from;
                wQueens ^= to;
                whites ^= to;
                break;
            case 'b':
                wPawns ^= from;
                whites ^= from;
                wBishops ^= to;
                whites ^= to;
                break;
            case 'k':
                wPawns ^= from;
                whites ^= from;
                wKnights ^= to;
                whites ^= to;
                break;
            case 'r':
                wPawns ^= from;
                whites ^= from;
                wRooks ^= to;
                whites ^= to;
                break;
        }
        occupied = whites | blacks;
        empty = ~occupied;
    }
    
    /**
     * This method returns the heuristic value for a given board.
     * @return int heuristic value
     */
    public int heuristic ( ) {        
        int materialScore = KWEIGHT * (popCount(wKing) - popCount(bKing))
                + QWEIGHT * (popCount(wQueens) - popCount(bQueens))
                + RWEIGHT * (popCount(wRooks) - popCount(bRooks))
                + NWEIGHT * (popCount(wKnights) - popCount(bKnights))
                + BWEIGHT * (popCount(wBishops) - popCount(bBishops))
                + PWEIGHT * (popCount(wPawns) - popCount(bPawns));
        
        long attackedSquaresWhite = moveGen.attackedSquares(this, 1) & ~(whites);
        long attackedSquaresBlack = moveGen.attackedSquares(this, -1) & ~(blacks);        
        int mobilityScore = popCount(attackedSquaresWhite) - 
                            popCount(attackedSquaresBlack);
        
        int pieceSquareValues = (getPieceSquareValues(1) - getPieceSquareValues(-1));
        
        return materialScore + mobilityScore + pieceSquareValues;
    } 
    
    /**
     * This method checks whether a player is in stalemate.
     * @param sideToMove player to consider
     * @return boolean whether or not player is in stalemate
     */
    public boolean inStalemate(int sideToMove) {
        return !inCheck(sideToMove) && !hasEscapeMoves(sideToMove);
    }
    
    /**
     * This method checks whether a player is in checkmate.
     * @param sideToMove player to consider
     * @return boolean whether or not player is in checkmate
     */
    public boolean inCheckMate(int sideToMove) {
        return inCheck(sideToMove) && !hasEscapeMoves(sideToMove);
    }
    
    /**
     * This method checked whether a player's king has escape moves.
     * @param sideToMove player to consider
     * @return boolean whether player's king has escape moves
     */
    public boolean hasEscapeMoves(int sideToMove) {
        Board[] nextBoards = moveGen.nextMoves(this, sideToMove);
        for (Board nextBoard : nextBoards) {            
            if(nextBoard != null && !nextBoard.inCheck(sideToMove)) {
                return true;
            }
        }
        return false;
    }
        
    /**
     * This method returns the piece square values for a given player.
     * @param sideToMove player to consider
     * @return int piece square values
     */
    public int getPieceSquareValues(int sideToMove) {
        long pawns = (sideToMove == 1) ? wPawns : bPawns;
        long rooks = (sideToMove == 1) ? wRooks : bRooks;
        long knights = (sideToMove == 1) ? wKnights : bKnights;
        long bishops = (sideToMove == 1) ? wBishops : bBishops;
        long queens = (sideToMove == 1) ? wQueens : bQueens;
        long kings = (sideToMove == 1) ? wKing : bKing;
        
        int pawnScore = 0;
        int rookScore = 0;
        int knightScore = 0;
        int bishopScore = 0;
        int queenScore = 0;
        int kingScore = 0;
        
        for (long pos = pawns & -pawns; pawns != 0; pawns ^= pos, pos = pawns & 
            -pawns) {
            int i = getPosition(pos);
            pawnScore += (sideToMove == 1) ? PAWNTABLE[i] : PAWNTABLE[63-i];
        }
        for (long pos = knights & -knights; knights != 0; knights ^= pos, pos = 
            knights & -knights) {
            int i = getPosition(pos);
            knightScore += (sideToMove == 1) ? KNIGHTTABLE[i] : KNIGHTTABLE[63-i];
        }
        for (long pos = rooks & -rooks; rooks != 0; rooks ^= pos, pos = rooks & 
            -rooks) {
            int i = getPosition(pos);
            rookScore += (sideToMove == 1) ? ROOKTABLE[i] : ROOKTABLE[63-i];
        }
        for (long pos = bishops & -bishops; bishops != 0; bishops ^= pos, pos = 
            bishops & -bishops) {
            int i = getPosition(pos);
            bishopScore += (sideToMove == 1) ? BISHOPTABLE[i] : BISHOPTABLE[63-i];
        }
        for (long pos = queens & -queens; queens != 0; queens ^= pos, pos = 
            queens & -queens) {
            int i = getPosition(pos);
            queenScore += (sideToMove == 1) ? QUEENTABLE[i] : QUEENTABLE[63-i];
        }
        for (long pos = kings & -kings; kings != 0; kings ^= pos, pos = kings & 
            -kings) {
            int i = getPosition(pos);
            if (popCount(wQueens) == 0 && popCount(bQueens)== 0) {
                kingScore += (sideToMove == 1) ? LATEKINGTABLE[i] : LATEKINGTABLE[63-i];
            } else {
                kingScore += (sideToMove == 1) ? MIDKINGTABLE[i] : MIDKINGTABLE[63-i];
            }            
        }        
        int result = pawnScore + rookScore + knightScore + bishopScore 
                + queenScore + kingScore;        
        return result;
    } 
 
    /**
     * This method checks whether a player's king is in check.
     * @param sideToMove player to consider
     * @return boolean whether king is in check
     */
    public boolean inCheck(int sideToMove) {
        return (sideToMove == 1) ? 
            (wKing & moveGen.attackedSquares(this, -1 * sideToMove)) != 0 : 
            (bKing & moveGen.attackedSquares(this, -1 * sideToMove)) != 0;
    }  
    
    /**
     * This method returns the position of a piece on the board.
     * @param l board to consider
     * @return int location of piece on board
     */
    public int getPosition(long l) {
        int result = 0;
        while (l != 1) {
            l = l >>> 1;
            result++;
        }
        return result;
    }    
    
    /**
     * This method returns the value of the specified piece
     * @param piece piece to consider
     * @return Integer the value of the piece
     */
    public Integer getPieceValue(String piece) {
        String type = piece.substring(1,2);
        if ("P".equals(type)) {
            return PWEIGHT;
        } else if ("R".equals(type)) {
            return RWEIGHT;
        } else if ("N".equals(type)) {
            return NWEIGHT;
        } else if ("B".equals(type)) {
            return BWEIGHT;
        } else if ("Q".equals(type)) {
            return QWEIGHT;
        } else if ("K".equals(type)) {
            return KWEIGHT;
        }
        return null;
    } 
    
    /**
     * This method returns the piece at a given board position.
     * @param l board to consider
     * @return String piece to return
     */
    public String getPiece(long l) {
        String piece = null;
        if ((l & occupied) != 0) {
            if ((l & wPawns) != 0) {
                piece = "wP";
            } else if ((l & bPawns) != 0) {
                piece = "bP";
            } else if ((l & wRooks) != 0) {
                piece = "wR";
            } else if ((l & bRooks) != 0) {
                piece = "bR";
            } else if ((l & wKnights) != 0) {
                piece = "wN";
            } else if ((l & bKnights) != 0) {
                piece = "bN";
            } else if ((l & wBishops) != 0) {
                piece = "wB";
            } else if ((l & bBishops) != 0) {
                piece = "bB";
            } else if ((l & wQueens) != 0) {
                piece = "wQ";
            } else if ((l & bQueens) != 0) {
                piece = "bQ";
            } else if ((l & wKing) != 0) {
                piece = "wK";
            } else if ((l & bKing) != 0) {
                piece = "bK";
            }
        }
        return piece;
    }     
    
    /**
     * This method prints the board in ASCII format to console output.
     */
    public void printBoard() {        
        for (int i = 63; i >= 0; i--) {
            if ((i + 1) % 8 == 0) {
                System.out.println();
                System.out.printf("%s", i / 8 + 1);
            }
            long pos = 1L << i;
            if ((pos & wPawns) != 0) {
                System.out.printf(" %s", 'p');
            } else if ((pos & bPawns) != 0) {
                System.out.printf(" %s", 'P');
            } else if ((pos & wRooks) != 0) {
                System.out.printf(" %s", 'r');
            } else if ((pos & bRooks) != 0) {
                System.out.printf(" %s", 'R');
            } else if ((pos & wKnights) != 0) {
                System.out.printf(" %s", 'n');
            } else if ((pos & bKnights) != 0) {
                System.out.printf(" %s", 'N');
            } else if ((pos & wBishops) != 0) {
                System.out.printf(" %s", 'b');
            } else if ((pos & bBishops) != 0) {
                System.out.printf(" %s", 'B');
            } else if ((pos & wQueens) != 0) {
                System.out.printf(" %s", 'q');
            } else if ((pos & bQueens) != 0) {
                System.out.printf(" %s", 'Q');
            } else if ((pos & wKing) != 0) {
                System.out.printf(" %s", 'k');
            } else if ((pos & bKing) != 0) {
                System.out.printf(" %s", 'K');
            } else {
                System.out.printf(" %s", "_");
            }
        }
        System.out.println("\n  A B C D E F G H");        
    }    
    
    /**
     * This method returns the population of a given board.
     * @param l board to consider
     * @return int population of board
     */
    public int popCount (long l) {        
        int count = 0;
        for (long lsb = l & -l; l != 0; l^= lsb, lsb = l & -l) {
            count++;
        }
        return count;        
    }       

    /**
     * This method returns a hash for a board.
     * @param l board to hash
     * @return long board hash
     */
    public long hash (long[] l) {        
        return l[0] * wPawns ^ l[1] * wRooks ^ l[2] * wKnights ^ l[3] * wBishops
            ^ l[4] * wQueens ^ l[5] * wKing ^ l[6] * bPawns ^ l[7] * bKnights
            ^ l[8] * bBishops ^ l[9] * bRooks ^ l[10] * bQueens ^ l[11] * bKing;        
    }             
        
    /**
     * This method outputs a long in board format to console.
     * @param l long to print
     */
    public static void print (long l) {        
        String b = String.format("%64s",Long.toBinaryString(l)).replace(' ','0');
        for (int i = 0; i < 64; i++) {
            System.out.printf("%s ", b.charAt(i));
            if ((i + 1) % 8 == 0) {
                System.out.println();
            }
        }
        System.out.println();        
    }       
    
    /**
     * This method returns a copy of a board.
     * @param board
     * @return
     */
    public static final Board copy(Board board) {
        return new Board(board.bPawns, board.bKnights, board.bBishops, 
                board.bRooks, board.bQueens, board.bKing, board.wPawns, 
                board.wKnights, board.wBishops, board.wRooks, board.wQueens, 
                board.wKing, board.epSquare, board.castlingRights[0],
                board.castlingRights[1], board.castlingRights[2], 
                board.castlingRights[3]);
    } 
    
} 