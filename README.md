# AP-24 – Programming Assignment #1

**Date:** November 25, 2024

## Assignment Overview

This programming assignment consists of two exercises on Java Beans and Java Reflection and Annotations, respectively.

---

## Exercise 1 (Java Beans) – The Matching Pairs Game

The **Matching Pair game** (aka *Concentration*) is a game where some cards are laid face down on a surface and two cards are flipped face up over each turn. The goal of the game is to turn over pairs of matching cards. When a matching pair is found, it is eliminated from the game, and when all cards are eliminated, the game terminates. More information about the game can be found [here](https://en.wikipedia.org/wiki/Concentration_(card_game)).

### Goal of the Assignment
The objective is to implement a board that allows one player to play the game. Optionally, support for more players can be provided. The board and the other components of the game must be realized as Java beans, and they have to interact among themselves according to the **Publish-Subscribe** (or **Observer**) design pattern, using events and event listeners as much as possible.

### Functional Requirements

- **Game Start:**  
  The game starts showing the `Board` (a bean which extends `JFrame`), on which eight `Card`s are displayed face down, together with a **Shuffle** and an **Exit** button, a **Controller** label, and a **Counter** label.

- **Card Properties:**  
  Each `Card` is a bean extending `JButton` and has:
    - A property `value`.
    - A property `state`, which is both **bound** and **constrained**. The state can have three possible values:
        - `excluded` (the card is removed from the game).
        - `face_down` (the card's value is hidden, shown as green).
        - `face_up` (the card's value is visible).

- **Game Logic:**
    - At the beginning of the game and whenever the **Shuffle** button is clicked, the `Board` initializes the `Card` values using a random sequence of length 8, made of 4 identical pairs.
    - The `Board` fires a **shuffle** event, wrapping the new sequence of values. Each `Card` must register as a listener to this event to update its value.
    - Clicking a `Card` changes its `state`:
        - From `face_down` to `face_up`.
        - From `excluded` or `face_up` to `face_down` (vetoed by the `Controller`).

- **Controller Logic:**
    - The **Controller** label tracks the number of matched pairs (`0` initially).
    - When a first card is flipped `face_up`, its value `v1` is stored.  
      When a second card is flipped `face_up`, its value `v2` is stored:
        - If `v1 == v2`:
            - The `Controller` fires a **matched** event with `true`.
            - Both cards are marked as `excluded`, and the counter is incremented.
        - If `v1 != v2`:
            - The `Controller` fires a **matched** event with `false`.
            - The second card remains visible for 1 second before returning to `face_down`.

- **Counter:**  
  Displays the total number of times a card has been turned `face_up`. Resets to `0` when the **shuffle** event is fired.

- **Exit Button:**  
  Prompts the user for confirmation. If confirmed, the game ends; otherwise, nothing happens.

### Additional Details
- At startup, the `Board` creates all beans and registers them as listeners to events.
- All beans must use the **Observer Design Pattern** for interactions.
- After all cards become `excluded`, no more state changes are possible, and only the **Shuffle** and **Exit** buttons remain active.

---

## Optional Extensions

1. **Parametric Card Count:**
    - Define a constant `N` in the `Board` (default is `4`) and use `2 x N` as the number of cards.
    - Modifying `N` requires recompiling only the `Board` to adapt the game.

2. **Challenge Label:**
    - Add a `Challenge` label to display the best score since the start of the game.
    - The score is defined as the smallest number of moves needed to complete a round.

---

## Solution Format

- Submit suitably commented source files for all beans.
- Provide one `.jar` archive for each bean.
