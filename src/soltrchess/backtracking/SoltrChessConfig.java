package soltrchess.backtracking;

import soltrchess.model.SoltrChessModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a single configuration in the SoltrChess game. (Includes extra credit portion)
 *
 * @author Jesse Burdick-Pless jb4411@g.rit.edu
 */
public class SoltrChessConfig implements Configuration {
    /** the game board */
    private SoltrChessModel board;
    /** a 2D array of the pieces on the board */
    private SoltrChessModel.Piece[][] pieceBoard;
    /** the current number of pieces on the board */
    private int numPieces;
    /** a list of the coordinates of all pieces currently on the board */
    private ArrayList<ArrayList<Integer>> pieces;

    /**
     * Constructor
     *
     * @param board the model representing the board
     * @param pieceBoard a 2D array of pieces on the board
     */
    public SoltrChessConfig(SoltrChessModel board, SoltrChessModel.Piece[][] pieceBoard) {
        this.pieceBoard = pieceBoard;
        this.numPieces = 0;
        this.pieces = new ArrayList<>();
        this.board = board;

        for (int row=0; row< SoltrChessModel.ROWS; ++row) {
            for (int col = 0; col < SoltrChessModel.COLS; ++col) {
                if (this.pieceBoard[row][col] != SoltrChessModel.Piece.NONE) {
                    this.numPieces++;
                    this.pieces.add(new ArrayList<>(Arrays.asList(row,col)));
                }
            }
        }
    }

    /**
     * Copy constructor
     *
     * @param copy SoltrChessConfig instance
     * @param startPiece the coordinates of the piece being moved
     * @param endPiece the coordinates of the piece being taken
     */
    public SoltrChessConfig(SoltrChessConfig copy, ArrayList<Integer> startPiece, ArrayList<Integer> endPiece) {
        this.board = new SoltrChessModel(copy.board);
        this.numPieces = 0;
        this.pieceBoard = new SoltrChessModel.Piece[SoltrChessModel.ROWS][SoltrChessModel.COLS];
        this.pieces = new ArrayList<>();
        this.board.makeMove(startPiece.get(1), startPiece.get(0), endPiece.get(1), endPiece.get(0));
        for (int row=0; row< SoltrChessModel.ROWS; ++row) {
            for (int col = 0; col < SoltrChessModel.COLS; ++col) {
                this.pieceBoard[row][col] = this.board.getContents(row, col);
            }
        }

        for (int row=0; row<SoltrChessModel.ROWS; ++row) {
            for (int col = 0; col < SoltrChessModel.COLS; ++col) {
                if (this.pieceBoard[row][col] != SoltrChessModel.Piece.NONE) {
                    this.numPieces++;
                    this.pieces.add(new ArrayList<>(Arrays.asList(row,col)));
                }
            }
        }
    }

    /**
     * Get the game board.
     *
     * @return game board
     */
    public SoltrChessModel getBoard() {
        return this.board;
    }

    /**
     * Create a collection of possible successors of the current configuration.
     * This function only generates valid successors.
     *
     * @return Collection of Configurations
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        ArrayList<Configuration> successors = new ArrayList<>();
        ArrayList<Integer> startPiece;
        ArrayList<Integer> endPiece;
        for (int i = 0; i < this.pieces.size(); i++) {
            for (int j = 0; j < this.pieces.size(); j++) {
                if (i != j) {
                    startPiece = this.pieces.get(i);
                    endPiece = this.pieces.get(j);
                    if (this.board.isValidMove(startPiece.get(1), startPiece.get(0), endPiece.get(1), endPiece.get(0))) {
                        SoltrChessConfig child = new SoltrChessConfig(this, startPiece, endPiece);
                        successors.add(child);
                    }
                }
            }
        }
        return successors;
    }

    /**
     * isValid() - always returns true because only valid configurations are
     * generated by getSuccessors().
     *
     * @return true
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Check if the current configuration is the goal configuration.
     *
     * @return whether or not the current configuration is the goal configuration
     */
    @Override
    public boolean isGoal() {
        return this.numPieces == 1;
    }

    /**
     * toString() method
     *
     * @return the boards toString()
     */
    @Override
    public String toString() {
        return this.board.toString();
    }
}
