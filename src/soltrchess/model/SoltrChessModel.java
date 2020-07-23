package soltrchess.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * The model for the Solitaire Chess game.
 *
 * @author Jesse Burdick-Pless jb4411@g.rit.edu
 */
public class SoltrChessModel implements Observer{
    /** the number of rows */
    public final static int ROWS = 4;
    /** the number of columns */
    public final static int COLS = 4;

    /** used to determine the pieces on the board */
    public enum Piece {
        BISHOP,
        KING,
        KNIGHT,
        PAWN,
        QUEEN,
        ROOK,
        NONE
    }

    /** the game status */
    public enum Status {
        NOT_OVER,
        SOLVED,
        FAILED
    }

    /** the status of the game */
    private Status status;
    /** the observers of this model */
    private List<Observer<SoltrChessModel, ClientData>> observers;
    /** the game board */
    private Piece[][] board;
    /** the column of the piece that was selected */
    private int selectedCol;
    /** the row of the piece that was selected */
    private int selectedRow;
    /** the column of the place to move to */
    private int moveCol;
    /** the row of the place to move to */
    private int moveRow;
    /** */
    private int numPieces;

    /**
     * Create a new board.
     */
    public SoltrChessModel(String filename) throws FileNotFoundException {
        this.status = Status.NOT_OVER;
        this.board = new Piece[ROWS][COLS];
        this.numPieces = 0;

        Scanner f = new Scanner(new File(filename));

        // initialize the board
        for (int row=0; row<ROWS; ++row) {
            for (int col=0; col<COLS; ++col) {
                String next = f.next();
                Piece current;
                this.numPieces++;
                switch (next) {
                    case "B" -> {
                        current = Piece.BISHOP;
                    }
                    case "K" -> {
                        current = Piece.KING;
                    }
                    case "N" -> {
                        current = Piece.KNIGHT;
                    }
                    case "P" -> {
                        current = Piece.PAWN;
                    }
                    case "Q" -> {
                        current = Piece.QUEEN;
                    }
                    case "R" -> {
                        current = Piece.ROOK;
                    }
                    default -> {
                        current = Piece.NONE;
                        this.numPieces--;
                    }
                }
                this.board[row][col] = current;
            }
        }
        this.observers = new LinkedList<>();
    }

    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer<SoltrChessModel, ClientData> observer) {
        this.observers.add(observer);
    }

    /** When the model changes, the observers are notified via their update() method */
    private void notifyObservers() {
        for (Observer<SoltrChessModel, ClientData> obs: this.observers ) {
            obs.update(this, null);
        }
    }

    /**
     * Is this a valid move?
     *
     * @param selectedCol the column of the selected piece to move
     * @param selectedRow the row of the selected piece to move
     * @param moveCol the column of the space to move to
     * @param moveRow the row of the space to move to
     * @return whether or not the move is valid
     */
    public boolean isValidMove(int selectedCol, int selectedRow, int moveCol, int moveRow) {
       if (this.board[moveCol][moveRow].equals(Piece.NONE)) {
            return false;
        } else if (selectedCol == moveCol && selectedRow == moveRow) {
            return false;
        } else if (selectedCol >= COLS || moveCol >= COLS || selectedCol <= 0 || moveCol <= 0) {
           return false;
       } else if (selectedRow >= ROWS || moveRow >= ROWS || selectedRow <= 0 || moveRow <= 0) {
           return false;
       }
        Piece next = this.board[selectedRow][selectedCol];
        switch (next) {
            case BISHOP -> {
                if (selectedCol == moveCol || selectedRow == moveRow) {
                    return false;
                } else {
                    return Math.abs(moveRow - selectedRow) == Math.abs(moveCol - selectedCol);
                }
            }
            case KING -> {
                return Math.abs(selectedCol - moveCol) <= 1 && Math.abs(selectedRow - moveRow) <= 1;
            }
            case KNIGHT -> {
                if (selectedCol == moveCol || selectedRow == moveRow) {
                    return false;
                } else {
                    if (Math.abs(moveRow - selectedRow) == 1) {
                        if (Math.abs(moveCol - selectedCol) == 2) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (Math.abs(moveCol - selectedCol) == 1) {
                        if (Math.abs(moveRow - selectedRow) == 2) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
            case PAWN -> {
                if (selectedCol == moveCol || selectedRow == moveRow) {
                    return false;
                } else {
                    return moveRow - selectedRow == 1 && Math.abs(moveCol - selectedCol) == 1;
                }
            }
            case QUEEN -> {
                if (selectedCol == moveCol || selectedRow == moveRow) {
                    if (moveCol != selectedCol) {
                        return moveRow == selectedRow;
                    } else {
                        return true;
                    }
                } else {
                    return Math.abs(moveRow - selectedRow) == Math.abs(moveCol - selectedCol);
                }
            }
            case ROOK -> {
                if (moveCol != selectedCol) {
                    return moveRow == selectedRow;
                } else {
                    return true;
                }
            }
            default -> {
                return false;
            }
        }
        return false;
    }

    /**
     * Make a move by placing a disc in a valid column.
     *
     * @rit.pre the move must be valid
     * @param selectedCol the column of the selected piece to move
     * @param selectedRow the row of the selected piece to move
     * @param moveCol the column of the space to move to
     * @param moveRow the row of the space to move to
     */
    public void makeMove(int selectedCol, int selectedRow, int moveCol, int moveRow) {
        this.board[moveRow][moveCol] = this.board[selectedRow][selectedCol];
        this.board[selectedRow][selectedCol] = Piece.NONE;
        this.numPieces--;

        if (this.numPieces == 1) {
            this.status = Status.SOLVED;
        }
    }

    @Override
    public void update(Object o, Object o2) {

    }
}