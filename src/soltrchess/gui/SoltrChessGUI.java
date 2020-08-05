package soltrchess.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import soltrchess.backtracking.Backtracker;
import soltrchess.backtracking.Configuration;
import soltrchess.backtracking.SoltrChessConfig;
import soltrchess.model.Observer;
import soltrchess.model.SoltrChessModel;

import javax.security.auth.Subject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A JavaFX GUI for the Solitaire Chess game.
 *
 * @author RIT CS
 * @author Jesse Burdick-Pless jb4411@g.rit.edu
 */
public class SoltrChessGUI extends Application implements Observer<SoltrChessModel, SoltrChessModel.Status> {
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
    /** a 2D array of every button on the board */
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
    /** current step if solving */
    private int currentStep;
    /** the final step number if solving */
    private int finalStep;

    /**
     * Has a starting piece been selected?
     *
     * @return whether or not a starting piece has been selected
     */
    public boolean hasBeenSelected() {
        return this.selected;
    }

    /**
     * A class to represent the buttons used to play solitaire chess.
     */
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
         * @param piece the button to have its image and piece changed
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
        boolean color = false;
        for (int row = 0; row < SoltrChessModel.ROWS; row++) {
            color = !color;
            for (int col = 0; col < SoltrChessModel.COLS; col++) {
                ChessButton button = new ChessButton(row,col);
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
                button.setOnAction(event -> {
                    if (!this.selected) {
                        if (!this.finished) {
                            this.selected = true;
                            this.selectedRow = button.row;
                            this.selectedCol = button.col;
                            this.statusBar.setText("Source selected: (" + this.selectedRow + "," + this.selectedCol + ")");
                        }
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

    /**
     * Shows a popup informing the user that the file choosen is invalid or
     * cannot be read.
     *
     * @param filename the name of the invalid file
     */
    public static void ErrorPopup(String filename) {
        //create the border pane that holds the board and status info
        BorderPane borderPane = new BorderPane();

        //bottom box
        HBox bottomBox = new HBox();
        Label errorMsg = new Label("Invalid content found in " + filename);
        errorMsg.setPadding(new Insets(20, 0, 50, 0));
        Button errorButton = new Button("OK");
        errorButton.setPrefWidth(80);
        bottomBox.setSpacing(40);
        bottomBox.getChildren().addAll(errorMsg, errorButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        borderPane.setBottom(bottomBox);

        //top box
        HBox topBox = new HBox();
        Label message = new Label("Message");
        message.setPadding(new Insets(30, 0, 0, 10));
        message.setFont(new Font("Arial", 15));
        topBox.getChildren().addAll(message);
        borderPane.setTop(topBox);

        // set up scene and stage
        Scene errorScene = new Scene(borderPane, 360, 160);
        Stage newWindow = new Stage();
        newWindow.setTitle("Message");
        newWindow.setScene(errorScene);

        errorButton.setOnAction(event -> newWindow.hide());
        newWindow.show();
    }

    /**
     * A helper function that is called to restart the game.
     *
     * @param filename the file that contains the current board.
     */
    public void restart(String filename) {
        try {
            this.board = new SoltrChessModel(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.finished = false;
        this.selected = false;
        this.board.addObserver(this);
        String[] filenameParts = filename.split("/");
        String shortName = filenameParts[filenameParts.length - 1];
        this.statusBar.setText("Game file: " + shortName);
        if (this.board.getGameStatus() == SoltrChessModel.Status.SOLVED) {
            this.statusBar.setText("You Won!");
            this.finished = true;
        } else if (this.board.getGameStatus() == SoltrChessModel.Status.INVALID_FILE) {
            this.statusBar.setText("Invalid file.");
            ErrorPopup(shortName);
            this.finished = true;
        }
        this.update(this.board, SoltrChessModel.Status.NOT_OVER);
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
        String[] filenameParts = this.currentFile.split("/");
        String shortName = filenameParts[filenameParts.length - 1];
        this.statusBar = new Label("Game file: " + shortName);
        if (this.board.getGameStatus() == SoltrChessModel.Status.SOLVED) {
            this.statusBar.setText("You Won!");
            this.finished = true;
        } else if (this.board.getGameStatus() == SoltrChessModel.Status.INVALID_FILE) {
            this.statusBar.setText("Invalid file.");
            this.finished = true;
        }
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
        //create restart button
        Button restart = new Button("Restart");
        restart.setOnAction(event -> {
            this.restart(this.currentFile);
        });
        this.controlButtons.getChildren().add(restart);
        //create hint button
        Button hint = new Button("Hint");
        hint.setOnAction(event -> {
            if (!this.finished) {
                Backtracker solver = new Backtracker();
                List<Configuration> solution = solver.solveWithPath(new SoltrChessConfig(new SoltrChessModel(this.board), this.board.getPieceBoard()));
                if (solution != null) {
                    solution.remove(0);
                    SoltrChessConfig configBoard = (SoltrChessConfig) solution.get(0);
                    this.board = configBoard.getBoard();
                    this.board.addObserver(this);
                    this.statusBar.setText("Next move: ");
                    this.update(this.board, this.board.getGameStatus());
                } else {
                    this.statusBar.setText("No solution");
                }
            } else {
                this.statusBar.setText("You've already won.");
            }
        });
        this.controlButtons.getChildren().add(hint);
        //create solve button
        Button solve = new Button("Solve");
        solve.setOnAction(event -> {
            if (!this.finished) {
                //solve with path
                Backtracker solver = new Backtracker();
                List<Configuration> solution = solver.solveWithPath(new SoltrChessConfig(new SoltrChessModel(this.board), this.board.getPieceBoard()));
                if (solution != null) {
                    this.finalStep = solution.size();
                    Solver guiSolve = new Solver(this, solution);
                    guiSolve.start();
                } else {
                    this.statusBar.setText("No solution");
                }
            } else {
                this.statusBar.setText("You've already won.");
            }
        });
        this.controlButtons.getChildren().add(solve);
        //add control buttons to the borderpane
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
        if (this.board.getGameStatus() == SoltrChessModel.Status.INVALID_FILE) {
            ErrorPopup(shortName);
        }
    }

    /**
     * A subclass used to show the steps of the solve function.
     */
    private static class Solver extends Thread {
        /** the javaFX GUI */
        private SoltrChessGUI gui;
        /** the steps to solve the current board */
        private List<Configuration> solution;

        /**
         * Constructor.
         *
         * @param gui the javaFX GUI
         * @param solution the steps to solve the current board
         */
        public Solver(SoltrChessGUI gui, List<Configuration> solution) {
            this.gui = gui;
            this.solution = solution;
        }

        /**
         * Show the steps of the solve function and update the GUI accordingly.
         */
        @Override
        public void run() {
            for (int i = 0; i < this.solution.size(); i++) {
                SoltrChessConfig configBoard = (SoltrChessConfig) this.solution.get(i);
                this.gui.board = configBoard.getBoard();
                this.gui.board.addObserver(this.gui);
                try {
                    Thread.sleep( 1000 );
                    this.gui.currentStep = i+1;
                    javafx.application.Platform.runLater( () ->
                        this.gui.update(this.gui.board, SoltrChessModel.Status.SOLVING)
                    );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called by the model, model.SoltrChessModel, whenever there is a state
     * change that needs to be updated by the GUI.
     *
     * @param soltrChessModel the board
     * @param gameStatus the current status of the game
     */
    @Override
    public void update(SoltrChessModel soltrChessModel, SoltrChessModel.Status gameStatus) {
        for (int row = 0; row < SoltrChessModel.ROWS; row++) {
            for (int col = 0; col < SoltrChessModel.COLS; col++) {
                this.buttonBoard[row][col].changePiece(this.board.getContents(row, col));
            }
            if (gameStatus != SoltrChessModel.Status.NOT_OVER && gameStatus != SoltrChessModel.Status.SOLVING) {
                this.finished = true;
                if (gameStatus == SoltrChessModel.Status.SOLVED) {
                    this.statusBar.setText("You won. Congratulations!");
                }
            } else if (gameStatus == SoltrChessModel.Status.SOLVING){
                if (this.currentStep != this.finalStep) {
                    this.statusBar.setText("STEP " + this.currentStep);
                } else {
                    this.statusBar.setText("You won. Congratulations!");
                    this.finished = true;
                }
            }
        }
    }
}