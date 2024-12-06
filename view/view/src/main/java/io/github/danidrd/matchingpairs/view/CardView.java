package io.github.danidrd.matchingpairs.view;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Each card in the game will be a JButton that can be flipped to reveal its value.
 * It will handle its own state, and notify its listeners when its state changes.
 */
public class CardView extends JButton {
    private int value;
    private CardState state;

    // Final property change support used to notify listeners
    private PropertyChangeSupport pcs;

    /**
     * CardView Constructor
     * Initializes the CardView calling super()
     * sets the state of the card to FACE_DOWN
     * Add an action listener to flip the card
     */
    public CardView() {
        super();
        setState(CardState.FACE_DOWN);
        setFont(new Font("Arial", Font.BOLD, 24));
        addActionListener(e -> flipCard());
    }

    /**
     * @return the value of the card
     */
    public int getValue(){
        return value;
    }

    /**
     * Set the value of this card.
     *
     * @param value the value of the card
     */
    public void setValue(int value){
        this.value = value;
    }

    /**
     * Get the current state of the card.
     *
     * @return the card state
     */
    public CardState getState() {
        return state;
    }

    /**
     * Sets the state of this card.
     *
     * <p>Whenever the state of this card is changed, its appearance is updated and
     * a {@link PropertyChangeEvent} is fired to all registered listeners.
     *
     * @param newState the new state of this card
     */
    public void setState(CardState newState) {
        CardState oldState = this.state;
        this.state = newState;
        updateAppearance();
        getPropertyChangeSupport().firePropertyChange("state", oldState, newState);
    }

    /**
     * Flips the card, changing its state from FACE_DOWN to FACE_UP.
     */
    private void flipCard() {
        if (state == CardState.FACE_DOWN) {
            setState(CardState.FACE_UP);
        }
    }

    /**
     * Updates the appearance of the card based on its current state.
     *
     * <ul>
     * <li>If the state is FACE_DOWN, the background color is set to light gray
     * and the text is set to an empty string.</li>
     * <li>If the state is FACE_UP, the background color is set to white, and
     * the text is set to the value of the card.</li>
     * <li>If the state is EXCLUDED, the background color is set to red, the
     * component is disabled, and the text is set to an empty string.</li>
     * </ul>
     */
    private void updateAppearance(){
        switch (state) {
            case FACE_DOWN:
                setBackground(Color.LIGHT_GRAY);
                setText("");
                setEnabled(true);
                break;
            case FACE_UP:
                setBackground(Color.WHITE);
                setText(String.valueOf(value));
                break;
            case EXCLUDED:
                setBackground(Color.RED);
                setEnabled(false);
                setText("");
                break;
        }
    }


    /**
     * Registers a listener to receive property change events from this
     * {@link CardView}.
     *
     * @param listener the listener to be registered
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    /**
     * Deregisters a listener from receiving property change events from
     * this {@link CardView}.
     *
     * @param listener the listener to be deregistered
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }

    private PropertyChangeSupport getPropertyChangeSupport(){
        if(pcs == null){
            pcs = new PropertyChangeSupport(this);
        }
        return pcs;
    }
}
