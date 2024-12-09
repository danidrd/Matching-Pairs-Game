package io.github.danidrd.matchingpairs.controller;

import io.github.danidrd.matchingpairs.view.BoardView;
import io.github.danidrd.matchingpairs.view.CardView;
import io.github.danidrd.matchingpairs.view.CardState;
import io.github.danidrd.matchingpairs.view.LeaderboardEntry;
import io.github.danidrd.matchingpairs.view.Player;


import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Controller responsibilities:
 * <ul>
 *     <li> Pairing and shuffling card values.</li>
 *     <li> Respond to card clicks, shuffle and exit actions. </li>
 *     <li> Track matched pairs and total flips. </li>
 * </ul>
 */
public class GameController implements ActionListener, PropertyChangeListener, VetoableChangeListener {
    private final Map<Integer, List<LeaderboardEntry>> leaderboard = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private int globalMatchedPairs = 0;
    private BoardView boardView;
    private int totalFlips = 0;
    private CardView firstSelectedCard = null;
    private boolean isTimerActive = false; // Flag to track timer activity
    private boolean bypassVeto = false;

    // Empty Constructor
    public GameController(List<String> playerNames) {
        for (String name : playerNames) {
            players.add(new Player(name));
        }
    }



    /**
     * Updates the leaderboard for a given board size with the current player's score.
     *
     * <p>This method adds the current player's score to the leaderboard for the given board size,
     * then sorts the leaderboard entries by the number of flips.
     *
     * @param boardSize the size of the board for which to update the leaderboard
     */
    public void updateLeaderboard(int boardSize, String winner) {
        List<LeaderboardEntry> entries = leaderboard.computeIfAbsent(boardSize, k -> new ArrayList<>());

        // Add the current player's score
        entries.add(new LeaderboardEntry(winner, totalFlips));

        // Sort by flips
        entries.sort(Comparator.comparingInt(LeaderboardEntry::getFlips));
    }

    /**
     * Retrieves the leaderboard entries for a specified board size.
     *
     * <p>This method returns a list of {@link LeaderboardEntry} objects
     * corresponding to the given board size. If no leaderboard entries
     * exist for the specified size, an empty list is returned.
     *
     * @param boardSize the size of the board for which to retrieve leaderboard entries
     * @return a list of leaderboard entries for the specified board size
     */
    public List<LeaderboardEntry> getLeaderboardForSize(int boardSize) {
        return leaderboard.getOrDefault(boardSize, Collections.emptyList());
    }



    /**
     * Handles vetoable changes to card state.
     *
     * <p>This method is triggered when a card's state property is about to change.
     * It checks if the transition from EXCLUDED or FACE_UP to FACE_DOWN is attempted,
     * and vetoes such state changes by throwing a {@link PropertyVetoException}.
     *
     * @param evt the vetoable change event containing the details of the state change
     * @throws PropertyVetoException if the state change is not allowed
     */
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if ( "state".equals(( evt.getPropertyName()))) {
            if(isBypassVeto()) {
                return; // Skip veto logic during shuffle or other bypass operations
            }
            CardState oldState = (CardState) evt.getOldValue();
            CardState newState = (CardState) evt.getNewValue();

            // Prevent changes during active timer
            if (isTimerActive()) {
                throw new PropertyVetoException("State change not allowed during timer", evt);
            }
            // Disallow state changes from excluded or face_up to face_down
            if( (oldState == CardState.EXCLUDED || oldState == CardState.FACE_UP) && newState == CardState.FACE_DOWN) {
                throw new PropertyVetoException("State transition not allowed", evt);
            }
        }
    }


    /**
     * Initializes the game controller.
     *
     * <p>This method is typically called right after the game controller is created.
     * It sets the board view, assigns listeners to all cards, the shuffle button,
     * and the exit button. It also shuffles the cards to start the game.
     *
     * @param boardView the board view to be associated with this game controller
     */
    public void initialize(BoardView boardView) {
        setBoardView(boardView);

        boardView.getShuffleButton().addActionListener(this);
        boardView.getExitButton().addActionListener(this);

        // Initialize game
        shuffleCards();
    }

    /**
     * @return true if a timer is currently active, false otherwise.
     * <p>
     * The timer is used to delay the flipping back of unmatched cards.
     */
    public boolean isTimerActive() {
        return isTimerActive;
    }

    /**
     * Handle action events from the view.
     * <p>
     * Responds to clicks on the shuffle button by shuffling the cards, and
     * clicks on the exit button by exiting the application.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == boardView.getShuffleButton() ) {
            shuffleCards();
        }else if ( e.getSource() == boardView.getExitButton() ) {
            // Display confirmation dialog
            int choice = JOptionPane.showConfirmDialog(
                    boardView,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE // Icon type (e.g. Warning)
            );
            if (choice == JOptionPane.YES_NO_OPTION) {
                System.exit(0);
            }
        }
    }


    /**
     * Responds to property changes in the view.
     * <p>
     * This method is called when a property in the view changes. If the property
     * is the "state" of a card, and the new state is FACE_UP, the card is
     * handled as if it were clicked. If the property is the "shuffle" property,
     * the game is reset and the cards are shuffled.
     *
     * @param evt the property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( "state".equals( evt.getPropertyName() ) ) {
            CardView card = (CardView) evt.getSource();
            CardState newState = (CardState) evt.getNewValue();

            if (newState == CardState.FACE_UP){
                handleCardFlip(card);
            }
        } else if("shuffle".equals(evt.getPropertyName())) {
            shuffleCards();
        }
    }

    /**
     * Responds to a card being flipped.
     * <p>
     * When a card is flipped, this method is called. If the card is flipped to
     * FACE_UP, the total number of flips is updated and the card is handled as
     * if it were clicked.
     * <p>
     * If there is no first selected card, this card is selected as the first
     * card. If there is a first selected card, it is compared to this card. If
     * they match, the matched pairs count is updated and both cards are marked
     * as excluded. If they don't match, the cards are flipped back after a
     * short delay.
     * <p>
     * If all pairs have been matched, a message is shown to the user.
     *
     * @param card the card that was flipped
     */
    private void handleCardFlip(CardView card) {
        Player currentPlayer = getCurrentPlayer();

        // Ignore clicks if the timer is active or the card is not in a flippable state
        if (isTimerActive || card.getState() != CardState.FACE_UP) {
            return;
        }

        currentPlayer.incrementTotalFlips();

        totalFlips++;
        boardView.getTotalFlipsLabel().setText("Total Flips_" + currentPlayer.getName() +": " + currentPlayer.getTotalFlips());

        // Flip the card to FACE_UP
        card.setState(CardState.FACE_UP);

        if (firstSelectedCard == null) {
            // This is the first card flipped
            firstSelectedCard = card;
        } else {
            // Compare with the first selected card
            if (firstSelectedCard.getValue() == card.getValue()) {
                // Matched pair
                incrementGlobalMatchedPairs();
                currentPlayer.incrementMatchedPairs();
                boardView.getMatchedPairsLabel().setText("Matched Pairs_" + currentPlayer.getName() + ": " + currentPlayer.getMatchedPairs());

                firstSelectedCard.setState(CardState.EXCLUDED);
                card.setState(CardState.EXCLUDED);

                // Verify consistency with multi-player
                verifyMatchingPairsConsistency();

                // Clear the selection
                firstSelectedCard = null;

                // Check for game completion
                checkGameCompletion();
            } else {
                // No match, flip both cards back after a short delay
                isTimerActive = true; // Timer starts, disable further interactions
                Timer timer = getTimer(card);
                timer.start();
            }
        }
    }

    /**
     * Creates a timer that, when triggered, will flip the given card and the
     * first selected card back to FACE_DOWN, clear the selection, switch to
     * the next player, and re-enable interactions.
     *
     * <p>
     * The timer is set to trigger after a 1 second delay. It is used to delay
     * the flipping back of unmatched cards.
     *
     * @param card the card to flip back
     * @return a timer that will flip the card back and reset the state
     */
    private Timer getTimer(CardView card) {
        Timer timer = new Timer(1000, evt -> {
            setBypassVeto(true);
            firstSelectedCard.setState(CardState.FACE_DOWN);
            card.setState(CardState.FACE_DOWN);
            setBypassVeto(false);

            // Clear the selection
            firstSelectedCard = null;
            nextPlayer();
            boardView.updateUI(this);
            isTimerActive = false; // Timer ends, re-enable interactions

        });
        timer.setRepeats(false);
        return timer;
    }


    /**
     * Shuffles the cards by assigning each card a value from 1 to the
     * number of pairs, duplicates the list and shuffles it, and then
     * assigns each card a value from the shuffled list and resets its
     * state to FACE_DOWN.
     */
    private void shuffleCards() {
        if (isTimerActive()) {
            JOptionPane.showMessageDialog(boardView, "Cannot shuffle while timer is active", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        globalMatchedPairs = 0;
        totalFlips = 0;
        for( Player player : players) {
            player.resetMatchedPairs();
        }
        setCurrentPlayerIndex(0);
        boardView.setTitle("Matching Pairs Game:" + getCurrentPlayer().getName());

        verifyMatchingPairsConsistency();
        boardView.getMatchedPairsLabel().setText("Matched Pairs_" + getCurrentPlayer().getName() + ": " + getCurrentPlayer().getMatchedPairs());
        boardView.getTotalFlipsLabel().setText("Total Flips_" + getCurrentPlayer().getName() + ": " + getCurrentPlayer().getTotalFlips());

        // Assign pairs
        int numberOfPairs = boardView.getCards().size() / 2;
        List<Integer> values = generateCardValues(numberOfPairs);


        // Temporarily bypass veto logic
        setBypassVeto(true);


        // Assign shuffled values to cards and reset state
        for( int i = 0; i < numberOfPairs * 2; i++ ) {
            CardView card = boardView.getCards().get(i);
            card.setValue(values.get(i));
            card.setState(CardState.FACE_DOWN);
        }

        setBypassVeto(false); // Re-enable veto logic
        firstSelectedCard = null; // Reset firstSelectedCard
        isTimerActive = false;  // Ensure the timer is not active
        boardView.revalidate();
        boardView.repaint();
    }

    /**
     * Sets the board view that this controller is associated with.
     * <p>
     * This method is typically called right after the game controller is created.
     * It sets the board view that the controller should interact with.
     * </p>
     *
     * @param boardView the board view to be associated with this game controller
     */
    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }

    /**
     * @return whether the controller should bypass veto logic for card state changes
     */
    public boolean isBypassVeto() {
        return bypassVeto;
    }

    /**
     * Sets whether the controller should bypass veto logic for card state changes.
     * <p>
     * When bypass veto is true, the controller will not check for veto exceptions
     * when changing the state of a card. This is useful for operations like shuffling
     * the cards, which should not be vetoed by the controller.
     * </p>
     *
     * @param bypassVeto whether the controller should bypass veto logic
     */
    public void setBypassVeto(boolean bypassVeto) {
        this.bypassVeto = bypassVeto;
    }

    /**
     * Generates a shuffled list of card values for the game.
     *
     * <p>This method creates a list containing pairs of integers from 1
     * to the specified number of pairs, shuffles the list to randomize
     * the order, and returns it. Each integer appears twice in the list,
     * representing a pair of matching card values.
     *
     * @param pairs the number of unique pairs of card values to generate
     * @return a shuffled list of card values with each value appearing twice
     */
    private List<Integer> generateCardValues(int pairs){
        List<Integer> values = new ArrayList<>();
        for(int i = 1; i <= pairs; i++){
            values.add(i);
            values.add(i);
        }

        Collections.shuffle(values);
        return values;
    }


    /**
     * Returns the player that is currently playing.
     *
     * @return the player that is currently playing
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Moves the game to the next player in the list.
     *
     * <p>This method cycles the current player index to the next player
     * in the list, using modulo arithmetic to wrap around to the start
     * of the list if the end is reached.
     */
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Returns the total number of matched pairs across all players.
     *
     * <p>This value is incremented whenever a player matches a pair of cards.
     *
     * @return the total number of matched pairs across all players
     */
    public int getGlobalMatchedPairs() {
        return globalMatchedPairs;
    }

    /**
     * Increments the total number of matched pairs across all players.
     *
     * <p>This value is incremented whenever a player matches a pair of cards.
     */
    public void incrementGlobalMatchedPairs() {
        globalMatchedPairs++;
    }

    /**
     * Returns the list of players in the game.
     *
     * <p>This list includes all the players that are currently playing
     * in the game, and is used to track their individual scores.
     *
     * @return the list of players in the game
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Checks whether the game is complete by comparing the number of matched pairs
     * to the total number of pairs in the game. If the game is complete, it displays
     * a congratulatory message box to the user and updates the leaderboard.
     */
    private void checkGameCompletion() {
        if (globalMatchedPairs == boardView.getCards().size() / 2) {
            endGame();
        }
    }

    /**
     * Verifies that the sum of the matched pairs of all players is equal to the total global matched pairs.
     *
     * <p>This method is used to ensure that the state of the game is consistent. If the sum of the matched
     * pairs of all players is not equal to the total global matched pairs, an {@link IllegalStateException}
     * is thrown.
     */
    private void verifyMatchingPairsConsistency() {
        int totalMatchedPairs = getPlayers().stream()
                .mapToInt(Player::getMatchedPairs)
                .sum();

        if ( totalMatchedPairs != getGlobalMatchedPairs()) {
            throw new IllegalStateException("Mismatch between global and individual matched pairs!");
        }
    }


/**
 * Ends the current game session by determining the winner, displaying a congratulatory message,
 * showing player rankings, and updating the leaderboard.
 *
 * <p>This method verifies the consistency of matched pairs, determines the winner based on the
 * number of matched pairs and total flips, and displays a message announcing the winner. It then
 * generates and displays the player rankings and updates the leaderboard with the winner's score
 * for the current board size.
 */
    private void endGame() {

        verifyMatchingPairsConsistency();

        Player winner = getPlayers().stream()
                .max(Comparator.comparingInt(Player::getMatchedPairs)
                        .thenComparingInt(Player::getTotalFlips))
                .orElseThrow();

        JOptionPane.showMessageDialog(
                boardView,
                "Game Over! The winner is " + winner.getName() +
                        " with " + winner.getMatchedPairs() + " matches and " +
                        winner.getTotalFlips() + " flips.",
                "Winner Announcement",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Generate and show player rankings
        showPlayerRankings();

        updateLeaderboard(boardView.getCards().size() / 2, winner.getName());
    }

    /**
     * Sets the index of the current player in the list of players.
     *
     * <p>This method is used to move the game to the next player in the list,
     * or to restart the game with the first player.
     *
     * @param currentPlayerIndex the index of the current player in the list
     *                            of players
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    /**
     * Shows the player rankings at the end of the game.
     *
     * <p>This method sorts the players by the number of matched pairs (in descending order)
     * and the total number of flips (in ascending order). The rankings are then displayed
     * in a message box.
     */
    private void showPlayerRankings() {
        // Sort players by matched pairs (descending) and total flips (ascending)
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Player::getMatchedPairs).reversed()
                        .thenComparingInt(Player::getTotalFlips))
                .toList();

        // Build the ranking string
        StringBuilder rankings = new StringBuilder("Player Rankings:\n");
        int rank = 1;
        for (Player player : sortedPlayers) {
            rankings.append(rank++).append(". ")
                    .append(player.getName())
                    .append(": ").append(player.getMatchedPairs()).append(" matched pairs, ")
                    .append(player.getTotalFlips()).append(" flips\n");
        }

        // Show the rankings in a dialog
        JOptionPane.showMessageDialog(
                boardView,
                rankings.toString(),
                "Player Rankings",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
