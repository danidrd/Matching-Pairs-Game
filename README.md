# Matching Pairs Game
First exercise of the assignment of Advanced Programming (MSc in Computer Science and Networking at UniPisa)
    
    https://pages.di.unipi.it/corradini/Didattica/AP-24/PROG-ASS/01/2024-Assignment-1-v1.pdf
## Overview
The Matching Pairs game is a turn-based card-matching game implemented in Java. This implementation focuses on modularity, scalability, and maintainability by following the **Model-View-Controller (MVC)** architecture, ensuring a clean separation of responsibilities. The project also adheres to **high decoupling** and the **Observer pattern**, enabling robust communication between components.

---

## Features

### Single-Player Mode
- Play a classic matching pairs game with a dynamic board size.
- Track your total flips and matched pairs during gameplay.

### Multiplayer Mode
- Supports up to 4 players with a turn-based system.
- Each player has individual scores (`matchedPairs` and `totalFlips`).
- The current player’s progress is dynamically displayed in the UI.
- The global game state ensures consistency among all players.

### Leaderboard
- Tracks the best scores across different board sizes.
- Players can view rankings for specific configurations and compete for the top spot.

### Dynamic Board Resizing
- Change the number of pairs dynamically during the game.
- A button allows manual input of the new board size, which adjusts the layout and updates the game state seamlessly.

---

## Design Principles

### Model-View-Controller (MVC) Architecture
- **Model**: `GameController` manages game logic, such as player turns, scoring, and the overall game state.
- **View**: `BoardView` and `CardView` handle user interface elements, displaying the game board and cards.
- **Controller**: Bridges the user input, game logic, and view, ensuring synchronization.

This architecture ensures modularity, making it easy to adapt or extend the game logic or UI independently.

### High Decoupling
- Instead of having each `CardView` directly respond to the `shuffle` event, the `GameController` centrally manages this logic.
- The `GameController` listens to the `BoardView`'s `shuffle` event and updates the `CardView` instances.
- This design reduces direct dependencies between components, enhancing maintainability and scalability.

### Observer Pattern
The implementation makes extensive use of the Observer pattern:
- The `BoardView` emits a `shuffle` event, which is handled by the `GameController` to update all `CardView` instances.
- Each `CardView` observes changes to its `state` property:
    - The `state` is **vetoable**, ensuring valid state transitions, and **bound**, notifying the `GameController` when flips occur.
    - The `GameController` processes the `state` event and invokes the `handleCardFlip` method to manage the game logic.
- Button actions are also handled centrally by the `GameController`, maintaining consistency and proper sequencing of events.

---

## How It Works

1. **Game Initialization**:
    - Players are prompted to choose single-player or multiplayer mode and specify the number of players.
    - Players then enter their usernames.

2. **Gameplay**:
    - Players take turns flipping cards to find matching pairs.
    - If a match is found, the player continues; otherwise, the next player takes their turn.

3. **Game End**:
    - The game ends when all pairs are matched.
    - The player with the most matches (and fewest flips in case of a tie) is declared the winner.
    - A detailed ranking of all players is displayed.

4. **Leaderboard**:
    - At the end of a game, scores are stored in a leaderboard that can be viewed based on the board size.

---

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/danidrd/matching-pairs-game.git

2. Use Maven from an IDE(e.g. IntelliJ, Apache NetBeans) for install and packaging the project.


Otherwise, .jar files will be uploaded soon.
