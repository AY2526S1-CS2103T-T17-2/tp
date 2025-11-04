package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    // --- Tests for Deleting by Index (Original Functionality) ---

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    // --- Tests for Deleting by Predicate (New Batch Delete Functionality) ---

    @Test
    public void execute_validPredicate_deleteSinglePersonSuccess() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice"));
        DeleteCommand deleteCommand = new DeleteCommand(List.of(predicate));

        String expectedMessage = DeleteCommand.MESSAGE_DELETE_ONE_PERSON_SUCCESS;

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validPredicate_deleteMultiplePersonsSuccess() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("Meier"));
        DeleteCommand deleteCommand = new DeleteCommand(List.of(predicate));

        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                .filter(predicate).collect(Collectors.toList());
        assertTrue(personsToDelete.size() > 1);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS,
                personsToDelete.size());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        for (Person p : personsToDelete) {
            expectedModel.deletePerson(p);
        }

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validCombinedPredicate_deleteSinglePersonSuccess() {
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList("Benson"));
        TagContainsKeywordsPredicate tagPredicate = new TagContainsKeywordsPredicate(Arrays.asList("friends"));
        List<Predicate<Person>> predicates = List.of(namePredicate, tagPredicate);

        DeleteCommand deleteCommand = new DeleteCommand(predicates);

        Predicate<Person> combinedPredicate = predicates.stream().reduce(Predicate::and).get();
        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                .filter(combinedPredicate).collect(Collectors.toList());
        assertEquals(1, personsToDelete.size());
        assertEquals(BENSON, personsToDelete.get(0));

        String expectedMessage = DeleteCommand.MESSAGE_DELETE_ONE_PERSON_SUCCESS;

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_validPredicate_noPersonFound() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("NonExistentName"));
        DeleteCommand deleteCommand = new DeleteCommand(List.of(predicate));

        String expectedMessage = DeleteCommand.MESSAGE_NO_PERSONS_FOUND_TO_DELETE;

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    // --- Tests for equals() and toString() (Updated for new fields) ---

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        Predicate<Person> firstPredicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        Predicate<Person> secondPredicate = new NameContainsKeywordsPredicate(List.of("Bob"));

        DeleteCommand deleteFirstPredicateCommand = new DeleteCommand(List.of(firstPredicate));
        DeleteCommand deleteSecondPredicateCommand = new DeleteCommand(List.of(secondPredicate));

        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));
        assertFalse(deleteFirstCommand.equals(1));
        assertFalse(deleteFirstCommand.equals(null));
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        assertTrue(deleteFirstPredicateCommand.equals(deleteFirstPredicateCommand));
        DeleteCommand deleteFirstPredicateCommandCopy = new DeleteCommand(List.of(firstPredicate));
        assertTrue(deleteFirstPredicateCommand.equals(deleteFirstPredicateCommandCopy));
        assertFalse(deleteFirstPredicateCommand.equals(deleteSecondPredicateCommand));

        assertFalse(deleteFirstCommand.equals(deleteFirstPredicateCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommandIndex = new DeleteCommand(targetIndex);
        String expectedIndex = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expectedIndex, deleteCommandIndex.toString());

        Predicate<Person> predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        List<Predicate<Person>> predicates = List.of(predicate);
        DeleteCommand deleteCommandPredicate = new DeleteCommand(predicates);
        String expectedPredicate = DeleteCommand.class.getCanonicalName()
                + "{predicates=" + predicates.toString() + "}";
        assertEquals(expectedPredicate, deleteCommandPredicate.toString());
    }

    /**
     * Helper method to update {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
