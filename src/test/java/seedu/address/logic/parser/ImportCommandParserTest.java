package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;

class ImportCommandParserTest {

    @TempDir
    Path tempDir;

    private final ImportCommandParser parser = new ImportCommandParser();

    @Test
    void parse_validUnquotedPath_doesNotThrow() throws IOException {
        // Arrange: create a temporary CSV file
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "Name,Phone,Email,Address\nAlice,91234567,alice@example.com,123 Road");

        // Act & Assert: should not throw
        assertDoesNotThrow(() -> parser.parse(csvFile.toString()));
    }

    @Test
    void parse_validQuotedPath_doesNotThrow() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "Name,Phone,Email,Address\nBob,98765432,bob@example.com,456 Road");

        String quotedPath = "\"" + csvFile.toString() + "\"";

        assertDoesNotThrow(() -> parser.parse(quotedPath));
    }

    @Test
    void parse_nonExistentFile_throwsParseException() {
        Path fakeFile = tempDir.resolve("nonexistent.csv");

        assertThrows(ParseException.class, () -> parser.parse(fakeFile.toString()));
    }

    @Test
    void parse_wrongExtension_throwsParseException() throws IOException {
        Path txtFile = tempDir.resolve("test.txt");
        Files.writeString(txtFile, "dummy content");

        assertThrows(ParseException.class, () -> parser.parse(txtFile.toString()));
    }

    @Test
    void parse_invalidPath_throwsParseException() {
        // Invalid characters in file path
        String invalidPath = "C:/invalid|path/test.csv";

        assertThrows(ParseException.class, () -> parser.parse(invalidPath));
    }

    @Test
    void parse_emptyArgument_usesDefaultPathOrThrows() {
        assertDoesNotThrow(() -> parser.parse(""));

        assertDoesNotThrow(() -> parser.parse("   "));
    }

}

