package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * This class simulates a chessboard with pieces for Graphical User Interface.
 * @author Brandon Cheshire, 5722350 (Java Swing elements made with use of
 * tutorial by Amir Afghani)
 * @date December 14, 2017
 * @version 1.0
 */
public class GUI {        
    
    private final JFrame gameFrame;
    public final BoardPanel boardPanel;
    private Board chessBoard;
    private final Engine engine;
    private final MoveGenerator moveGen;
    
    private Long sourceTile;
    private Long destinationTile;
    private String humanMovedPiece;
    private BoardDirection boardDirection;
    private Board lastBoard;
    
    private boolean highlightLegalMoves;
    private int sideToMove;
    private final int gameSetup;
    private final int searchDepth;
    private final Scanner scan;
    
    private final Color lightTileColour = Color.decode("#FFFACD");
    private final Color darkTileColour = Color.decode("#593E1A");
    
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TITLE_PANEL_DIMENSION = new Dimension(10,10);
    private static final String DEFAULT_PIECES_IMAGES_PATH = "images/pieces/plain/";
    
    private static final GUI INSTANCE = new GUI();
    
    public GUI () { 
        scan = new Scanner(System.in);
        System.out.println("Select game setup: (1=Human vs. Computer; 2=Computer vs Computer)");
        gameSetup = scan.nextInt();              
        gameFrame = new JFrame("Chess");
        gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setSize(OUTER_FRAME_DIMENSION); 
        chessBoard = new Board();
        engine = new Engine();
        moveGen = new MoveGenerator();    
        boardPanel = new BoardPanel();
        boardDirection = BoardDirection.NORMAL;
        highlightLegalMoves = true;
        gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true); 
        sideToMove = 1;  
        System.out.println("Select search depth: (4 = fast; 5 = normal (recommended, esp. for comp vs. comp); 6 = slow)");
        searchDepth = scan.nextInt();   
        if(gameSetup == 2) {
            computerMatch(sideToMove);                        
        }
    }
    
    /**
     * This method plays the computer's turn.
     */
    private void computerPlay() {        
        boardPanel.drawBoard(chessBoard);       
        chessBoard = engine.miniMax(chessBoard, searchDepth, -1);
        boardPanel.drawBoard(chessBoard);
        if (chessBoard.inCheckMate(1)) {            
            System.out.println("Checkmate for white.");
        }
        else if (chessBoard.inStalemate(1)) {
            System.out.println("Stalemate for white.");
        }
        else if (chessBoard.inCheck(1)) {
            System.out.println("King is in check.");
            sideToMove = 1;
        }
        else {
            sideToMove = 1;
        }
    }
    
    /**
     * This method matches two computer players against each other.
     */
    private void computerMatch(int sideToMove) {
        for (int i = 0; i < 3000; i++) {
            try {
                if (chessBoard != null) {
                    chessBoard = engine.miniMax(chessBoard, searchDepth, sideToMove);
                    engine.boardHashes[i] = chessBoard.hash(engine.hashTable);
                    sideToMove *= -1;
                    if (chessBoard != null) {
                        lastBoard = Board.copy(chessBoard);
                        boardPanel.drawBoard(chessBoard);
                        //for debugging
//                        System.out.println("Ply: " + (i + 1) + ", Heuristic: " + chessBoard.
//                                heuristic() + ". Hash: " + chessBoard.hash(engine.hashTable));
                    }
                }
            } catch (NullPointerException e) {  
                if (lastBoard.inCheckMate(1)) {
                    System.out.println("Checkmate for white");
                    break;
                } else if (lastBoard.inCheckMate(-1)) {
                    System.out.println("Checkmate for black"); 
                    break;
                } else if (lastBoard.inStalemate(1)) {
                    System.out.println("Stalemate for white");
                    break;
                } else if (lastBoard.inStalemate(-1)) {
                    System.out.println("Stalemate for black");
                    break;
                } else if (engine.isDraw(engine.boardHashes[i - 1])) {
                    System.out.println("Draw for " + ((sideToMove == 1) ? "white" : "black"));
                    break;
                }
            }
        }
        chessBoard = Board.copy(lastBoard);
        this.boardPanel.drawBoard(chessBoard);
    }

    /**
     * This method returns an instance of this class.
     * @return GUI instance of this GUI.
     */    
    public static GUI get() {
        return INSTANCE;
    }
    
    /**
     * This method displays the current instance.
     */
    public void show() {
        GUI.get().getBoardPanel().drawBoard(GUI.get().getGameBoard());
    }    
    
    private Board getGameBoard() {
        return this.chessBoard;
    }
    
    private JMenuBar createTableMenuBar ( ) { 
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu()); 
        tableMenuBar.add(createPreferencesMenu());        
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }    
    
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");        
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    } 
    
    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = 
                new JCheckBoxMenuItem("HighLight Legal Moves", true);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckbox);
        return preferencesMenu;
    } 
    
        private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");        
        final JMenuItem undoLastMoveMenuItem = new JMenuItem("Undo Last Move");
        undoLastMoveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameSetup == 1) {
                    chessBoard = Board.copy(lastBoard);
                    boardPanel.drawBoard(chessBoard);
                }
            }
        });
        optionsMenu.add(undoLastMoveMenuItem);
        final JMenuItem resetBoardMenuItem = new JMenuItem("Reset Board");
        resetBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameSetup == 1) {
                    chessBoard = new Board();
                    boardPanel.drawBoard(chessBoard);
                }
            }
        });
        optionsMenu.add(resetBoardMenuItem);
        return optionsMenu;
    }
    
    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }
    
    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }    
    
    /**
     * This class builds the board panel for the chessboard tile panels.
     */
    public class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;
        
        BoardPanel ( ) {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for (int i = 63; i >= 0; i--) {
                final TilePanel tilePanel = new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();            
        } // constructor        
        
        public void drawBoard (final Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();            
        }       
    }
    
    /**
     * This class represents and draws the tiles on the chess board.
     */
    public class TilePanel extends JPanel {
        private final int tileId;
        
        TilePanel (final BoardPanel boardPanel,
                final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TITLE_PANEL_DIMENSION);
            assignTileColour();
            assignTilePieceIcon(chessBoard);
            if (gameSetup == 1) {
            addMouseListener(new MouseListener() {

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (sideToMove == 1) {
            highlightLegalMoves = true;
                if (SwingUtilities.isRightMouseButton(e)) {
                    sourceTile = null;
                    destinationTile = null;
                    humanMovedPiece = null;
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (sourceTile == null) {
                        sourceTile = 1L << tileId;
                        humanMovedPiece = chessBoard.getPiece(sourceTile);
                        if (humanMovedPiece == null) {
                            sourceTile = null;
                        }
                    } else {
                        destinationTile = 1L << tileId;
                        final Move move = new Move(sourceTile, destinationTile);
                        if (move.from == move.to) {
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        } else {
                            for (final Move legal : pieceLegalMoves(chessBoard)) {
                                if (legal != null) {
                                    if (move.from == legal.from && move.to == legal.to) {
                                        if (legal.special != 0) {
                                            move.special = legal.special;
                                        }
                                        Board temp = Board.copy(chessBoard);                                                
                                        temp.makeMove(move);
                                        if (!temp.inCheck(1)) {
                                            if((move.from & chessBoard.wPawns) 
                                                != 0 && (move.to & MoveGenerator.
                                                    RANK_8) != 0) {                                                        
                                                chessBoard.userPromotion(move);                                                       
                                                boardPanel.drawBoard(chessBoard);
                                                sideToMove = -1;
                                            }else if(chessBoard.inCheckMate(-1)) {
                                                System.out.println("Checkmate for black.");
                                            }else if(chessBoard.inStalemate(-1)) {
                                                System.out.println("Stalemate for black.");
                                            }else {
                                                lastBoard = Board.copy(chessBoard);
                                                chessBoard.makeMove(move);
                                                highlightLegalMoves = false;
                                                boardPanel.drawBoard(chessBoard);
                                                sideToMove = -1;
                                            }
                                        } else if (temp.inCheckMate(1) && 
                                                sideToMove == 1) {
                                            System.out.println("Illegal Move: "
                                                    + " king in checkmate.");
                                        } else {
                                            System.out.println("Illegal Move: "+
                                            ((chessBoard.inCheck(1)) ? "keeps" :
                                                    "puts") +" king in check.");
                                        }
                                    }
                                }
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    boardPanel.drawBoard(chessBoard);
                    if (sideToMove == -1) {
                        computerPlay();
                    }
                    }
                    });
                    }
                }
                }
                @Override
                public void mousePressed(MouseEvent e) {
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                }
                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            }
            validate();
        }         

        private void drawTile(final Board board) {
            assignTileColour();
            assignTilePieceIcon(board);            
            highlightLegals(board);
            validate();
            repaint();
        }        
        
        /**
         * This method assigns the piece icons to their respective tile.
         */
        private void assignTilePieceIcon(final Board board) {
            removeAll();
            Long l = 1L << tileId;
            if (board != null) {
                String piece = board.getPiece(l);
                if (piece != null) {
                    try {
                        final BufferedImage image = ImageIO.read(this.getClass().getResource(DEFAULT_PIECES_IMAGES_PATH + piece + ".gif"));
                        add(new JLabel(new ImageIcon(image)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        /**
         * This method draws the legal moves for a piece on the board.
         */
        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) {
                for(final Move move : pieceLegalMoves(board)) {
                    long l = 1L << this.tileId;                    
                    if (move == null) {
                    } else {
                        if ((move.to & l) != 0) {
                            try {
                                add(new JLabel(new ImageIcon(ImageIO.read
                  (this.getClass().getResource("images/misc/green_dot.png")))));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } 
        
        /**
         * This method returns the legal moves for a piece on the board.
         */
        private Move[] pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && "w".equals(humanMovedPiece.substring(0,1))) {
                String type = humanMovedPiece.substring(1,2);
                if (null != type) switch (type) {
                    case "P":
                        return moveGen.legalWhitePawnMoves(sourceTile,
                                chessBoard.blacks,chessBoard.occupied,~chessBoard.whites,chessBoard);
                    case "R":
                        return moveGen.legalRookMoves(sourceTile,chessBoard.occupied,
                                ~chessBoard.whites);
                    case "N":
                        return moveGen.legalKnightMoves(sourceTile,~chessBoard.whites);
                    case "B":
                        return moveGen.legalBishopMoves(sourceTile,chessBoard.occupied,
                                ~chessBoard.whites);
                    case "Q":
                        return moveGen.legalQueenMoves(sourceTile,chessBoard.occupied,
                                ~chessBoard.whites);
                    case "K":
                        return moveGen.legalKingMoves(sourceTile,~chessBoard.whites,                
                                chessBoard,1);
                    default:
                        break;
                }                  
            } 
            //following was for implementing human vs. human game setup and debugging.
//            else if (humanMovedPiece != null && "b".equals(humanMovedPiece.substring(0,1))){
//                String type = humanMovedPiece.substring(1,2);
//                if (null != type) switch (type) {
//                    case "P":
//                        return moveGen.legalBlackPawnMoves(sourceTile,chessBoard.whites,
//                                chessBoard.occupied,~chessBoard.blacks,chessBoard);
//                    case "R":
//                        return moveGen.legalRookMoves(sourceTile,chessBoard.occupied,
//                                ~chessBoard.blacks);
//                    case "N":
//                        return moveGen.legalKnightMoves(sourceTile,~chessBoard.blacks);
//                    case "B":
//                        return moveGen.legalBishopMoves(sourceTile,chessBoard.occupied,
//                                ~chessBoard.blacks);
//                    case "Q":
//                        return moveGen.legalQueenMoves(sourceTile,chessBoard.occupied,
//                                ~chessBoard.blacks);
//                    case "K":
//                        return moveGen.legalKingMoves(sourceTile,~chessBoard.blacks,                
//                                chessBoard,-1);
//                    default:
//                        break;
//                }       
//            }            
            return new Move[]{};                    
        } 
    
        /**
         * This method assigns the tile colours to the tile panels.
         */
        private void assignTileColour() {
            if (this.tileId >= 0 && this.tileId <=7 || 
                    this.tileId >= 16 && this.tileId <= 23 || 
                    this.tileId >= 32 && this.tileId <= 39 || 
                    this.tileId >= 48 && this.tileId <= 55){
                setBackground(this.tileId % 2 == 0 ? lightTileColour : 
                        darkTileColour);
            } else if (this.tileId >= 8 && this.tileId <=15 || 
                    this.tileId >= 24 && this.tileId <= 31 || 
                    this.tileId >= 40 && this.tileId <= 47 || 
                    this.tileId >= 56 && this.tileId <= 63){
                setBackground(this.tileId % 2 != 0 ? lightTileColour : 
                        darkTileColour);
            }
        }        
    } 
    
    /**
     * This enum defines direction of traversal of Tile panels for displaying
     * the board.
     */
    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                List<TilePanel> temp = new ArrayList<>(boardTiles);
                Collections.reverse(temp);
                return temp;
            }
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();        
    }
}
