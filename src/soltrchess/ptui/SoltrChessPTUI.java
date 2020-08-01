package soltrchess.ptui;

import soltrchess.model.Observer;
import soltrchess.model.SoltrChessModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SoltrChessPTUI implements Observer<SoltrChessModel, SoltrChessModel.Status> {
    /** the game board */
    private SoltrChessModel board;
    /** whether or not the game has ended */
    private boolean finished;
    /** the current file */
    private String currentFile;
    /** the list of valid commands */
    private static final ArrayList<String> VALID_COMMANDS = new ArrayList<>(Arrays.asList("move", "new", "restart", "hint", "solve", "quit"));

    /**
     * Construct the PTUI.
     *
     * @param filename the file name
     */
    public SoltrChessPTUI(String filename) {
        this.currentFile = filename;
        this.restart(filename);
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
        this.board.addObserver(this);
        String[] filenameParts = filename.split("/");
        String shortName = filenameParts[filenameParts.length - 1];
        System.out.println("\nGame File: " + shortName);
        if (this.board.getGameStatus() == SoltrChessModel.Status.SOLVED) {
            System.out.println("You won. Congratulations!");
            this.finished = true;
        } else if (this.board.getGameStatus() == SoltrChessModel.Status.INVALID_FILE) {
            System.out.println("Invalid file.");
            //ErrorPopup(shortName);
            this.finished = true;
        }
        this.update(this.board, SoltrChessModel.Status.NOT_OVER);
    }

    /**
     * A helper function for getting commands from the user.
     *
     * @param in the scanner that is getting input from the user
     * @return a valid command
     */
    private String getCommand(Scanner in) {
        boolean validCMD = false;
        String command = null;
        while (!validCMD) {
            validCMD = true;
            System.out.print(" [move,new,restart,hint,solve,quit]> ");
            String cmd = in.nextLine();
            if (VALID_COMMANDS.contains(cmd)) {
                command = cmd;
                break;
            }
        }
        return command;
    }

    /**
     * A helper function for making moves.
     *
     * @param in the scanner that is getting input from the user
     */
    private void makeMove(Scanner in) {
        int selectedRow;
        int selectedCol;
        int moveRow;
        int moveCol;
        boolean validMove = false;

        while (!validMove) {
            System.out.print("source row? ");
            selectedRow = in.nextInt();
            System.out.print("source col? ");
            selectedCol = in.nextInt();
            System.out.print("dest row? ");
            moveRow = in.nextInt();
            System.out.print("dest col? ");
            moveCol = in.nextInt();
            validMove = this.board.isValidMove(selectedCol,selectedRow,moveCol,moveRow);
            if (!validMove) {
                System.out.print("\nInvalid move.\n");
            } else {
                System.out.println((this.board.getContents(selectedRow, selectedCol) + " to (" + moveRow + "," + moveCol + ")"));
                this.board.makeMove(selectedCol,selectedRow,moveCol,moveRow);
                //this.update(this.board, this.board.getGameStatus());
            }
        }

    }

    /**
     * The main command loop.
     */
    public void run() {
        boolean running = true;
        while (running) {
            Scanner in = new Scanner(System.in);
            String cmd = getCommand(in);
            switch (cmd) {
                case "move" -> {
                    if (!this.finished) {
                        this.makeMove(in);
                    } else {
                        System.out.print("You've already won.");
                    }
                }
                case "new" -> {
                    System.out.print("game file name: ");
                    String newFile = in.nextLine();
                    this.currentFile = newFile;
                    this.restart(newFile);
                }
                case "restart" -> {
                    this.restart(this.currentFile);
                }
                case "hint" -> {

                }
                case "solve" -> {

                }
                case "quit" -> {
                    running = false;
                }
            }
        }
    }

    /**
     * Called by the model, model.SoltrChessModel, whenever there is a state
     * change that needs to be updated by the PTUI.
     *
     * @param soltrChessModel the board
     * @param gameStatus the current status of the game
     */
    @Override
    public void update(SoltrChessModel soltrChessModel, SoltrChessModel.Status gameStatus) {
        if (gameStatus != SoltrChessModel.Status.INVALID_FILE) {
            System.out.print(this.board.toString());
        }
        if (gameStatus != SoltrChessModel.Status.NOT_OVER) {
            this.finished = true;
            if (gameStatus == SoltrChessModel.Status.SOLVED) {
                System.out.println("You won. Congratulations!");
            }
        }
    }
}