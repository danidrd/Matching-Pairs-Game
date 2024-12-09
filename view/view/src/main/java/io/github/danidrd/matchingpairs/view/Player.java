package io.github.danidrd.matchingpairs.view;

public class Player {
    private final String name;
    private int totalFlips = 0;
    private int matchedPairs = 0;

    public Player(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the player.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the total number of flips made by this player.
     *
     * @return the number of flips made by this player
     */
    public int getTotalFlips() {
        return totalFlips;
    }

    /**
     * Increments the total number of flips made by the player by one.
     */
    public void incrementTotalFlips() {
        totalFlips++;
    }

    /**
     * Retrieves the number of matched pairs found by this player.
     *
     * @return the number of matched pairs
     */
    public int getMatchedPairs() {
        return matchedPairs;
    }

    /**
     * Increments the number of matched pairs found by this player by one.
     * <p>
     * This method is called when a player finds a matched pair.
     */
    public void incrementMatchedPairs() {
        matchedPairs++;
    }

    /**
     * Resets the player's matched pairs and total flips to zero.
     * <p>
     * This method is called when a game is reset.
     */
    public void resetMatchedPairs() {
        matchedPairs = 0;
        totalFlips = 0;
    }
}
