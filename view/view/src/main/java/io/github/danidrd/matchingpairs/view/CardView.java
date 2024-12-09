package io.github.danidrd.matchingpairs.view;


import javax.swing.*;
import java.awt.*;
import java.beans.*;

/**
 * Each card in the game will be a JButton that can be flipped to reveal its value.
 * It will handle its own state, and notify its listeners when its state changes.
 */
public class CardView extends JButton {
    private int value;
    private CardState state;

    // Final property change support used to notify listeners
    private PropertyChangeSupport pcs;
    private VetoableChangeSupport vcs;

    /**
     * CardView Constructor
     * Initializes the CardView calling super(),
     * initialize the controller reference,
     * for handling responsiveness of cards,
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
     * Sets the state of the card and updates its appearance.
     *
     * <p>This method changes the card's state to the specified new state,
     * updates its appearance accordingly, and fires a property change event
     * to notify listeners of the state change. If the controller is active
     * and the new state is FACE_UP, the state change is ignored to prevent
     * interaction during active timing.
     *
     * @param newState the new state to be set for the card
     */
    public void setState(CardState newState) {

        try {

            getVetoableChangeSupport().fireVetoableChange("state", this.state, newState);

            CardState oldState = this.state;
            this.state = newState;
            updateAppearance();
            getPropertyChangeSupport().firePropertyChange("state", oldState, newState);
        } catch (PropertyVetoException e) {
            System.out.println("State change vetoed: " + e.getMessage());
        }

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
                setBackground(Color.GREEN);
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
     * Adds a listener to the list of listeners that are notified when
     * this card's state is about to change. The listener is given the
     * opportunity to veto the change by throwing a
     * {@link PropertyVetoException}.
     *
     * @param listener the listener to be added
     */
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        getVetoableChangeSupport().addVetoableChangeListener(listener);
    }

    /**
     * Removes a listener from the list of listeners that are notified when
     * this card's state changes.
     *
     * @param listener the listener to be removed
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        getVetoableChangeSupport().removeVetoableChangeListener(listener);
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

    /**
     * Returns the {@link PropertyChangeSupport} object responsible for
     * managing listeners for bound property changes on this
     * {@link CardView}.
     *
     * <p>This method lazily initializes the
     * {@link PropertyChangeSupport} instance if it has not been
     * initialized yet.
     *
     * @return the property change support
     */
    private PropertyChangeSupport getPropertyChangeSupport(){
        if(pcs == null){
            pcs = new PropertyChangeSupport(this);
        }
        return pcs;
    }

    /**
     * Provides access to the vetoable change support for this card.
     *
     * <p>This method initializes the {@link VetoableChangeSupport} instance
     * if it is not already initialized and returns it. The vetoable change
     * support is used to manage listeners that can veto changes to bound
     * properties of this card.
     *
     * @return the vetoable change support associated with this card
     */
    private VetoableChangeSupport getVetoableChangeSupport(){
        if(vcs == null){
            vcs = new VetoableChangeSupport(this);
        }
        return vcs;
    }
}
