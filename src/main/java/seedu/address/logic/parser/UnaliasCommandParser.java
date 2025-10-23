package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.UnaliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new UnaliasCommand object.
 */
public class UnaliasCommandParser implements Parser<UnaliasCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UnaliasCommand
     * and returns an UnaliasCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public UnaliasCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnaliasCommand.MESSAGE_USAGE));
        }

        // If the argument is the special flag, create the "clear all" version of the command.
        if (trimmedArgs.equals(UnaliasCommand.ALL_FLAG)) {
            return UnaliasCommand.createClearAllCommand();
        }

        // Otherwise, create the command to remove a single, specified alias.
        return new UnaliasCommand(trimmedArgs);
    }
}
