package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class ModelStub implements Model {
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
    public ReadOnlyAddressBook getAddressBook() {
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


public class ImportCommandTest {
    @Test
    public void execute_nullModel_throwsNullPointerException() {
        Path validFilePath = Paths.get("src/test/data/CsvUtilTest/valid.csv");
        ImportCommand importCommand = new ImportCommand(validFilePath);
        assertThrows(NullPointerException.class, () -> importCommand.execute(null));
    }

    @Test
    public void execute_importSuccessful() throws CommandException {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Path validFilePath = Paths.get("src/test/data/CsvUtilTest/valid.csv");
        ImportCommand importCommand = new ImportCommand(validFilePath);

        CommandResult commandResult = importCommand.execute(modelStub);
        String expectedMessage = String.format("Imported %d contact(s). Skipped %d duplicate row(s).", 2, 0);
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
    }

    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();
        @Override
        public boolean hasPerson(Person person) {
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }
        @Override
        public void addPerson(Person person) {
            personsAdded.add(person);
        }
    }

    @Test
    public void execute_duplicatePersonInCsv_addsFirstSkipsSecond() throws CommandException {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Path duplicatePersonFilePath = Paths.get("src/test/data/CsvUtilTest/duplicatePerson.csv");
        ImportCommand importCommand = new ImportCommand(duplicatePersonFilePath);

        CommandResult commandResult = importCommand.execute(modelStub);
        assertEquals(1, modelStub.personsAdded.size());
        String expectedMessage = String.format("Imported %d contact(s). Skipped %d duplicate row(s).", 1, 1);
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_personAlreadyInAddressBook_skipsDuplicate() throws CommandException {
        ModelStubWithPerson modelStub = new ModelStubWithPerson(new PersonBuilder().withName("Alex").build());
        Path filePath = Paths.get("src/test/data/CsvUtilTest/alreadyExists.csv"); // CSV contains "Alex"
        ImportCommand importCommand = new ImportCommand(filePath);

        CommandResult commandResult = importCommand.execute(modelStub);
        String expectedMessage = String.format("Imported %d contact(s). Skipped %d duplicate row(s).", 0, 1);
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
        assertEquals(0, modelStub.personsAdded.size());
    }

    private class ModelStubWithPerson extends ModelStub {
        private final Person person;
        final ArrayList<Person> personsAdded = new ArrayList<>();

        ModelStubWithPerson(Person person) {
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            return this.person.isSamePerson(person);
        }

        @Override
        public void addPerson(Person person) {
            personsAdded.add(person);
        }
    }

    @Test
    public void execute_invalidFilePath_throwsCommandException() {
        ModelStub modelStub = new ModelStub();
        Path invalidFilePath = Paths.get("path/to/nonexistent/file.csv");
        ImportCommand importCommand = new ImportCommand(invalidFilePath);
        assertThrows(CommandException.class,
                ImportCommand.INVALID_PATH_ERROR, () -> importCommand.execute(modelStub));
    }
}
