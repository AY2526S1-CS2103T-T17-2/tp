package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE);

        if (!PredicateParserUtil.isAnyPrefixPresent(argMultimap, PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Throws ParseException if prefixes are repeated e.g. n/Alice n/Bob
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE);

        String usageMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        List<Predicate<Person>> predicates = PredicateParserUtil.parsePredicates(argMultimap, usageMessage);

        // Combine all predicates with an 'AND' logic
        Predicate<Person> combinedPredicate = predicates.stream().reduce(Predicate::and).orElse(x -> true);

        return new FindCommand(combinedPredicate);
    }
}
