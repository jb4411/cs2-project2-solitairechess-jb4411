package soltrchess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import soltrchess.model.Observer;
import soltrchess.model.SoltrChessModel;

import javax.security.auth.Subject;
import java.util.List;

public class SoltrChessGUI extends Application implements Observer<SoltrChessModel,String> {
    /** bishop image */
    private Image bishop = new Image(getClass().getResourceAsStream("resources/bishop.png"));
    /** blue image */
    private Image blue = new Image(getClass().getResourceAsStream("resources/blue.png"));
    /** dark image */
    private Image dark = new Image(getClass().getResourceAsStream("resources/dark.png"));
    /** king image */
    private Image king = new Image(getClass().getResourceAsStream("resources/king.png"));
    /** knight image */
    private Image knight = new Image(getClass().getResourceAsStream("resources/knight.png"));
    /** light image */
    private Image light = new Image(getClass().getResourceAsStream("resources/light.png"));
    /** pawn image */
    private Image pawn = new Image(getClass().getResourceAsStream("resources/pawn.png"));
    /** queen image */
    private Image queen = new Image(getClass().getResourceAsStream("resources/queen.png"));
    /** rook image */
    private Image rook = new Image(getClass().getResourceAsStream("resources/rook.png"));
    /** white image */
    private Image white = new Image(getClass().getResourceAsStream("resources/white.png"));

    /** the Label that stores the current game status */
    private Label statusBar;
    /** the game board */
    private SoltrChessModel board;
    /** a 2D array of every button in the board */
    private ChessButton[][] buttonBoard;
    /** whether or not the game has ended */
    private boolean finished;
    /** the HBox that stores the buttons for the control button bar */
    private HBox controlButtons;

    private class ChessButton extends Button {
        /** this button's row */
        private int row;
        /** this button's column */
        private int col;
        /** this button's current piece */
        private SoltrChessModel.Piece piece;
        /** this button's current piece image */
        private ImageView pieceImage;
        /** this button's graphic */
        private StackPane graphic;

        /**
         * A helper function that changes the piece and image of the button
         * passed in through the parameter.
         *
         * @param piece the button to have it's image and piece changed
         */
        public void changePiece(SoltrChessModel.Piece piece) {
            this.piece = piece;
            switch (piece) {
                case BISHOP -> {
                    this.pieceImage = new ImageView(bishop);
                    this.pieceImage.setVisible(true);
                    this.setGraphic(this.graphic);
                }
                case KING -> {
                    this.pieceImage = new ImageView(king);
                    this.pieceImage.setVisible(true);
                    this.setGraphic(this.graphic);
                }
                case KNIGHT -> {
                    this.pieceImage = new ImageView(knight);
                    this.pieceImage.setVisible(true);
                    this.setGraphic(this.graphic);
                }
                case PAWN -> {
                    this.pieceImage = new ImageView(pawn);
                    this.pieceImage.setVisible(true);
                    this.setGraphic(this.graphic);
                }
                case QUEEN -> {
                    this.pieceImage = new ImageView(queen);
                    this.pieceImage.setVisible(true);
                    this.setGraphic(this.graphic);
                }
                case ROOK -> {
                    this.pieceImage = new ImageView(rook);
                    this.pieceImage.setVisible(true);
                    this.setGraphic(this.graphic);
                }
                default -> {
                    this.pieceImage = new ImageView(blue);
                    this.pieceImage.setVisible(false);
                    this.setGraphic(this.graphic);
                }
            }
        }

        /**
         * Create a new ConnectButton with no owner and an image of an empty
         * space.
         *
         * @param row this button's row
         * @param col this button's column
         * @param piece what piece this button is
         */
        public ChessButton(int row, int col, SoltrChessModel.Piece piece) {
            this.row = row;
            this.col = col;
            this.changePiece(piece);
        }
    }

    /**
     * A helper function that builds a grid of buttons used as the GUI
     * representation of the board.
     *
     * @return the grid pane representing the board
     */
    private GridPane makeBoard(SoltrChessModel board) {
        GridPane gridPane = new GridPane();
        //build the grid of buttons
        boolean color = true;
        for (int row = 0; row < SoltrChessModel.ROWS; row++) {
            for (int col = 0; col < SoltrChessModel.COLS; col++) {
                ChessButton button = new ChessButton(row,col, board.getContents(row,col));
                //button.setMinWidth(100);
                //button.setMinHeight(100);
                if (color) {
                    button.pieceImage = new ImageView(blue);
                    button.pieceImage.setVisible(false);
                    button.graphic = new StackPane();
                    button.graphic.getChildren().addAll(new ImageView(light), button.pieceImage);
                    button.setGraphic(button.graphic);
                    color = false;
                } else {
                    button.pieceImage = new ImageView(blue);
                    button.pieceImage.setVisible(false);
                    button.graphic = new StackPane();
                    button.graphic.getChildren().addAll(new ImageView(dark), button.pieceImage);
                    button.setGraphic(button.graphic);
                    color = true;
                }

                //button.setOnAction(event -> { if (this.board.isValidMove()) {this.board.makeMove(button.col);}});
                gridPane.add(button,col,row);
                this.buttonBoard[row][col] = button;
            }
        }
        return gridPane;
    }

    /**
     * Construct the layout for the game.
     *
     * @param stage container (window) in which to render the GUI
     * @throws Exception if there is a problem
     */
    @Override
    public void start(Stage stage) throws Exception {
        //create the border pane that holds the board and status info
        BorderPane borderPane = new BorderPane();
        //create the board
        this.board = new SoltrChessModel(getParameters().getRaw().get(0));
        this.board.addObserver(this);

        //initialize variables
        this.finished = false;
        this.buttonBoard = new ChessButton[SoltrChessModel.ROWS][SoltrChessModel.COLS];

        //create the status bar
        this.statusBar = new Label("Game file: " + getParameters().getRaw().get(0));
        borderPane.setTop(this.statusBar);
        BorderPane.setAlignment(this.statusBar, Pos.CENTER);

        //create the control button bar
        this.controlButtons = new HBox();
        borderPane.setBottom(this.controlButtons);

        // get the grid pane from the helper method
        GridPane gridPane = makeBoard(this.board);
        borderPane.setCenter(gridPane);

        //store and display board
        Scene scene = new Scene(borderPane);
        stage.setTitle("Solitaire Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    @Override
    public void update(SoltrChessModel soltrChessModel, String s) {

    }
}