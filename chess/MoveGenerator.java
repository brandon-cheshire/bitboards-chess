package chess;

/**
 * This class is responsible for generating and returning the set of all legal
 * moves for a given player. 
 * @author Brandon Cheshire, 5722350
 * @date December 14, 2017
 * @version 1.0
 */
public class MoveGenerator {    
    
    static final long FILE_A = 0x8080808080808080L;
    static final long FILE_B = FILE_A >>> 1;
    static final long FILE_C = FILE_B >>> 1;
    static final long FILE_D = FILE_C >>> 1;
    static final long FILE_E = FILE_D >>> 1;
    static final long FILE_F = FILE_E >>> 1;
    static final long FILE_G = FILE_F >>> 1;
    static final long FILE_H = FILE_G >>> 1;
    static final long RANK_1 = 0x00000000000000FFL;
    static final long RANK_2 = RANK_1 << 8;
    static final long RANK_3 = RANK_2 << 8;
    static final long RANK_4 = RANK_3 << 8;
    static final long RANK_5 = RANK_4 << 8;
    static final long RANK_6 = RANK_5 << 8;
    static final long RANK_7 = RANK_6 << 8;
    static final long RANK_8 = RANK_7 << 8;
    static final long NOT_FILE_A = ~FILE_A;
    static final long NOT_FILE_B = ~FILE_B;
    static final long NOT_FILE_C = ~FILE_C;
    static final long NOT_FILE_D = ~FILE_D;
    static final long NOT_FILE_E = ~FILE_E;
    static final long NOT_FILE_F = ~FILE_F;
    static final long NOT_FILE_G = ~FILE_G;
    static final long NOT_FILE_H = ~FILE_H;
    static final long NOT_RANK_1 = ~RANK_1;
    static final long NOT_RANK_2 = ~RANK_2;
    static final long NOT_RANK_3 = ~RANK_3;
    static final long NOT_RANK_4 = ~RANK_4;
    static final long NOT_RANK_5 = ~RANK_5;
    static final long NOT_RANK_6 = ~RANK_6;
    static final long NOT_RANK_7 = ~RANK_7;
    static final long NOT_RANK_8 = ~RANK_8;
    
    /**
     * This method returns all the legal next moves (represented as board
     * objects) for a given side.
     * @param board board to consider
     * @param sideToMove player to consider
     * @return board[] set of all legal moves for player
     */
    public Board[] nextMoves(Board board, int sideToMove) {
        //create array for next moves boards
        Board[] result = new Board[1000];
        //initalise array index
        int i = 0;
        //get correct player pieces
        long notFriendly = (sideToMove == 1) ? ~board.whites : ~board.blacks;
        long knights = (sideToMove == 1) ? board.wKnights : board.bKnights;
        long pawns = (sideToMove == 1) ? board.wPawns : board.bPawns;
        long rooks = (sideToMove == 1) ? board.wRooks : board.bRooks;
        long bishops = (sideToMove == 1) ? board.wBishops : board.bBishops;
        long queens = (sideToMove == 1) ? board.wQueens : board.bQueens;
        long king = (sideToMove == 1) ? board.wKing : board.bKing;
        long occupied = board.occupied;
        //pawn moves
        for (long pawnPos = LSB(pawns); pawns != 0; 
                pawns ^= pawnPos, pawnPos = LSB(pawns)) {
            Move[] pawnMoves = (sideToMove == 1)
                    ? whitePromotionMoves(pawnPos, board.blacks, occupied,
                            notFriendly) : blackPromotionMoves(pawnPos,
                            board.whites, occupied, notFriendly);
            for (Move move : pawnMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
            pawnMoves = (sideToMove == 1)
                    ? legalWhitePawnMoves(pawnPos, board.blacks, occupied,
                            notFriendly, board) : legalBlackPawnMoves(pawnPos,
                            board.whites, occupied, notFriendly, board);
            for (Move move : pawnMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
        }
        //queen moves
        for (long queenPos = LSB(queens); queens != 0; queens
                ^= queenPos, queenPos = LSB(queens)) {
            Move[] queenMoves = legalQueenMoves(queenPos,
                    occupied, notFriendly);
            for (Move move : queenMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
        }
        //king moves
        for (long kingPos = LSB(king); king != 0; king ^= kingPos, 
                kingPos = LSB(king)) {
            Move[] kingMoves = legalKingMoves(kingPos,notFriendly,board,sideToMove);
            for (Move move : kingMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
        }
        //bishop moves
        for (long bishopPos = LSB(bishops); bishops != 0; bishops
                ^= bishopPos, bishopPos = LSB(bishops)) {
            Move[] bishopMoves = legalBishopMoves(bishopPos,
                    occupied, notFriendly);
            for (Move move : bishopMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
        }
        //knight moves
        for (long knightPos = LSB(knights); knights != 0; knights
                ^= knightPos, knightPos = LSB(knights)) { 
            Move[] knightMoves = legalKnightMoves(knightPos,
                    notFriendly);
            for (Move move : knightMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
        }
        //rook moves
        for (long rookPos = LSB(rooks); rooks != 0; rooks ^= rookPos, 
                rookPos = LSB(rooks)) {           
            Move[] rookMoves = legalRookMoves(rookPos, occupied,
                    notFriendly);
            for (Move move : rookMoves) {
                if (move != null) {
                    Board copy = copy(board);
                    copy.makeMove(move);
                    result[i] = copy;
                    i++;
                }
            }
        }
        return result;
    } 
    
    /**
     * This method returns the positions under attack by opponent pieces.
     * @param board board to consider
     * @param sideToMove player to consider
     * @return long attacked squares
     */
    public long attackedSquares(Board board, int sideToMove) {
        //get correct players pieces
        long pawns = (sideToMove == 1) ? board.wPawns : board.bPawns;
        long rooks = (sideToMove == 1) ? board.wRooks : board.bRooks;
        long knights = (sideToMove == 1) ? board.wKnights : board.bKnights;
        long bishops = (sideToMove == 1) ? board.wBishops : board.bBishops;
        long queens = (sideToMove == 1) ? board.wQueens : board.bQueens;
        long kings = (sideToMove == 1) ? board.wKing : board.bKing;
        long occupied = board.occupied; 
        long enemies = (sideToMove == 1) ? board.blacks : board.whites;
        long attackedSquares = 0L; 
        //get attacked squares for each piece type
        for (long pos = knights & -knights; knights != 0; knights ^= pos, pos = 
                knights & -knights) {
            attackedSquares |= pseudoLegalKnightMoves(pos);
        }
        for (long pos = rooks & -rooks; rooks != 0; rooks ^= pos, pos = rooks & 
                -rooks) {
            attackedSquares |= pseudoLegalRookMoves(pos, occupied);
        }
        for (long pos = bishops & -bishops; bishops != 0; bishops ^= pos, pos = 
                bishops & -bishops) {
            attackedSquares |= pseudoLegalBishopMoves(pos, occupied);
        }
        for (long pos = queens & -queens; queens != 0; queens ^= pos, pos = 
                queens & -queens) {
            attackedSquares |= pseudoLegalQueenMoves(pos, occupied);
        }
        for (long pos = kings & -kings; kings != 0; kings ^= pos, pos = kings & 
                -kings) {
            attackedSquares |= pseudoLegalKingMoves(pos);
        }
        for (long pos = pawns & -pawns; pawns != 0; pawns ^= pos, pos = pawns & 
                -pawns) {
            attackedSquares |= ((sideToMove == 1) ? pseudoWhitePawnCaptures(pos) 
                    : pseudoBlackPawnCaptures(pos)) & enemies;
        }
        return attackedSquares;
    } 
      
    /**
     * This method returns moves for castling.
     * @param board board to consider
     * @param sideCondition which castling move
     * @return Move the castling move
     */
    public Move castlingMoves(Board board, int sideCondition) {
        if (!board.castlingRights[sideCondition]) {
            return null;
        }
        long empty;
        long attackSet;
        switch (sideCondition) {
            case 0: // black queenside               
                empty = 0x7000000000000000L;
                attackSet = attackedSquares(board,1);
                if (((attackSet & 0x1800000000000000L) != 0) 
                        || ((board.occupied & empty) 
                 != 0) || board.inCheck(-1) || (1L << 56 & board.bRooks) == 0) {
                    return null;
                }
                return new Move(0, 0, 9); 
            case 1: // black kingside
                empty = 0x0600000000000000L;
                attackSet = attackedSquares(board,1);
                if (((attackSet & 0x0C00000000000000L) != 0) 
                        || ((board.occupied & empty) 
                 != 0) || board.inCheck(-1) || (1L << 63 & board.bRooks) == 0) {
                    return null;
                }
                return new Move(0, 0, 10); 
            case 2: // white queenside
                empty = 0x0000000000000070L;
                attackSet = attackedSquares(board,-1);
                if (((attackSet & 0x18L) != 0) || ((board.occupied & empty) 
                   != 0) || board.inCheck(1) || (1L << 7 & board.wRooks) == 0) {
                    return null;
                }
                return new Move(board.wKing, board.wKing << 2, 11); 
            case 3: // white kingside
                empty = 0x0000000000000006L;
                attackSet = attackedSquares(board,-1);
                if (((attackSet & 0xCL) != 0) || ((board.occupied & empty) 
                        != 0) || board.inCheck(1) || (1L & board.wRooks) == 0) {
                    return null;
                }
                return new Move(board.wKing, board.wKing >>> 2, 12);           
        }
        return null; 
    }   
    
    /**
     * This method returns the legal king moves.
     * @param kingPos position of king on board
     * @param notFriendly opponent pieces on board
     * @param board board to consider
     * @param sideToMove player to consider
     * @return Move[] legal king moves
     */
    public Move[] legalKingMoves(long kingPos, long notFriendly, Board board, 
                                                            int sideToMove) {        
        Move[] moves = new Move[64];
        long attacks = pseudoLegalKingMoves(kingPos) & notFriendly;
        int i = 0;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(kingPos, to);
            i++;
        }
        moves[i] = (sideToMove == 1) ? castlingMoves(board, 2) : 
                castlingMoves(board, 0);
        i++;
        moves[i] = (sideToMove == 1) ? castlingMoves(board, 3) : 
                castlingMoves(board, 1);
        return moves;        
    } 
    
    /**
     * This method returns the legal white pawn moves.
     * @param pawnPos positions of pawns on board
     * @param blacks black pieces on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @param board board to consider
     * @return Move[] legal white pawn moves
     */
    public Move[] legalWhitePawnMoves(long pawnPos, long blacks, long 
            occupied, long notFriendly, Board board) {        
        Move[] moves = new Move[64];        
        long attacks = pseudoWhitePawnCaptures(pawnPos) 
                & (blacks | board.epSquare);
        int i = 0;        
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            if((to & board.epSquare) != 0) {
                moves[i] = new Move(pawnPos, board.epSquare, 13);
                i++;
            }
            else {
            moves[i] = new Move(pawnPos, to);
            i++;
            }
        }
        attacks = pseudoWhitePawnOnePush(pawnPos,occupied) & notFriendly;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(pawnPos, to);
            i++;
        }
        attacks = pseudoWhitePawnTwoPush(pawnPos,occupied) & notFriendly;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(pawnPos, to);
            i++;
        }        
        return moves;        
    } 
        
    /**
     * This method returns the legal black pawn moves.
     * @param pawnPos position of pawns on board
     * @param whites white pieces on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @param board board to consider
     * @return Move[] legal black pawn moves
     */
    public Move[] legalBlackPawnMoves(long pawnPos, long whites, long 
            occupied, long notFriendly, Board board) {        
        Move[] moves = new Move[64];
        long attacks = pseudoBlackPawnCaptures(pawnPos) & 
                (whites | board.epSquare);
        int i = 0;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            if((to & board.epSquare) != 0) {
                moves[i] = new Move(pawnPos, board.epSquare, 14);
                i++;
            }
            else {
            moves[i] = new Move(pawnPos, to);
            i++;
            }
        }
        attacks = pseudoBlackPawnOnePush(pawnPos,occupied) & notFriendly;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(pawnPos, to);
            i++;
        }
        attacks = pseudoBlackPawnTwoPush(pawnPos,occupied) & notFriendly;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(pawnPos, to);
            i++;
        }
        return moves;        
    } 
        
    /**
     * This method returns the promotion moves for the white player.
     * @param pawnPos position of pawns
     * @param blacks black pieces on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @return Move[] legal white promotion moves
     */
    public Move[] whitePromotionMoves(long pawnPos, long blacks, long 
                                                occupied, long notFriendly) {        
        Move[] moves = new Move[64];
        int i = 0;
        long attacks = pseudoWhitePawnOnePush(pawnPos,occupied)
                & notFriendly & RANK_8;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = LSB(attacks)) {
            moves[i] = new Move(pawnPos, to, 1);
            i++;
            moves[i] = new Move(pawnPos, to, 2);
            i++;
            moves[i] = new Move(pawnPos, to, 3);
            i++;
            moves[i] = new Move(pawnPos, to, 4);
            i++;
        }
        return moves;        
    }
    
    /**
     * This method returns the black promotion moves.
     * @param pawnPos position of pawns
     * @param whites white pieces on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @return Move[] legal black promotion moves
     */
    public Move[] blackPromotionMoves(long pawnPos, long whites, long 
                                                occupied, long notFriendly) {        
        Move[] moves = new Move[64];
        int i = 0;
        long attacks = pseudoBlackPawnOnePush(pawnPos,occupied) 
                & notFriendly & RANK_1;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = LSB(attacks)) {
            moves[i] = new Move(pawnPos, to, 5);
            i++;
            moves[i] = new Move(pawnPos, to, 6);
            i++;
            moves[i] = new Move(pawnPos, to, 7);
            i++;
            moves[i] = new Move(pawnPos, to, 8);
            i++;
        }
        return moves;        
    } 
    
    /**
     * This method returns legal queen moves.
     * @param queenPos position of queen on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @return Move[] legal queen moves
     */
    public Move[] legalQueenMoves (long queenPos, long occupied, long 
            notFriendly) {        
        Move[] moves = new Move[64];        
        long attacks = pseudoLegalQueenMoves(queenPos,occupied) & notFriendly;
        int i = 0;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(queenPos, to);
            i++;
        }
        return moves;        
    } 
        
    /**
     * This method returns the legal bishop moves.
     * @param bishopPos position of bishop on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @return Move[] legal bishop moves
     */
    public Move[] legalBishopMoves (long bishopPos, long occupied, long 
            notFriendly) {        
        Move[] moves = new Move[64];
        long attacks = pseudoLegalBishopMoves(bishopPos,occupied) & notFriendly;
        int i = 0;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(bishopPos, to);
            i++;
        }
        return moves;        
    } 
    
    /**
     * This method returns the legal rook moves.
     * @param rookPos position of rook on board
     * @param occupied occupied tiles on board
     * @param notFriendly opponent pieces on board
     * @return Move[] legal rook moves
     */
    public Move[] legalRookMoves (long rookPos, long occupied, long 
            notFriendly) {        
        Move[] moves = new Move[64];        
        long attacks = pseudoLegalRookMoves(rookPos,occupied) & notFriendly;
        int i = 0;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(rookPos, to);
            i++;
        }
        return moves;        
    }
        
    /**
     * This method returns the legal knight moves.
     * @param knightPos position of knight on board
     * @param notFriendly opponent pieces on board
     * @return Move[] legal move set for knight
     */
    public Move[] legalKnightMoves(long knightPos, long notFriendly) {
        Move[] moves = new Move[64];
        long attacks = pseudoLegalKnightMoves(knightPos) & notFriendly;
        int i = 0;
        for (long to = LSB(attacks); attacks != 0; attacks ^= to, to = 
                LSB(attacks)) {
            moves[i] = new Move(knightPos, to);
            i++;
        }
        return moves;
    } 
    
    /**
     *This method returns the least significant bit from a board.
     * @param l board for LSB
     * @return long LSB board
     */
    public long LSB (long l) {        
        return l&-l;        
    }    
       
    /**
     * This method returns the pseudo-legal queen moves.
     * @param queenPos position of queen on board
     * @param occupied occupied tiles on board
     * @return long pseudo-legal queen moves
     */ 
    public long pseudoLegalQueenMoves(long queenPos, long occupied) {        
	return pseudoLegalRookMoves(queenPos, occupied) | 
                pseudoLegalBishopMoves(queenPos, occupied);        
    } 
     
    /**
     * This method returns the move set for the knight at knightPos.
     * @param knightPos position of knight on board
     * @return long the move set of the knight
     */
    public long pseudoLegalKnightMoves(long knightPos) {        
        long WN = (knightPos & NOT_FILE_A & NOT_FILE_B) << 10;
        long NW = (knightPos & NOT_FILE_A) << 17;
        long WS = (knightPos & NOT_FILE_A & NOT_FILE_B) >>> 6;
        long SW = (knightPos & NOT_FILE_A) >>> 15;
        long EN = (knightPos & NOT_FILE_G & NOT_FILE_H) >>> 10;
        long NE = (knightPos & NOT_FILE_H) >>> 17;
        long ES = (knightPos & NOT_FILE_G & NOT_FILE_H) << 6;
        long SE = (knightPos & NOT_FILE_H) << 15;
        return (WN | NW | WS | SW | EN | NE | ES | SE);        
    } 
    
    /**
     * This method returns the legal moves for a rook at a specified position.
     * @param rookPos position of rook on board
     * @param occupied occupied tiles on board
     * @return long pseudo-legal moves for rook
     */
    public long pseudoLegalRookMoves(long rookPos, long occupied) {         
        long notRookPos = ~rookPos;
        long W = rookPos;       
        while (((W & notRookPos & occupied) == 0) & ((W & FILE_A) == 0)) {
            W |= W << 1;            
        }        
        long E = rookPos;
        while (((E & notRookPos & occupied) == 0) & ((E & FILE_H) == 0)) {
            E |= E >>> 1;            
        }        
        long N = rookPos;
        while (((N & notRookPos & occupied) == 0) & ((N & RANK_8) == 0)) {
            N |= N << 8;
        }
        long S = rookPos;
        while (((S & notRookPos & occupied) == 0) & ((S & RANK_1) == 0)) {
            S |= S >>> 8;
        }
        return (N | E | S | W) & notRookPos;         
    }    
    
    /**
     * This method returns pseudo-legal moves for a king at specified position.
     * @param kingPos position of king on board
     * @return long pseudo-legal moves for king
     */
    public long pseudoLegalKingMoves(long kingPos) {        
        long N = (kingPos << 8);
        long NE = (kingPos & NOT_FILE_H) << 7;
        long E = (kingPos & NOT_FILE_H) >>> 1;
        long SE = (kingPos & NOT_FILE_H) >>> 9;
        long S = (kingPos >>> 8);
        long SW = (kingPos & NOT_FILE_A) >>> 7;
        long W = (kingPos & NOT_FILE_A) << 1;
        long NW = (kingPos & NOT_FILE_A) << 9;
        return (N | NE | E | SE | S | SW | W | NW);        
    } 

    /**
     * This method returns pseudo-legal moves for bishop at specified position.
     * @param bishopPos position of bishop on board
     * @param occupied occupied tiles on board
     * @return long pseudo-legal moves for 
     */
    public long pseudoLegalBishopMoves(long bishopPos, long occupied) {        
        long notBishopPos = ~bishopPos;
        long NW = bishopPos;        
        while ((NW & notBishopPos & occupied) == 0 & 
                ((NW & FILE_A) == 0) & ((NW & RANK_8) == 0)) {
            NW |= NW << 9;
        }
        long NE = bishopPos;
        while ((NE & notBishopPos & occupied) == 0 & 
                ((NE & FILE_H) == 0) & ((NE & RANK_8) == 0)) {
            NE |= NE << 7;
        }
        long SW = bishopPos;
        while ((SW & notBishopPos & occupied) == 0 & 
                ((SW & FILE_A) == 0) & ((SW & RANK_1) == 0)) {
            SW |= SW >>> 7;
        }
        long SE = bishopPos;
        while ((SE & notBishopPos & occupied) == 0 & 
                ((SE & FILE_H) == 0) & ((SE & RANK_1) == 0)) {
            SE |= SE >>> 9;
        }
        return (NW | NE | SE | SW) & notBishopPos;        
    } 
    
    /**
     * This method returns pseudo-legal moves for a white pawn one push.
     * @param pawnPos position of pawn on board
     * @param occupied occupied tiles on board
     * @return long pseudo-legal moves for 
     */
    public long pseudoWhitePawnOnePush(long pawnPos, long occupied) {        
        long open = ~occupied;
        long oneStep = (pawnPos << 8) & open;
        return oneStep;        
    } 
    
    /**
     * This method returns pseudo-legal moves for a white pawn two push.
     * @param pawnPos position of pawn on board
     * @param occupied occupied squares on board
     * @return long pseudo-legal moves for 
     */
    public long pseudoWhitePawnTwoPush(long pawnPos, long occupied) {        
        long open = ~occupied;
        long oneStep = pseudoWhitePawnOnePush(pawnPos, occupied);
        long twoStep = ((RANK_3 & oneStep) << 8) & open;
        return twoStep;        
    } 
    
    /**
     * This method generates the pseudo-legal moves for white pawn captures.
     * @param pawnPos current pawn position
     * @return long pseudo-legal white pawn captures.
     */
    public long pseudoWhitePawnCaptures(long pawnPos) {        
        long NE = ((pawnPos & NOT_FILE_H) << 7);
        long NW = ((pawnPos & NOT_FILE_A) << 9);
        return (NE | NW);        
    } 
    
    /**
     * This method generates the pseudo-legal moves for black pawn one pushes.
     * @param pawnPos current pawn position
     * @param occupied occupied tiles on board
     * @return long pseudo-legal moves for 
     */
    public long pseudoBlackPawnOnePush(long pawnPos, long occupied) {        
        long open = ~occupied;
        long oneStep = (pawnPos >>> 8) & open;
        return oneStep;        
    }  
    
    /**
     * This method generates the pseudo-legal moves for black pawn two pushes.
     * @param pawnPos current pawn position
     * @param occupied occupied tiles on board
     * @return long pseudo-legal moves for 
     */
    public long pseudoBlackPawnTwoPush(long pawnPos, long occupied) {         
        long open = ~occupied;
        long oneStep = pseudoBlackPawnOnePush(pawnPos, occupied);
        long twoStep = ((RANK_6 & oneStep) >>> 8) & open;
        return twoStep;        
    } 
    
    /**
     * This method generates the pseudo-legal moves for black pawn captures.
     * @param pawnPos current pawn position
     * @return long pseudo-legal moves for 
     */
    public long pseudoBlackPawnCaptures(long pawnPos) {        
        long SE = ((pawnPos & NOT_FILE_H) >>> 9);
        long SW = ((pawnPos & NOT_FILE_A) >> 7);
        return (SE | SW);        
    } 
    
    /**
     * This method returns a copy of a board.
     * @param board board to copy
     * @return Board copy of board
     */
    public static final Board copy(Board board) {
        return new Board(board.bPawns, board.bKnights, board.bBishops, 
                board.bRooks,board.bQueens, board.bKing, board.wPawns,
                board.wKnights, board.wBishops, board.wRooks, board.wQueens, 
                board.wKing, board.epSquare, board.castlingRights[0],
                board.castlingRights[1], board.castlingRights[2], 
                board.castlingRights[3]);
    } 
    
} 