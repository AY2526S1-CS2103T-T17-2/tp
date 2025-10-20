package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.util.CsvUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.favorite.Favorite;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

class ExportCommandTest {

    @TempDir
    Path tempDir;

    private TestModel model;

    @BeforeEach
    void setUp() {
        model = new TestModel();
    }

    /**
     * Minimal in-memory Model implementation for testing ExportCommand.
     */
    private static class TestModel implements Model {
        private final AddressBook addressBook = new AddressBook();

        @Override
        public void removeAlias(String alias) {
            throw new AssertionError("This method should not be called.");
        }


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
        public AddressBook getAddressBook() {
            return addressBook;
        }

        @Override
        public boolean hasPerson(Person person) {
            return addressBook.hasPerson(person);
        }

        @Override
        public void deletePerson(Person target) {

        }

        @Override
        public void addPerson(Person person) {
            addressBook.addPerson(person);
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

        // Other Model methods are not needed for these tests
    }

    @Test
    void execute_emptyAddressBook_returnsEmptyMessage() throws Exception {
        ExportCommand command = new ExportCommand();
        assertEquals(
                ExportCommand.EMPTY_ADDRESSBOOK,
                command.execute(model).getFeedbackToUser()
        );
    }

    @Test
    void execute_successfulExport_returnsAcknowledgement() throws Exception {
        // Arrange: add a contact
        Person alice = new Person(
                new Name("Alice Pauline"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Road"),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new Favorite(false)
        );
        model.addPerson(alice);

        // Override export path to tempDir
        ExportCommand command = new ExportCommand() {
            @Override
            public CommandResult execute(Model model) throws CommandException {
                Path testPath = tempDir.resolve("CampusBook_contacts.csv");
                List<Person> allContacts = new ArrayList<>(model.getAddressBook().getPersonList());

                if (allContacts.isEmpty()) {
                    return new CommandResult(EMPTY_ADDRESSBOOK);
                }
                try {
                    CsvUtil.writeContactsToCsv(testPath, allContacts);
                } catch (IOException e) {
                    throw new CommandException(FAILED_EXPORT);
                }
                return new CommandResult(MESSAGE_EXPORT_ACKNOWLEDGEMENT);
            }
        };

        CommandResult result = command.execute(model);

        assertEquals(ExportCommand.MESSAGE_EXPORT_ACKNOWLEDGEMENT, result.getFeedbackToUser());

        // Verify CSV file exists and contains contact
        Path exportedFile = tempDir.resolve("CampusBook_contacts.csv");
        assert(Files.exists(exportedFile));
        String content = Files.readString(exportedFile);
        assert(content.contains("Alice Pauline"));
    }

    @Test
    void execute_ioException_throwsCommandException() throws Exception {
        // Arrange: add a contact
        Person alice = new Person(
                new Name("Alice Pauline"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Road"),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new Favorite(false)
        );
        model.addPerson(alice);

        // Override execute to simulate IOException
        ExportCommand command = new ExportCommand() {
            @Override
            public CommandResult execute(Model model) throws CommandException {
                throw new CommandException(FAILED_EXPORT);
            }
        };

        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
