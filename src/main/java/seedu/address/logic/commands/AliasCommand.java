package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.model.Model;

/**
 * Adds a command alias to the address book.
 */
public class AliasCommand extends Command {

    public static final String COMMAND_WORD = "alias";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Defines an alias for a command. "
            + "Parameters: ALIAS_NAME COMMAND_STRING\n"
            + "Example 1: " + COMMAND_WORD + " la list\n"
            + "Example 2: " + COMMAND_WORD + " findfriend find t/friend";

    public static final String MESSAGE_SUCCESS = "Alias defined: %1$s -> %2$s";
    public static final String MESSAGE_ALIAS_OVERWRITTEN =
            "Warning: Existing alias '%1$s' (was: '%2$s') "
            + "is now overwritten to: '%3$s'";
    public static final String MESSAGE_ALIAS_IS_COMMAND = "Alias name '%1$s' is a built-in command and cannot be used.";
    public static final String MESSAGE_ALIAS_IS_EMPTY = "Alias name cannot be empty.";
    public static final String MESSAGE_COMMAND_IS_EMPTY = "Command string cannot be empty.";
    public static final String MESSAGE_CHAINED_ALIAS_NOT_ALLOWED =
            "Command '%1$s' is an existing alias. Chained aliases are not allowed.";
    public static final String MESSAGE_COMMAND_NOT_FOUND =
            "Command '%1$s' is not a valid built-in command.";


    // A static set of all built-in command words.
    private static final Set<String> BUILT_IN_COMMANDS = Stream.of(
            AddCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            SelectCommand.COMMAND_WORD,
            ExportCommand.COMMAND_WORD,
            ImportCommand.COMMAND_WORD,
            AliasCommand.COMMAND_WORD,
            UnaliasCommand.COMMAND_WORD,
            ListAliasesCommand.COMMAND_WORD
    ).collect(Collectors.toSet());

    private final String aliasName;
    private final String commandString;

    /**
     * Creates an AliasCommand to add the specified alias.
     */
    public AliasCommand(String aliasName, String commandString) {
        requireNonNull(aliasName);
        requireNonNull(commandString);

        if (aliasName.trim().isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_ALIAS_IS_EMPTY);
        }
        if (commandString.trim().isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_COMMAND_IS_EMPTY);
        }

        this.aliasName = aliasName.trim();
        this.commandString = commandString.trim(); // Store the full command string
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Map<String, String> existingAliases = model.getCommandAliases();
        String commandWord = commandString.split(" ")[0]; // Get the actual command word from the command string

        // Rule 1: Alias name cannot be a built-in command.
        if (BUILT_IN_COMMANDS.contains(aliasName)) {
            throw new CommandException(String.format(MESSAGE_ALIAS_IS_COMMAND, aliasName));
        }

        // Rule 2: The target command cannot be another alias (prevents chaining).
        if (existingAliases.containsKey(commandWord)) {
            throw new CommandException(String.format(MESSAGE_CHAINED_ALIAS_NOT_ALLOWED, commandWord));
        }

        // Rule 3: The target command must be a valid built-in command.
        if (!BUILT_IN_COMMANDS.contains(commandWord)) {
            throw new CommandException(String.format(MESSAGE_COMMAND_NOT_FOUND, commandWord));
        }

        // Check if alias already exists to provide a warning
        String feedbackMessage;
        if (existingAliases.containsKey(aliasName)) {
            String oldCommand = existingAliases.get(aliasName);
            feedbackMessage = String.format(MESSAGE_ALIAS_OVERWRITTEN, aliasName, oldCommand, commandString);
        } else {
            feedbackMessage = String.format(MESSAGE_SUCCESS, aliasName, commandString);
        }

        // Add/Overwrite the alias
        model.addAlias(aliasName, commandString);

        // Return the appropriate message
        return new CommandResult(feedbackMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AliasCommand)) {
            return false;
        }

        AliasCommand otherAliasCommand = (AliasCommand) other;
        return aliasName.equals(otherAliasCommand.aliasName)
                && commandString.equals(otherAliasCommand.commandString);
    }

    @Override
    public String toString() {
        return "AliasCommand{"
                + "aliasName='" + aliasName + '\''
                + ", commandString='" + commandString + '\''
                + '}';
    }
}
