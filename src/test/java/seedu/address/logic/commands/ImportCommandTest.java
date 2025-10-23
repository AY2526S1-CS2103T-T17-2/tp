package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opencsv.exceptions.CsvValidationException;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

class ImportCommandTest {

    @TempDir
    Path tempDir;

    private final Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    void execute_validCsv_importsAllContacts() throws IOException, CsvValidationException, Exception {
        // Arrange: create a temporary CSV file with 3 contacts
        Path csvFile = tempDir.resolve("test.csv");
        String csvContent = String.join(System.lineSeparator(),
                "Name,Phone Number,Email,Address,Tags,Modules,Faculties,Favorites",
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,,",
                "Bob Tan,91234567,bob@example.com,456 Clementi Rd,,,,",
                "Chloe Lim,98765432,chloe@example.com,789 Pasir Ris Dr 2,,,,"
        );
        Files.writeString(csvFile, csvContent);

        ImportCommand command = new ImportCommand(csvFile);

        // Act
        CommandResult result = command.execute(model);

        // Assert
        assertEquals(3, model.getAddressBook().getPersonList().size());
        assertEquals("Imported 3 contact(s). Skipped 0 duplicate row(s).", result.getFeedbackToUser());
    }

    @Test
    void execute_duplicatesSkipped() throws IOException, CsvValidationException, Exception {
        Path csvFile = tempDir.resolve("test.csv");
        String csvContent = String.join(System.lineSeparator(),
                "Name,Phone Number,Email,Address,Tags,Modules,Faculties,Favorites",
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,,",
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,,"
        );
        Files.writeString(csvFile, csvContent);

        ImportCommand command = new ImportCommand(csvFile);

        CommandResult result = command.execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size()); // only one added
        assertEquals("Imported 1 contact(s). Skipped 1 duplicate row(s).", result.getFeedbackToUser());
    }

    @Test
    void execute_invalidPath_throwsCommandException() {
        Path fakeFile = tempDir.resolve("nonexistent.csv");
        ImportCommand command = new ImportCommand(fakeFile);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

}
