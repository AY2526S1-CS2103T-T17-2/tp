package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Removes one or all command aliases from the address book.
 */
public class UnaliasCommand extends Command {

    public static final String COMMAND_WORD = "unalias";
    public static final String ALL_FLAG = "--all";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the specified alias or all aliases.\n"
            + "Parameters: ALIAS_NAME OR " + ALL_FLAG + "\n"
            + "Example: " + COMMAND_WORD + " la\n"
            + "Example: " + COMMAND_WORD + " " + ALL_FLAG;

    public static final String MESSAGE_SUCCESS = "Alias removed: %1$s";
    public static final String MESSAGE_SUCCESS_ALL = "All aliases have been removed.";
    public static final String MESSAGE_ALIAS_NOT_FOUND = "Alias not found: %1$s";
    public static final String MESSAGE_NO_ALIASES_TO_CLEAR = "There are no aliases to clear.";


    private final String aliasToRemove;
    private final boolean isClearAll;

    /**
     * Creates an UnaliasCommand to remove a single specified alias.
     * The alias to remove cannot be the --all flag.
     */
    public UnaliasCommand(String aliasToRemove) {
        requireNonNull(aliasToRemove);
        this.aliasToRemove = aliasToRemove.trim();
        this.isClearAll = false;
    }

    /**
     * Creates an UnaliasCommand to remove all aliases.
     */
    private UnaliasCommand(boolean isClearAll) {
        this.aliasToRemove = ""; // Not used when clearing all
        this.isClearAll = isClearAll;
    }

    /**
     * Static factory method to create an UnaliasCommand for clearing all aliases.
     */
    public static UnaliasCommand createClearAllCommand() {
        return new UnaliasCommand(true);
    }


    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (isClearAll) {
            // Get all alias keys and remove them one by one.
            List<String> aliasKeys = new ArrayList<>(model.getCommandAliases().keySet());
            if (aliasKeys.isEmpty()) {
                throw new CommandException(MESSAGE_NO_ALIASES_TO_CLEAR);
            }
            for (String key : aliasKeys) {
                model.removeAlias(key);
            }
            return new CommandResult(MESSAGE_SUCCESS_ALL);
        }

        // Logic for removing a single alias.
        boolean removed = model.removeAlias(aliasToRemove);
        if (!removed) {
            throw new CommandException(String.format(MESSAGE_ALIAS_NOT_FOUND, aliasToRemove));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, aliasToRemove));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnaliasCommand)) {
            return false;
        }
        UnaliasCommand otherUnaliasCommand = (UnaliasCommand) other;
        return aliasToRemove.equals(otherUnaliasCommand.aliasToRemove)
                && isClearAll == otherUnaliasCommand.isClearAll;
    }
}
