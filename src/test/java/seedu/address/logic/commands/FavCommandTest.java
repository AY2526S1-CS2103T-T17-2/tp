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
 * {@code FavCommand}.
 */
public class FavCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToFav = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        FavCommand favCommand = new FavCommand(INDEX_FIRST_PERSON);

        Person favoritedPerson = new PersonBuilder(personToFav).withFavorite(true).build();

        String expectedMessage = String.format(FavCommand.MESSAGE_FAV_PERSON_SUCCESS,
                Messages.format(favoritedPerson));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToFav, favoritedPerson);

        assertCommandSuccess(favCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        FavCommand favCommand = new FavCommand(outOfBoundIndex);

        assertCommandFailure(favCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToFav = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        FavCommand favCommand = new FavCommand(INDEX_FIRST_PERSON);

        Person favoritedPerson = new PersonBuilder(personToFav).withFavorite(true).build();

        String expectedMessage = String.format(FavCommand.MESSAGE_FAV_PERSON_SUCCESS,
                Messages.format(favoritedPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToFav, favoritedPerson);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(favCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        FavCommand favCommand = new FavCommand(outOfBoundIndex);

        assertCommandFailure(favCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_alreadyFavorited_throwsCommandException() {
        Person personToFav = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person favoritedPerson = new PersonBuilder(personToFav).withFavorite(true).build();
        model.setPerson(personToFav, favoritedPerson);

        FavCommand favCommand = new FavCommand(INDEX_FIRST_PERSON);

        assertCommandFailure(favCommand, model, FavCommand.MESSAGE_ALREADY_FAVORITED);
    }

    @Test
    public void equals() {
        FavCommand favFirstCommand = new FavCommand(INDEX_FIRST_PERSON);
        FavCommand favSecondCommand = new FavCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(favFirstCommand.equals(favFirstCommand));

        // same values -> returns true
        FavCommand favFirstCommandCopy = new FavCommand(INDEX_FIRST_PERSON);
        assertTrue(favFirstCommand.equals(favFirstCommandCopy));

        // different types -> returns false
        assertFalse(favFirstCommand.equals(1));

        // null -> returns false
        assertFalse(favFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(favFirstCommand.equals(favSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        FavCommand favCommand = new FavCommand(targetIndex);
        String expected = FavCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, favCommand.toString());
    }
}

