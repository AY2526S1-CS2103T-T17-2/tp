package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.ListAliasesCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.UnaliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and arguments.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    private final AliasProvider aliasProvider;

    /**
     * Constructs an {@code AddressBookParser} that uses the given {@code AliasProvider}
     * to resolve aliases.
     *
     * @param aliasProvider The component to get alias definitions from.
     */
    public AddressBookParser(AliasProvider aliasProvider) {
        this.aliasProvider = aliasProvider;
    }


    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        String commandWord = matcher.group("commandWord");
        String arguments = matcher.group("arguments");

        final Map<String, String> aliases = aliasProvider.getCommandAliases();

        if (aliases.containsKey(commandWord)) {
            final String aliasedCommand = aliases.get(commandWord);
            final String newCommandText = (aliasedCommand + " " + arguments).trim();

            logger.fine("Alias detected. Original: '" + userInput
                            + "', Substituted: '" + newCommandText + "'");

            final Matcher newMatcher = BASIC_COMMAND_FORMAT.matcher(newCommandText);

            if (!newMatcher.matches()) {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }

            commandWord = newMatcher.group("commandWord");
            arguments = newMatcher.group("arguments");
        }


        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            if (!arguments.trim().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
            }
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            if (!arguments.trim().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
            }
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            if (!arguments.trim().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE));
            }
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            if (!arguments.trim().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
            }
            return new HelpCommand();

        case SelectCommand.COMMAND_WORD:
            return new SelectCommandParser().parse(arguments);

        case ExportCommand.COMMAND_WORD:
            if (!arguments.trim().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
            }
            return new ExportCommand();

        case ImportCommand.COMMAND_WORD:
            return new ImportCommandParser().parse(arguments);

        case AliasCommand.COMMAND_WORD:
            return new AliasCommandParser().parse(arguments);

        case UnaliasCommand.COMMAND_WORD:
            return new UnaliasCommandParser().parse(arguments);

        case ListAliasesCommand.COMMAND_WORD:
            if (!arguments.trim().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListAliasesCommand.MESSAGE_USAGE));
            }
            return new ListAliasesCommand();

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
