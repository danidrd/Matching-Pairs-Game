package io.github.danidrd.matchingpairs;

import io.github.danidrd.matchingpairs.controller.GameController;
import io.github.danidrd.matchingpairs.view.BoardView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MatchingPairsGame {
    /**
     * This is the main entry point of the game.
     * It will run asynchronously on the AWT event dispatching thread.
     * <p>
     * It creates a new {@link BoardView} with 16 cards and a new
     * {@link GameController} passing the {@link BoardView} as argument.
     * <p>
     * @param args the command line arguments
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {

            int numPlayers = getNumberOfPlayers();
            List<String> playerNames = getPlayerNames(numPlayers);


            GameController controller = new GameController(playerNames); // Create controller
            BoardView boardView = new BoardView(controller); // Example with 16 cards
            controller.initialize(boardView);


        });
    }

    /**
     * Gets the number of players from the user.
     * <p>
     * This method will continue to prompt the user for input until
     * a valid number of players is entered. A valid number of players
     * is a positive integer between 1 and 4 (inclusive).
     * <p>
     * @return the number of players
     */
    private static int getNumberOfPlayers() {
        while (true) {
            try{
                String input = JOptionPane.showInputDialog(
                        null,
                        "Enter the number of players (1-4):",
                        "Choose Number of Players",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (input == null || input.trim().isEmpty()){
                    System.exit(0);
                }

                int numPlayers = Integer.parseInt(input.trim());
                if (numPlayers >= 1 && numPlayers <= 4){
                    return numPlayers;
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Invalid input! Please enter a number between 1 and 4.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid input! Please enter a number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Gets the names of the players from the user.
     * <p>
     * This method will continue to prompt the user for input until
     * a valid name is entered for each of the given number of players.
     * A valid name is a non-empty string.
     * <p>
     * @param numPlayers the number of players to prompt for
     * @return a list of player names
     */
    private static List<String> getPlayerNames(int numPlayers) {
        List<String> playerNames = new ArrayList<>();
        for( int i = 1; i <= numPlayers; i++){
            String name = JOptionPane.showInputDialog(
                    null,
                    "Enter the name for Player " + i + ":",
                    "Player Name",
                    JOptionPane.PLAIN_MESSAGE
            );

            if ( name == null || name.trim().isEmpty()){
                name = "Player " + i; // Default name
            }

            playerNames.add(name);
        }

        return playerNames;
    }


}
