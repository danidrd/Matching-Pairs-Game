package io.github.danidrd.matchingpairs.view;

import io.github.danidrd.matchingpairs.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.util.List;
import java.util.ArrayList;

/**
 * This class will be the main game windows (JFrame) that holds all the cards,
 * control buttons (like shuffle and exit) and info labels (like matched pairs and total flips)
 */
public class BoardView extends JFrame {

    private  PropertyChangeSupport pcs;
    private  List<CardView> cards = new ArrayList<>();
    private final JButton bestScoreButton = new JButton("Leaderboard");
    private final JLabel pairsLabel = new JLabel("Number of Pairs: 4");
    private final JButton changePairsButton = new JButton("Change Pairs");
    private int numberOfPairs = 4; // Default number of pairs
    private final JButton shuffleButton = new JButton("Shuffle");
    private final JButton exitButton = new JButton("Exit");
    private final JLabel matchedPairsLabel = new JLabel("Matched Pairs: 0");
    private final JLabel totalFlipsLabel = new JLabel("Total Flips: 0");
    private final JPanel cardsPanel = new JPanel();

    /**
     * BoardView constructor
     * Initializes BoardView calling the JFrame constructor,
     * sets the size of the window,
     * sets the layout of the window,
     * sets close operation.
     * Initializes all cards passing the controller and adds them to the cards panel
     * thus, adds the cards panel to the window.
     * Create a control panel with the shuffle button and the exit button
     * and adds it to the window.
     * Create info panel with the matched pairs label and the total flips label
     * and adds it to the window.
     * Finally, sets the window visible
     */
    public BoardView(GameController controller){
        super("Matching Pair Game: " + controller.getCurrentPlayer().getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize cards

        initializeBoard(controller);

        bestScoreButton.addActionListener(e -> {
            showBestScores(controller);
        });
        // Initialize control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.add(bestScoreButton);
        controlPanel.add(changePairsButton);
        controlPanel.add(shuffleButton);
        controlPanel.add(exitButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Initialize info labels
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pairsLabel.setText("Number of Pairs: " + numberOfPairs);
        infoPanel.add(pairsLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(matchedPairsLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(totalFlipsLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Add action listener for changing pairs
        changePairsButton.addActionListener(e -> changeNumberOfPairs(controller));
        addPropertyChangeListener(controller);

        setVisible(true);
    }

    /**
     * Initializes the board with the specified number of pairs by clearing the previous cards,
     * setting the layout of the cards panel to a grid layout, creating and adding the
     * specified number of cards to the panel, and adding it to the window.
     * <p>
     * The size of the grid is determined by taking the square root of the number of cards
     * and rounding up to the nearest whole number.
     * </p>
     * @param controller the game controller to register as a listener for the cards
     */
    private void initializeBoard(GameController controller) {
        // Clear previous cards
        cardsPanel.removeAll();
        cardsPanel.setLayout(new GridLayout(
                (int) Math.sqrt(numberOfPairs * 2),
                (int) Math.ceil((double) numberOfPairs * 2 / (int) Math.sqrt(numberOfPairs * 2)),
                10,
                10
        ));
        controller.setCurrentPlayerIndex(0);
        this.changeNumberOfPairs();
        // Create and add cards
        initializeCards(controller, controller, numberOfPairs * 2, cardsPanel);

        add(cardsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Prompts the user for a new number of pairs and updates the board with the new number if valid.
     * <p>
     * The method will continue to prompt the user for input until a valid number is entered.
     * A valid number is a positive even integer.
     * </p>
     * @param controller the game controller with which to update the board
     */
    private void changeNumberOfPairs(GameController controller) {
        // Prompt for a new number of pairs
        int newPairs = numberOfPairs; // Start with the current value
        while (true){
            try {
                String input = JOptionPane.showInputDialog(
                        this,
                        "Enter the number of pairs (must be an even positive number):",
                        "Change Number of Pairs",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (input == null || input.trim().isEmpty()){
                    break; // Cancel or empty input, keep the current value
                }

                newPairs = Integer.parseInt(input.trim());
                if(newPairs > 0 && newPairs % 2 == 0){
                    numberOfPairs = newPairs;

                    // Reset labels
                    controller.setCurrentPlayerIndex(0);
                    updateUI(controller);


                    // Update board
                    cards = new ArrayList<>();
                    initializeBoard(controller);
                    this.fireShuffleEvent();
                    pairsLabel.setText("Number of Pairs: " + numberOfPairs);

                    break;
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Invallid input! Please enter a positive even number.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (NumberFormatException e){
                JOptionPane.showMessageDialog(
                    this,
                        "Invalid input! Please enter a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    /**
     * Initializes the specified number of cards and adds them to the given panel.
     *
     * <p>This method creates a specified number of {@link CardView} instances,
     * registers the provided property and vetoable change listeners to each card,
     * and adds them to the specified panel. The cards are also added to the internal
     * list of cards for tracking.
     *
     * @param listener the property change listener to be registered with each card
     * @param vetoListener the vetoable change listener to be registered with each card
     * @param totalCards the total number of cards to initialize and add
     * @param cardsPanel the panel to which the initialized cards are to be added
     */
    private void initializeCards(PropertyChangeListener listener, VetoableChangeListener vetoListener, int totalCards, JPanel cardsPanel) {

        for(int i = 0; i < totalCards; i++) {
            CardView card = new CardView();
            card.addPropertyChangeListener(listener);
            card.addVetoableChangeListener(vetoListener);
            cards.add(card);
            cardsPanel.add(card);
        }
    }


    /**
     * Returns a list of all the cards currently on the board.
     *
     * @return the list of cards
     */
    public List<CardView> getCards() {
        return cards;
    }

    /**
     * @return the shuffle button in this board view.
     */
    public JButton getShuffleButton() {
        return shuffleButton;
    }

    /**
     * @return the exit button in this board view.
     */
    public JButton getExitButton() {
        return exitButton;
    }

    /**
     * @return the label displaying the number of matched pairs in this board view.
     */
    public JLabel getMatchedPairsLabel() {
        return matchedPairsLabel;
    }

    /**
     * @return the label displaying the total number of flips in this board view.
     */
    public JLabel getTotalFlipsLabel() {
        return totalFlipsLabel;
    }

    /**
     * Adds a listener to the list of listeners that are notified when
     * bound properties of this {@link BoardView} change.
     *
     * <p>This method registers a listener so it receives property change
     * notifications from this board view. The listener is notified when
     * bound properties of this board view change, and can then react
     * accordingly.
     *
     * @param listener the listener to be registered
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from the listener list.
     *
     * <p>This method deregisters a listener so it no longer receives
     * property change notifications from this {@link BoardView}.
     *
     * @param listener the listener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }

    /**
     * Provides access to the property change support for this board view.
     *
     * <p>This method lazily initializes the property change support and
     * returns it. The property change support is used to manage listeners that
     * are notified when bound properties of this board view change.
     *
     * @return the property change support associated with this board view
     */
    public PropertyChangeSupport getPropertyChangeSupport() {
        if(pcs == null){
            pcs = new PropertyChangeSupport(this);
        }
        return pcs;
    }

    /**
     * Fires a property change event with the name "shuffle".
     *
     * <p>This method is used by the action listener attached to the shuffle
     * button to notify registered listeners of a shuffle event.
     */
    private void fireShuffleEvent() {
        getPropertyChangeSupport().firePropertyChange("shuffle", false, true);
    }

    /**
     * Attaches an action listener to the shuffle button that fires a shuffle event.
     *
     * <p>This method ensures that whenever the shuffle button is clicked,
     * a property change event with the name "shuffle" is fired, which can
     * trigger a shuffle action in the game.
     */
    private void changeNumberOfPairs() {
        shuffleButton.addActionListener(e -> fireShuffleEvent());
    }

    /**
     * Displays the best scores for a specified board size.
     *
     * <p>This method prompts the user to enter a board size and retrieves
     * the leaderboard entries for that size from the provided GameController.
     * If no entries are found, a dialog is shown indicating that no games
     * exist for the specified size. Otherwise, a leaderboard is displayed
     * with the player names and their corresponding scores.
     *
     * <p>If the user enters an invalid board size, an error dialog is displayed.
     *
     * @param controller the game controller used to access leaderboard data
     */
    private void showBestScores(GameController controller) {
        String input = JOptionPane.showInputDialog(
                null,
                "Enter the board size to view the leaderboard:",
                "Leaderboard Search",
                JOptionPane.PLAIN_MESSAGE
        );

        try {
            int boardSize = Integer.parseInt(input.trim());
            List<LeaderboardEntry> entries = controller.getLeaderboardForSize(boardSize);

            if (entries.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "No games found for board size " + boardSize,
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                StringBuilder sb = new StringBuilder("Leaderboard for " + boardSize + " pairs:\n");
                for (LeaderboardEntry entry : entries) {
                    sb.append(entry).append("\n");
                }

                JOptionPane.showMessageDialog(
                        null,
                        sb.toString(),
                        "Leaderboard",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Invalid board size entered!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Updates the UI components with the current game state.
     *
     * <p>This method updates the title of the board view with the current
     * player's name, and updates the text of the total flips and matched
     * pairs labels with the current player's total flips and matched pairs
     * values.
     *
     * @param controller the game controller used to access the current player's state
     */
    public void updateUI(GameController controller) {
        setTitle("Matching Game Pairs: " + controller.getCurrentPlayer().getName());
        totalFlipsLabel.setText("Total Flips_" + controller.getCurrentPlayer().getName() + " :" + controller.getCurrentPlayer().getTotalFlips());
        matchedPairsLabel.setText("Matched Pairs_" + controller.getCurrentPlayer().getName() + " :" + controller.getCurrentPlayer().getMatchedPairs());
    }


}
