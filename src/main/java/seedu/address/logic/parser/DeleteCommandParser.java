package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FacultyContainsKeywordsPredicate;
import seedu.address.model.person.ModuleContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagContainsKeywordsPredicate;


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
        if (!isAnyPrefixPresent(argMultimap, PREFIX_NAME, PREFIX_TAG, PREFIX_FACULTY, PREFIX_MODULE)
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

        List<Predicate<Person>> predicates = new ArrayList<>();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String nameKeywords = argMultimap.getValue(PREFIX_NAME).get();
            if (nameKeywords.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
            predicates.add(new NameContainsKeywordsPredicate(List.of(nameKeywords.split("\\s+"))));
        }
        if (argMultimap.getValue(PREFIX_TAG).isPresent()) {
            String tagKeywords = argMultimap.getValue(PREFIX_TAG).get();
            if (tagKeywords.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
            predicates.add(new TagContainsKeywordsPredicate(List.of(tagKeywords.split("\\s+"))));
        }
        if (argMultimap.getValue(PREFIX_FACULTY).isPresent()) {
            String facultyKeywords = argMultimap.getValue(PREFIX_FACULTY).get();
            if (facultyKeywords.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
            predicates.add(new FacultyContainsKeywordsPredicate(List.of(facultyKeywords.split("\\s+"))));
        }
        if (argMultimap.getValue(PREFIX_MODULE).isPresent()) {
            String moduleKeywords = argMultimap.getValue(PREFIX_MODULE).get();
            if (moduleKeywords.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
            predicates.add(new ModuleContainsKeywordsPredicate(List.of(moduleKeywords.split("\\s+"))));
        }

        if (predicates.isEmpty()) {
            // Should not happen if isAnyPrefixPresent check passed, but as a safeguard
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(predicates);
    }

    /**
     * Returns true if at least one of the prefixes contains a non-empty {@code Optional} value in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean isAnyPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
