package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents the command history of the application.
 * Stores up to 10 recent commands and provides navigation through the history.
 */
public class CommandHistory {
    private static final int MAX_HISTORY_SIZE = 10;

    private final List<String> history;
    private int currentPosition;

    /**
     * Creates a CommandHistory with an empty history.
     */
    public CommandHistory() {
        this.history = new ArrayList<>();
        this.currentPosition = 0;
    }

    /**
     * Creates a CommandHistory initialized with the given history.
     * If the history exceeds the maximum size, only the most recent commands are kept.
     *
     * @param history The initial command history.
     */
    public CommandHistory(List<String> history) {
        requireNonNull(history);
        this.history = new ArrayList<>(history);

        // Trim to max size if needed, keeping the most recent commands
        if (this.history.size() > MAX_HISTORY_SIZE) {
            this.history.subList(0, this.history.size() - MAX_HISTORY_SIZE).clear();
        }

        this.currentPosition = this.history.size();
    }

    /**
     * Adds a command to the history.
     * Skips adding if the command is empty, whitespace-only, or the same as the most recent command.
     * Removes the oldest command if the history exceeds the maximum size.
     * Resets the navigation position to the end of the history.
     *
     * @param command The command to add.
     */
    public void addCommand(String command) {
        requireNonNull(command);

        // Skip if command is empty or whitespace-only
        if (command.trim().isEmpty()) {
            return;
        }

        // Skip if command is the same as the most recent command
        if (!history.isEmpty() && history.get(history.size() - 1).equals(command)) {
            currentPosition = history.size();
            return;
        }

        history.add(command);

        // Remove oldest command if size exceeds limit
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }

        // Reset position to end
        currentPosition = history.size();
    }

    /**
     * Returns the previous command in the history.
     * Moves the navigation position backwards.
     *
     * @return An Optional containing the previous command, or empty if at the start.
     */
    public Optional<String> getPreviousCommand() {
        if (currentPosition <= 0) {
            return Optional.empty();
        }

        currentPosition--;
        return Optional.of(history.get(currentPosition));
    }

    /**
     * Returns the next command in the history.
     * Moves the navigation position forwards.
     *
     * @return An Optional containing the next command, or empty if at the end.
     */
    public Optional<String> getNextCommand() {
        if (currentPosition >= history.size() - 1) {
            currentPosition = history.size();
            return Optional.empty();
        }

        currentPosition++;
        return Optional.of(history.get(currentPosition));
    }

    /**
     * Returns an unmodifiable view of the command history.
     *
     * @return An unmodifiable list of commands.
     */
    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }
}

