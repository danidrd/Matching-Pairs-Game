package io.github.danidrd.matchingpairs;

import io.github.danidrd.matchingpairs.controller.GameController;
import io.github.danidrd.matchingpairs.view.BoardView;

import javax.swing.*;

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

            String playerName = JOptionPane.showInputDialog(
                    null,
                    "Enter your name:",
                    "Player Registration",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Guest";
            }

            GameController controller = new GameController(playerName); // Create controller
            BoardView boardView = new BoardView(controller); // Example with 16 cards
            controller.initialize(boardView);


        });
    }

}
