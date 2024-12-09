package io.github.danidrd.matchingpairs.view;

public class LeaderboardEntry {
    private final String playerName;
    private final int flips;

    public LeaderboardEntry(String playerName, int flips) {
        this.playerName = playerName;
        this.flips = flips;
    }

    /**
     * @return the number of flips required to complete the game
     */
    public int getFlips() {
        return flips;
    }

    /**
     * @return a string representation of the leaderboard entry in the format
     *         'playerName: flips '
     */
    @Override
    public String toString() {
        return playerName + ": " + flips + " flips";
    }
}
