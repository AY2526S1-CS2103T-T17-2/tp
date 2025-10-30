package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.model.Model;

/**
 * Lists all defined command aliases.
 */
public class ListAliasesCommand extends Command {

    public static final String COMMAND_WORD = "listaliases";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all defined command aliases.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Defined aliases:\n%1$s";
    public static final String MESSAGE_NO_ALIASES = "No aliases defined.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Map<String, String> aliases = model.getCommandAliases();

        if (aliases.isEmpty()) {
            return new CommandResult(MESSAGE_NO_ALIASES);
        }

        String formattedAliases = aliases.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue())
                .collect(Collectors.joining("\n"));

        return new CommandResult(String.format(MESSAGE_SUCCESS, formattedAliases));
    }

    @Override
    public boolean equals(Object other) {
        // This command has no state, so any two instances are equal
        return other instanceof ListAliasesCommand;
    }
}
