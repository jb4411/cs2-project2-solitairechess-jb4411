package soltrchess.backtracking;

import soltrchess.model.SoltrChessModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SoltrChessConfig implements Configuration {
    /** the game board */
    private SoltrChessModel board;
    /** the game board */
    private SoltrChessModel.Piece[][] pieceBoard;
    /** the current number of pieces on the board */
    private int numPieces;
    /** a list of the coordinates of all pieces currently on the board */
    private ArrayList<ArrayList<Integer>> pieces;

    SoltrChessConfig(SoltrChessModel board, SoltrChessModel.Piece[][] pieceBoard) throws FileNotFoundException {
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

    public SoltrChessConfig(SoltrChessConfig copy, ArrayList<Integer> startPiece, ArrayList<Integer> endPiece) {
        this.board = new SoltrChessModel(copy.board);
        this.numPieces = copy.numPieces;
        this.pieceBoard = new SoltrChessModel.Piece[SoltrChessModel.ROWS][SoltrChessModel.COLS];
        this.pieces = new ArrayList<>();
        for (int r=0; r<SoltrChessModel.ROWS; r++) {
            System.arraycopy(copy.pieceBoard[r], 0, this.pieceBoard[r], 0, SoltrChessModel.COLS);
        }
        for (int row=0; row<SoltrChessModel.ROWS; ++row) {
            for (int col = 0; col < SoltrChessModel.COLS; ++col) {
                if (this.pieceBoard[row][col] != SoltrChessModel.Piece.NONE) {
                    this.numPieces++;
                    this.pieces.add(new ArrayList<>(Arrays.asList(row,col)));
                }
            }
        }
        this.board.makeMove(startPiece.get(1), startPiece.get(0), endPiece.get(1), endPiece.get(0));
    }

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

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isGoal() {
        return this.numPieces == 1;
    }
}
