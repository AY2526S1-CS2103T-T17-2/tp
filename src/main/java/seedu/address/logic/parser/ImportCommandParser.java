package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;


/**
 * Parses input arguments and creates a new ImportCommand object
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    @Override
    public ImportCommand parse(String userInput) throws ParseException {
        requireNonNull(userInput);

        userInput = userInput.trim();
        if ((userInput.startsWith("\"") && userInput.endsWith("\""))
                || (userInput.startsWith("'") && userInput.endsWith("'"))) {
            userInput = userInput.substring(1, userInput.length() - 1);
        }

        try {
            Path path = Paths.get(userInput);

            if (!path.isAbsolute()) {
                String userHome = System.getProperty("user.home");
                path = Paths.get(userHome, "Downloads", userInput);
            }
            if (!userInput.endsWith(".csv") || !Files.exists(path) || !Files.isRegularFile(path)) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE)
                );
            }
            return new ImportCommand(path);
        } catch (InvalidPathException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE), e);
        }
    }
}
