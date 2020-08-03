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
public class SoltrChessModel {
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
        INVALID_FILE,
        FAILED
    }

    /** the status of the game */
    private Status status;
    /** the observers of this model */
    private List<Observer<SoltrChessModel, Status>> observers;
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
    /** the current number of pieces on the board */
    private int numPieces;

    /**
     * Create a new board.
     */
    public SoltrChessModel(String filename) throws FileNotFoundException {
        this.status = Status.NOT_OVER;
        this.board = new Piece[ROWS][COLS];
        this.numPieces = 0;

        Scanner f = new Scanner(new File(filename));
        String next = "";
        // initialize the board
        for (int row=0; row<ROWS; ++row) {
            for (int col=0; col<COLS; ++col) {
                try {
                    next = f.next();
                } catch (Exception e) {
                    this.status = Status.INVALID_FILE;
                    next = "bad file";
                }

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
                    case "-" -> {
                        current = Piece.NONE;
                        this.numPieces--;
                    }
                    default -> {
                        this.status = Status.INVALID_FILE;
                        current = Piece.NONE;
                    }
                }
                this.board[row][col] = current;
            }
        }
        this.observers = new LinkedList<>();
        if (this.numPieces == 1 && this.status != Status.INVALID_FILE) {
            this.status = Status.SOLVED;
        }
        f.close();
    }

    /**
     * Copy constructor
     *
     * @param copy SoltrChessModel instance
     */
    public SoltrChessModel(SoltrChessModel copy) {
        this.status = copy.status;
        this.observers = new LinkedList<>();
        this.board = new Piece[ROWS][COLS];
        for (int r=0; r<ROWS; r++) {
            System.arraycopy(copy.board[r], 0, this.board[r], 0, COLS);
        }
        this.selectedCol = copy.selectedCol;
        this.selectedRow = copy.selectedRow;
        this.moveCol = copy.moveCol;
        this.moveRow = copy.moveRow;
        this.numPieces = copy.numPieces;
    }

    /**
     * The view calls this method to add itself as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer<SoltrChessModel, Status> observer) {
        this.observers.add(observer);
    }

    /** When the model changes, the observers are notified via their update() method */
    private void notifyObservers() {
        for (Observer<SoltrChessModel, Status> obs: this.observers ) {
            obs.update(this, status);
        }
    }

    /**
     * Get the status of the game.
     *
     * @return game status
     */
    public Status getGameStatus() {
        return this.status;
    }

    /**
     * Get the piece board.
     *
     * @return the piece board
     */
    public Piece[][] getPieceBoard() {
        return this.board;
    }

    /**
     * Get the piece at the selected row and column.
     *
     * @param row the selected row
     * @param col the selected column
     * @return the piece at the selected row and column
     */
    public Piece getContents(int row, int col) {
        return this.board[row][col];
    }

    /**
     * Check whether or not there will be a collision when a piece moves
     *
     * @param selectedCol the column of the selected piece to move
     * @param selectedRow the row of the selected piece to move
     * @param moveCol the column of the space to move to
     * @param moveRow the row of the space to move to
     * @param diagonal whether or not to check the diagonal
     * @return whether or not there is a collision
     */
    public boolean hasCollision(int selectedCol, int selectedRow, int moveCol, int moveRow, boolean diagonal) {
        int currentCol = selectedCol;
        int currentRow = selectedRow;
        if (diagonal) {
            if (moveRow - selectedRow < 0) { //moving up
                if (moveCol - selectedCol < 0) { //moving up-left
                    currentCol--;
                    currentRow--;
                    while (currentCol > moveCol && currentRow > moveRow) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentCol--;
                        currentRow--;
                    }
                    return true;
                } else { //moving up-right
                    currentCol++;
                    currentRow--;
                    while (currentCol < moveCol && currentRow > moveRow) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentCol++;
                        currentRow--;
                    }
                    return true;
                }
            } else { //moving down
                if (moveCol - selectedCol < 0) { //moving down-left
                    currentCol--;
                    currentRow++;
                    while (currentCol > moveCol && currentRow < moveRow) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentCol--;
                        currentRow++;
                    }
                    return true;
                } else { //moving down-right
                    currentCol++;
                    currentRow++;
                    while (currentCol < moveCol && currentRow < moveRow) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentCol++;
                        currentRow++;
                    }
                    return true;
                }
            }
        } else {
            if (moveCol != selectedCol) { //moving left or right
                if (selectedCol > moveCol) { //moving left
                    currentCol--;
                    while (currentCol > moveCol) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentCol--;
                    }
                    return true;
                } else { //moving right
                    currentCol++;
                    while (currentCol < moveCol) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentCol++;
                    }
                    return true;
                }
            } else { //moving up or down
                if (selectedRow > moveRow) { //moving up
                    currentRow--;
                    while (currentRow > moveRow) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentRow--;
                    }
                    return true;
                } else { //moving down
                    currentRow++;
                    while (currentRow < moveRow) {
                        if (getContents(currentRow, currentCol) != Piece.NONE) {
                            return false;
                        }
                        currentRow++;
                    }
                    return true;
                }
            }
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
        if (selectedCol >= COLS || moveCol >= COLS || selectedCol < 0 || moveCol < 0) {
            return false;
        } else if (selectedRow >= ROWS || moveRow >= ROWS || selectedRow < 0 || moveRow < 0) {
            return false;
        } else if (selectedCol == moveCol && selectedRow == moveRow) {
            return false;
        } else if (this.board[moveRow][moveCol].equals(Piece.NONE)) {
            return false;
        }
        Piece next = this.board[selectedRow][selectedCol];
        switch (next) {
            case BISHOP -> {
                if (selectedCol == moveCol || selectedRow == moveRow) {
                    return false;
                } else {
                    if (Math.abs(moveRow - selectedRow) == Math.abs(moveCol - selectedCol)) {
                        return hasCollision(selectedCol, selectedRow, moveCol, moveRow, true);
                    } else {
                        return false;
                    }
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
                    return selectedRow - moveRow == 1 && Math.abs(moveCol - selectedCol) == 1;
                }
            }
            case QUEEN -> {
                if (selectedCol == moveCol || selectedRow == moveRow) {
                    return hasCollision(selectedCol, selectedRow, moveCol, moveRow, false);
                } else {
                    if (Math.abs(moveRow - selectedRow) == Math.abs(moveCol - selectedCol)) {
                        return hasCollision(selectedCol, selectedRow, moveCol, moveRow, true);
                    } else {
                        return false;
                    }
                }
            }
            case ROOK -> {
                if (moveCol != selectedCol) {
                    if (moveRow == selectedRow) {
                        return hasCollision(selectedCol, selectedRow, moveCol, moveRow, false);
                    } else {
                        return false;
                    }
                } else {
                    return hasCollision(selectedCol, selectedRow, moveCol, moveRow, false);
                }
            }
            default -> {
                return false;
            }
        }
        return false;
    }

    /**
     * Make a move by selecting a piece to move, and a space to move it to.
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

        // check if the game has been won, is no longer solvable, or is still going on
        if (this.numPieces == 1) {
            this.status = Status.SOLVED;
        }

        // let the view know a move has been made
        notifyObservers();
    }

    /**
     * Returns a string representation of the board, suitable for printing out.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                Piece current = this.board[row][col];
                String next;
                switch (current) {
                    case BISHOP -> {
                        next = "B";
                    }
                    case KING -> {
                        next = "K";
                    }
                    case KNIGHT -> {
                        next = "N";
                    }
                    case PAWN -> {
                        next = "P";
                    }
                    case QUEEN -> {
                        next = "Q";
                    }
                    case ROOK -> {
                        next = "R";
                    }
                    default -> {
                        next = "-";
                    }
                }
                boardString.append(next).append(" ");
            }
            boardString.append("\n");
        }
        return String.valueOf(boardString);
    }
}