package io.github.danidrd.matchingpairs.controller;

import io.github.danidrd.matchingpairs.view.BoardView;
import io.github.danidrd.matchingpairs.view.CardView;
import io.github.danidrd.matchingpairs.view.CardState;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
public class GameController implements ActionListener, PropertyChangeListener {
    private final BoardView boardView;
    private final List<CardView> cards;

    private int matchedPairs = 0;
    private int totalFlips = 0;
    private CardView firstSelectedCard = null;

    public GameController(BoardView boardView) {
        this.boardView = boardView;
        this.cards = boardView.getCards();

        // Assign listeners
        for ( CardView card : cards ) {
            card.addPropertyChangeListener(this);
            card.addActionListener(this);
        }

        boardView.getShuffleButton().addActionListener(this);
        boardView.getExitButton().addActionListener(this);

        // Initialize game
        shuffleCards();
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
            System.exit(0);
        }
    }

    /**
     * Responds to property changes in the cards.
     * <p>
     * Specifically, when a card's state property changes, this method is
     * called. If the state is changed to FACE_UP, the total number of flips is
     * updated and the card is handled as if it were clicked.
     *
     * @param evt the property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( "state".equals( evt.getPropertyName() ) ) {
            CardView card = (CardView) evt.getSource();
            handleCardFlip(card);
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
        if ( card.getState() == CardState.FACE_UP ) {
            totalFlips++;
            boardView.getTotalFlipsLabel().setText("Total Flips: " + totalFlips);

            if ( firstSelectedCard == null ) {
                firstSelectedCard = card;
            } else {
                if ( firstSelectedCard.getValue() == card.getValue() ) {
                    matchedPairs++;
                    boardView.getMatchedPairsLabel().setText("Matched Pairs: " + matchedPairs);
                    firstSelectedCard.setState(CardState.EXCLUDED);
                    card.setState(CardState.EXCLUDED);
                    firstSelectedCard = null;

                    // Check for game completion
                    if( matchedPairs == cards.size() / 2 ) {
                        JOptionPane.showMessageDialog(boardView, "Congratulations! You've matched all pairs.");
                    }
                } else {
                    // No match, flip back after a short delay
                    Timer timer = new Timer(500, evt -> {
                        firstSelectedCard.setState(CardState.FACE_DOWN);
                        card.setState(CardState.FACE_DOWN);
                        firstSelectedCard = null;
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
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
        int numberOfPairs = cards.size() / 2;
        List<Integer> values = new ArrayList<>();
        for( int i = 1; i <= numberOfPairs; i++ ) {
            values.add(i);
            values.add(i);
        }

        // Shuffle values
        Collections.shuffle(values);

        // Assign shuffled values to cards and reset state
        for( int i = 0; i < cards.size(); i++ ) {
            CardView card = cards.get(i);
            card.setValue(values.get(i));
            card.setState(CardState.FACE_DOWN);
        }
    }

}
