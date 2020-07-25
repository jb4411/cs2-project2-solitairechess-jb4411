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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import soltrchess.model.Observer;
import soltrchess.model.SoltrChessModel;

import javax.security.auth.Subject;
import java.io.File;
import java.io.FileNotFoundException;
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
    /** has a starting piece been selected? */
    private boolean selected;
    /** the column of the piece that was selected */
    private int selectedCol;
    /** the row of the piece that was selected */
    private int selectedRow;
    /** the file chooser */
    private FileChooser fileChooser;
    /** the current file */
    private String currentFile;

    /**
     * Has a starting piece been selected?
     *
     * @return whether or not a starting piece has been selected
     */
    public boolean hasBeenSelected() {
        return this.selected;
    }

    private class ChessButton extends Button {
        /** this button's row */
        private int row;
        /** this button's column */
        private int col;
        /** this button's current piece */
        private SoltrChessModel.Piece piece;
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
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(new ImageView(bishop));
                    this.setGraphic(this.graphic);
                }
                case KING -> {
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(new ImageView(king));
                    this.setGraphic(this.graphic);
                }
                case KNIGHT -> {
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(new ImageView(knight));
                    this.setGraphic(this.graphic);
                }
                case PAWN -> {
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(new ImageView(pawn));
                    this.setGraphic(this.graphic);
                }
                case QUEEN -> {
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(new ImageView(queen));
                    this.setGraphic(this.graphic);
                }
                case ROOK -> {
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(new ImageView(rook));
                    this.setGraphic(this.graphic);
                }
                default -> {
                    ImageView pieceImage = new ImageView(blue);
                    pieceImage.setVisible(false);
                    this.graphic.getChildren().remove(1);
                    this.graphic.getChildren().add(pieceImage);
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
         */
        public ChessButton(int row, int col) {
            this.row = row;
            this.col = col;
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
                ChessButton button = new ChessButton(row,col);
                //button.setMinWidth(100);
                //button.setMinHeight(100);
                if (color) {
                    ImageView pieceImage = new ImageView(blue);
                    pieceImage.setVisible(false);
                    button.graphic = new StackPane();
                    button.graphic.getChildren().addAll(new ImageView(light), pieceImage);
                    button.setGraphic(button.graphic);
                    button.changePiece(this.board.getContents(row,col));
                    color = false;
                } else {
                    ImageView pieceImage = new ImageView(blue);
                    pieceImage.setVisible(false);
                    button.graphic = new StackPane();
                    button.graphic.getChildren().addAll(new ImageView(dark), pieceImage);
                    button.setGraphic(button.graphic);
                    button.changePiece(this.board.getContents(row,col));
                    color = true;
                }

                //button.setOnAction(event -> { if (this.board.isValidMove()) {this.board.makeMove(button.col);}});
                //button.setOnAction(event -> { button.changePiece(SoltrChessModel.Piece.QUEEN);});
                button.setOnAction(event -> {
                    if (!this.selected) {
                        this.selected = true;
                        this.selectedRow = button.row;
                        this.selectedCol = button.col;
                        this.statusBar.setText("Source selected: (" + this.selectedRow + "," + this.selectedCol + ")");
                    } else {
                        this.selected = false;
                        if (this.board.isValidMove(this.selectedCol, this.selectedRow, button.col, button.row)) {
                            this.statusBar.setText(this.board.getContents(this.selectedRow, this.selectedCol) + " to (" + button.row + "," + button.col + ")");
                            this.board.makeMove(this.selectedCol, this.selectedRow, button.col, button.row);
                        } else {
                            this.statusBar.setText("Move not Allowed.");
                        }
                    }});
                gridPane.add(button,col,row);
                this.buttonBoard[row][col] = button;
            }
        }
        return gridPane;
    }

    public void restart(String filename) {
        try {
            this.board = new SoltrChessModel(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.selected = false;
        this.board.addObserver(this);
        this.statusBar.setText("Game file: " + filename);
        this.update(this.board, "restart");
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
        this.selected = false;
        this.finished = false;
        this.buttonBoard = new ChessButton[SoltrChessModel.ROWS][SoltrChessModel.COLS];
        this.currentFile = getParameters().getRaw().get(0);

        //create the status bar
        this.statusBar = new Label("Game file: " + currentFile);
        borderPane.setTop(this.statusBar);
        BorderPane.setAlignment(this.statusBar, Pos.CENTER);

        //create the control button bar
        this.controlButtons = new HBox();
        //create new game button
        this.fileChooser = new FileChooser();
        Button newGame = new Button("New Game");
        newGame.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            this.currentFile = selectedFile.toString();
            this.restart(this.currentFile);
        });
        this.controlButtons.getChildren().add(newGame);
        //create restart game button
        Button restart = new Button("Restart");
        restart.setOnAction(event -> {
            this.restart(this.currentFile);
        });
        this.controlButtons.getChildren().add(restart);
        this.controlButtons.setAlignment(Pos.CENTER);
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
        for (int row = 0; row < SoltrChessModel.ROWS; row++) {
            for (int col = 0; col < SoltrChessModel.COLS; col++) {
                this.buttonBoard[row][col].changePiece(this.board.getContents(row, col));
            }
        }
    }
}