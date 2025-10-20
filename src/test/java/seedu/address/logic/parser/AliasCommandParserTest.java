package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AliasCommandParserTest {

    private AliasCommandParser parser = new AliasCommandParser();

    @Test
    public void parse_validArgs_returnsAliasCommand() {
        // Test with a simple alias
        String userInput = " la list";
        AliasCommand expectedCommand = new AliasCommand("la", "list");
        assertParseSuccess(parser, userInput, expectedCommand);

        // Test with a more complex command string
        String userInput2 = " findalex find n/Alex t/friend";
        AliasCommand expectedCommand2 = new AliasCommand("findalex", "find n/Alex t/friend");
        assertParseSuccess(parser, userInput2, expectedCommand2);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Test with no arguments
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AliasCommand.MESSAGE_USAGE));

        // Test with only one argument (alias name)
        assertParseFailure(parser, " la",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AliasCommand.MESSAGE_USAGE));

        // Test with empty alias name
        assertParseFailure(parser, "   list",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AliasCommand.MESSAGE_USAGE));

    }
}
