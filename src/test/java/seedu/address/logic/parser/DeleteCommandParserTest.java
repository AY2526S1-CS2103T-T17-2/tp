package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

// Import Messages to get the specific error message for duplicate prefixes
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.TagContainsKeywordsPredicate;

public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();
    // Pre-format the usage message for repeated use in failure assertions
    private final String messageUsage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    @Test
    public void parse_validIndex_returnsDeleteCommand() {
        // Test parsing by index for backward compatibility (e.g., "delete 1")
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        // Test parsing an invalid index (non-integer)
        assertParseFailure(parser, "a", messageUsage);
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        // Test parsing empty arguments
        assertParseFailure(parser, "   ", messageUsage);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Test arguments that are neither a valid index nor contain prefixes
        assertParseFailure(parser, "Some random text", messageUsage);
    }

    @Test
    public void parse_validCriteria_returnsDeleteCommand() {
        // --- Tests for new batch delete functionality ---

        // Test parsing a single valid Name prefix
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice"));
        // The parser should create a DeleteCommand holding a *list* of predicates
        DeleteCommand expectedCommand = new DeleteCommand(List.of(namePredicate));
        assertParseSuccess(parser, " " + PREFIX_NAME + "Alice", expectedCommand);

        // Test parsing a single valid Tag prefix
        TagContainsKeywordsPredicate tagPredicate = new TagContainsKeywordsPredicate(Arrays.asList("friend"));
        expectedCommand = new DeleteCommand(List.of(tagPredicate));
        assertParseSuccess(parser, " " + PREFIX_TAG + "friend", expectedCommand);
    }

    @Test
    public void parse_validMultipleCriteria_returnsDeleteCommand() {
        // Test parsing multiple valid prefixes (e.g., "delete n/Alice t/friend")

        // 1. Define the expected predicates
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice"));
        TagContainsKeywordsPredicate tagPredicate = new TagContainsKeywordsPredicate(Arrays.asList("friend"));

        // 2. Create the expected command with a *list* of predicates.
        // The order in this list *must* match the order they are added inside the parser
        // (which is defined by the ArgumentTokenizer and the parser's if-blocks: n/, t/, f/, m/)
        DeleteCommand expectedCommand = new DeleteCommand(List.of(namePredicate, tagPredicate));

        // 3. Assert parsing success. This .equals() check compares the List<Predicate>,
        // which is why the command was modified to hold a list instead of a combined predicate.
        assertParseSuccess(parser, " " + PREFIX_NAME + "Alice " + PREFIX_TAG + "friend", expectedCommand);
    }

    @Test
    public void parse_invalidCriteria_throwsParseException() {
        // Test cases where a prefix is provided but the keyword is empty

        // Only "n/"
        assertParseFailure(parser, " " + PREFIX_NAME, messageUsage);
        // Only "t/"
        assertParseFailure(parser, " " + PREFIX_TAG, messageUsage);
        // Mix of valid and invalid
        assertParseFailure(parser, " " + PREFIX_NAME + "Alice " + PREFIX_TAG, messageUsage);
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        // Test for duplicate prefixes, which should be caught by ArgumentMultimap
        assertParseFailure(parser, " " + PREFIX_NAME + "Alice " + PREFIX_NAME + "Bob",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_preambleWithPrefix_throwsParseException() {
        // Test for preamble (text before prefixes) when prefixes are present.
        // This is disallowed for criteria-based deletion.
        // "1" here is treated as a preamble because it's not a standalone index.
        assertParseFailure(parser, "1 " + PREFIX_NAME + "Alice", messageUsage);
        // "abc" is also an invalid preamble.
        assertParseFailure(parser, "abc " + PREFIX_NAME + "Alice", messageUsage);
    }
}
