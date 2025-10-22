package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opencsv.exceptions.CsvValidationException;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ImportCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;

// Simple in-memory stub of Model for testing
class ModelStub implements Model {

    final List<Person> persons = new ArrayList<>();

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {

    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return null;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return null;
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {

    }

    @Override
    public Path getAddressBookFilePath() {
        return null;
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {

    }

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {

    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return null;
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.contains(person);
    }

    @Override
    public void deletePerson(Person target) {

    }

    @Override
    public void addPerson(Person person) {
        persons.add(person);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {

    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return null;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {

    }

    // Stub out other Model methods if needed
}

class ImportCommandTest {

    @TempDir
    Path tempDir;
    private final ImportCommandParser parser = new ImportCommandParser();

    @Test
    void execute_validCsv_importsAllContacts() throws IOException, CsvValidationException, Exception {
        // Arrange: create a temporary CSV file with 3 contacts
        Path csvFile = tempDir.resolve("test.csv");
        String csvContent = String.join(System.lineSeparator(),
                "Name,Phone Number,Email,Address,Tags,Modules,Faculties,Favorites",
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,," ,
                "Bob Tan,91234567,bob@example.com,456 Clementi Rd,,,," ,
                "Chloe Lim,98765432,chloe@example.com,789 Pasir Ris Dr 2,,,,"
        );
        Files.writeString(csvFile, csvContent);

        ImportCommand command = new ImportCommand(csvFile);
        ModelStub model = new ModelStub();

        // Act
        CommandResult result = command.execute(model);

        // Assert
        assertEquals(3, model.persons.size());
        assertEquals("Imported 3 contact(s). Skipped 0 duplicate row(s).", result.getFeedbackToUser());
    }

    @Test
    void execute_duplicatesSkipped() throws IOException, CsvValidationException, Exception {
        Path csvFile = tempDir.resolve("test.csv");
        String csvContent = String.join(System.lineSeparator(),
                "Name,Phone Number,Email,Address,Tags,Modules,Faculties,Favorites",
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,," ,
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,,"
        );
        Files.writeString(csvFile, csvContent);

        ImportCommand command = new ImportCommand(csvFile);
        ModelStub model = new ModelStub();

        CommandResult result = command.execute(model);

        assertEquals(1, model.persons.size()); // only one added
        assertEquals("Imported 1 contact(s). Skipped 1 duplicate row(s).", result.getFeedbackToUser());
    }

    @Test
    void execute_invalidPath_throwsCommandException() {
        Path fakeFile = tempDir.resolve("nonexistent.csv");
        ImportCommand command = new ImportCommand(fakeFile);

        assertThrows(CommandException.class, () -> command.execute(new ModelStub()));
    }

    @Test
    public void parse_validFileName_resolvesToDownloadsDirectory() throws Exception {
        String fileName = "contacts.csv";
        String userHome = System.getProperty("user.home");
        Path expectedPath = Paths.get(userHome, "Downloads", fileName);

        // Stub file existence for test purposes — create a temp file
        Path tempFile = expectedPath;
        java.nio.file.Files.createDirectories(tempFile.getParent());
        java.nio.file.Files.createFile(tempFile);

        try {
            ImportCommand command = parser.parse(fileName);
            assertEquals(expectedPath, command.getPath());
        } finally {
            // Clean up the temporary file
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void parse_nonCsvFile_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("contacts.txt"));
    }
}
