package io.github.danidrd.matchingpairs.controller;

import io.github.danidrd.matchingpairs.view.BoardView;
import io.github.danidrd.matchingpairs.view.CardView;
import io.github.danidrd.matchingpairs.view.CardState;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

/**
 * Controller responsibilities:
 * <ul>
 *     <li> Pairing and shuffling card values.</li>
 *     <li> Respond to card clicks, shuffle and exit actions. </li>
 *     <li> Track matched pairs and total flips. </li>
 * </ul>
 */
public class GameController implements ActionListener, PropertyChangeListener, VetoableChangeListener {
    private BoardView boardView;
    private int matchedPairs = 0;
    private int totalFlips = 0;
    private CardView firstSelectedCard = null;
    private boolean isTimerActive = false; // Flag to track timer activity
    private boolean bypassVeto = false;

    // Empty Constructor
    public GameController() {}

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
            CardView card = (CardView) evt.getSource();
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
            matchedPairs = 0;
            totalFlips = 0;
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
        // Ignore clicks if the timer is active or the card is not in a flippable state
        if (isTimerActive || card.getState() != CardState.FACE_UP) {
            return;
        }

        totalFlips++;
        boardView.getTotalFlipsLabel().setText("Total Flips: " + totalFlips);

        // Flip the card to FACE_UP
        card.setState(CardState.FACE_UP);

        if (firstSelectedCard == null) {
            // This is the first card flipped
            firstSelectedCard = card;
        } else {
            // Compare with the first selected card
            if (firstSelectedCard.getValue() == card.getValue()) {
                // Matched pair
                matchedPairs++;
                boardView.getMatchedPairsLabel().setText("Matched Pairs: " + matchedPairs);

                firstSelectedCard.setState(CardState.EXCLUDED);
                card.setState(CardState.EXCLUDED);

                // Clear the selection
                firstSelectedCard = null;

                // Check for game completion
                if (matchedPairs == boardView.getCards().size() / 2) {
                    JOptionPane.showMessageDialog(boardView, "Congratulations! You've matched all pairs.");
                }
            } else {
                // No match, flip both cards back after a short delay
                isTimerActive = true; // Timer starts, disable further interactions
                Timer timer = new Timer(1000, evt -> {
                    setBypassVeto(true);
                    firstSelectedCard.setState(CardState.FACE_DOWN);
                    card.setState(CardState.FACE_DOWN);
                    setBypassVeto(false);

                    // Clear the selection
                    firstSelectedCard = null;

                    isTimerActive = false; // Timer ends, re-enable interactions
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }


    /**
     * Shuffles the cards by assigning each card a value from 1 to the
     * number of pairs, duplicates the list and shuffles it, and then
     * assigns each card a value from the shuffled list and resets its
     * state to FACE_DOWN.
     */
    private void shuffleCards() {
        matchedPairs = 0;
        totalFlips = 0;
        boardView.getMatchedPairsLabel().setText("Matched Pairs: " + matchedPairs);
        boardView.getTotalFlipsLabel().setText("Total Flips: " + totalFlips);

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
}
