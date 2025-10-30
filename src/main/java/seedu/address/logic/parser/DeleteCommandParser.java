package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;


/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE);

        // Check if at least one prefix is present and preamble is empty
        if (!PredicateParserUtil.isAnyPrefixPresent(argMultimap, PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE)
                || !argMultimap.getPreamble().isEmpty()) {

            // Try parsing as index for single delete backward compatibility
            try {
                Index index = ParserUtil.parseIndex(args.trim()); // trim() to handle potential surrounding spaces
                return new DeleteCommand(index);
            } catch (ParseException pe) {
                // Throw format error if neither criteria nor valid index is provided
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
            }
        }

        // Verify no duplicate prefixes if parsing criteria
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE);

        String usageMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        List<Predicate<Person>> predicates = PredicateParserUtil.parsePredicates(argMultimap, usageMessage);

        if (predicates.isEmpty()) {
            // Should not happen if isAnyPrefixPresent check passed, but as a safeguard
            throw new ParseException(usageMessage);
        }

        return new DeleteCommand(predicates);
    }
}
