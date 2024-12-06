package io.github.danidrd.matchingpairs.view;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.ArrayList;

/**
 * This class will be the main game windows (JFrame) that holds all the cards,
 * control buttons (like shuffle and exit) and info labels (like matched pairs and total flips)
 */
public class BoardView extends JFrame {
    private final List<CardView> cards = new ArrayList<>();
    private final JButton shuffleButton = new JButton("Shuffle");
    private final JButton exitButton = new JButton("Exit");
    private final JLabel matchedPairsLabel = new JLabel("Matched Pairs: 0");
    private final JLabel totalFlipsLabel = new JLabel("Total Flips: 0");

    /**
     * BoardView constructor
     * Initializes BoardView calling the JFrame constructor
     * sets the size of the window
     * sets the layout of the window
     * sets close operation
     * Initializes all cards and adds them to the cards panel
     * thus, adds the cards panel to the window
     * Create a control panel with the shuffle button and the exit button
     * and adds it to the window
     * Create info panel with the matched pairs label and the total flips label
     * and adds it to the window
     * Finally, sets the window visible
     * @param numberOfCards
     */
    public BoardView(int numberOfCards){
        super("Matching Pair Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize cards
        JPanel cardsPanel = new JPanel(new GridLayout(4, numberOfCards / 4, 10, 10)); // Adjust the number of rows as needed
        for(int i = 0; i < numberOfCards; i++) {
            CardView card = new CardView();
            cards.add(card);
            cardsPanel.add(card);
        }
        add(cardsPanel, BorderLayout.CENTER);

        // Initialize control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.add(shuffleButton);
        controlPanel.add(exitButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Initialize info labels
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(matchedPairsLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(totalFlipsLabel);
        add(infoPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    /**
     * @return the list of cards in this board view.
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

}
