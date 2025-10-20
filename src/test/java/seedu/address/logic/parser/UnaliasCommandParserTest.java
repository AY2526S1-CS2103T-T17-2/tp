// package seedu.address.logic.parser;

// import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
// import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
// import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

// import org.junit.jupiter.api.Test;

// import seedu.address.logic.commands.UnaliasCommand;

// public class UnaliasCommandParserTest {

//     private UnaliasCommandParser parser = new UnaliasCommandParser();

//     @Test
//     public void parse_validArgs_returnsUnaliasCommand() {
//         String userInput = " la";
//         UnaliasCommand expectedCommand = new UnaliasCommand("la");
//         assertParseSuccess(parser, userInput, expectedCommand);
//     }

//     @Test
//     public void parse_invalidArgs_throwsParseException() {
//         // Test with no arguments
//         assertParseFailure(parser, " ",
//                 String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnaliasCommand.MESSAGE_USAGE));

//         // Test with empty alias name
//         assertParseFailure(parser, "   ",
//                 String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnaliasCommand.MESSAGE_USAGE));
//     }
// }
