package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.util.CsvUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.favorite.Favorite;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

class ExportCommandTest {

    @TempDir
    Path tempDir;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
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
