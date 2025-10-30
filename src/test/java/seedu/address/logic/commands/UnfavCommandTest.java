package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code UnfavCommand}.
 */
public class UnfavCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToUnfav = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favoritedPerson = new PersonBuilder(personToUnfav).withFavorite(true).build();
        model.setPerson(personToUnfav, favoritedPerson);

        UnfavCommand unfavCommand = new UnfavCommand(INDEX_FIRST_PERSON);

        Person unfavoritedPerson = new PersonBuilder(favoritedPerson).withFavorite(false).build();

        String expectedMessage = String.format(UnfavCommand.MESSAGE_UNFAV_PERSON_SUCCESS,
                Messages.format(unfavoritedPerson));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(favoritedPerson, unfavoritedPerson);

        assertCommandSuccess(unfavCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnfavCommand unfavCommand = new UnfavCommand(outOfBoundIndex);

        assertCommandFailure(unfavCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToUnfav = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favoritedPerson = new PersonBuilder(personToUnfav).withFavorite(true).build();
        model.setPerson(personToUnfav, favoritedPerson);

        UnfavCommand unfavCommand = new UnfavCommand(INDEX_FIRST_PERSON);

        Person unfavoritedPerson = new PersonBuilder(favoritedPerson).withFavorite(false).build();

        String expectedMessage = String.format(UnfavCommand.MESSAGE_UNFAV_PERSON_SUCCESS,
                Messages.format(unfavoritedPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        Person personInExpectedModel = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favoritedPersonInExpectedModel = new PersonBuilder(personInExpectedModel).withFavorite(true).build();
        expectedModel.setPerson(personInExpectedModel, favoritedPersonInExpectedModel);
        expectedModel.setPerson(favoritedPersonInExpectedModel, unfavoritedPerson);

        assertCommandSuccess(unfavCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        UnfavCommand unfavCommand = new UnfavCommand(outOfBoundIndex);

        assertCommandFailure(unfavCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_notFavorited_throwsCommandException() {
        Person personNotFavorited = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        UnfavCommand unfavCommand = new UnfavCommand(INDEX_FIRST_PERSON);

        assertCommandFailure(unfavCommand, model, UnfavCommand.MESSAGE_NOT_FAVORITED);
    }

    @Test
    public void equals() {
        UnfavCommand unfavFirstCommand = new UnfavCommand(INDEX_FIRST_PERSON);
        UnfavCommand unfavSecondCommand = new UnfavCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(unfavFirstCommand.equals(unfavFirstCommand));

        // same values -> returns true
        UnfavCommand unfavFirstCommandCopy = new UnfavCommand(INDEX_FIRST_PERSON);
        assertTrue(unfavFirstCommand.equals(unfavFirstCommandCopy));

        // different types -> returns false
        assertFalse(unfavFirstCommand.equals(1));

        // null -> returns false
        assertFalse(unfavFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(unfavFirstCommand.equals(unfavSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        UnfavCommand unfavCommand = new UnfavCommand(targetIndex);
        String expected = UnfavCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, unfavCommand.toString());
    }
}

