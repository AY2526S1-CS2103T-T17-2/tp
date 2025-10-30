package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FacultyContainsKeywordsPredicate;
import seedu.address.model.person.ModuleContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Contains utility methods used for parsing strings to create {@code Predicate} objects.
 */
public class PredicateParserUtil {

    /**
     * Parses the given {@code ArgumentMultimap} and creates a list of predicates.
     *
     * @param argMultimap The map of arguments to parse.
     * @param usageMessage The message to show if a keyword is empty.
     * @return A list of predicates based on the provided arguments.
     * @throws ParseException if any of the keywords for a prefix is empty.
     */
    public static List<Predicate<Person>> parsePredicates(ArgumentMultimap argMultimap, String usageMessage)
            throws ParseException {
        List<Predicate<Person>> predicates = new ArrayList<>();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String nameKeywords = argMultimap.getValue(PREFIX_NAME).get();
            if (nameKeywords.isEmpty()) {
                throw new ParseException(usageMessage);
            }
            predicates.add(new NameContainsKeywordsPredicate(List.of(nameKeywords.split("\\s+"))));
        }
        if (argMultimap.getValue(PREFIX_TAG).isPresent()) {
            String tagKeywords = argMultimap.getValue(PREFIX_TAG).get();
            if (tagKeywords.isEmpty()) {
                throw new ParseException(usageMessage);
            }
            predicates.add(new TagContainsKeywordsPredicate(List.of(tagKeywords.split("\\s+"))));
        }
        if (argMultimap.getValue(PREFIX_FACULTY).isPresent()) {
            String facultyKeywords = argMultimap.getValue(PREFIX_FACULTY).get();
            if (facultyKeywords.isEmpty()) {
                throw new ParseException(usageMessage);
            }
            predicates.add(new FacultyContainsKeywordsPredicate(List.of(facultyKeywords.split("\\s+"))));
        }
        if (argMultimap.getValue(PREFIX_MODULE).isPresent()) {
            String moduleKeywords = argMultimap.getValue(PREFIX_MODULE).get();
            if (moduleKeywords.isEmpty()) {
                throw new ParseException(usageMessage);
            }
            predicates.add(new ModuleContainsKeywordsPredicate(List.of(moduleKeywords.split("\\s+"))));
        }

        return predicates;
    }

    /**
     * Returns true if at least one of the prefixes is present in the given
     * {@code ArgumentMultimap}.
     */
    public static boolean isAnyPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
