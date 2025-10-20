package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;

public class ExportCommandTest {

    @TempDir
    public Path testFolder;

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ExportCommand exportCommand = new ExportCommand();
        assertThrows(NullPointerException.class, () -> exportCommand.execute(null));
    }

    @Test
    public void execute_emptyList_exportsEmptyFile() throws Exception {
        TestModel model = new TestModel(); // Model with empty list
        ExportCommand exportCommand = new ExportCommand();
        CommandResult result = exportCommand.execute(model);
        assertEquals(ExportCommand.EMPTY_ADDRESSBOOK, result.getFeedbackToUser());
    }

    private static class TestModel implements Model {
        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        // Methods from Alias feature
        @Override
        public Map<String, String> getCommandAliases() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAlias(String alias, String commandString) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean removeAlias(String alias) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearAliases() {
            throw new AssertionError("This method should not be called.");
        }
    }
}
