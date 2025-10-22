package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
// Import typical persons for testing predicate-based deletion
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
import seedu.address.model.person.TagContainsKeywordsPredicate; // Import Tag predicate for combined test

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    // Initialize a model with typical data for testing
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    // --- Tests for Deleting by Index (Original Functionality) ---

    @Test
    public void execute_validIndexUnfilteredList_success() {
        // Test deleting a person by index from an unfiltered list
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        // Define the expected success message
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        // Define the expected model state after deletion
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        // Test deleting with an out-of-bounds index
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        // Expect an invalid index error message
        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        // Test deleting a person by index from a *filtered* list
        showPersonAtIndex(model, INDEX_FIRST_PERSON); // Filter list to show only one person

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON); // Index 1 is valid in the filtered list

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel); // The filtered list in the expected model should now be empty

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        // Test deleting with an index that is out-of-bounds for the *filtered* list
        showPersonAtIndex(model, INDEX_FIRST_PERSON); // Filtered list size is 1

        Index outOfBoundIndex = INDEX_SECOND_PERSON; // Index 2 is out of bounds for the filtered list
        // Ensure this index is *within* the bounds of the full address book list
        // to confirm we are testing the filtered list bounds
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    // --- Tests for Deleting by Predicate (New Batch Delete Functionality) ---

    @Test
    public void execute_validPredicate_deleteSinglePersonSuccess() {
        // Test batch delete where the predicate matches exactly *one* person
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice"));
        // Use the new constructor that accepts a List<Predicate>
        DeleteCommand deleteCommand = new DeleteCommand(List.of(predicate));

        // Expect the "multiple persons" success message, even for 1 person
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS, 1);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE); // Manually delete Alice from the expected model

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validPredicate_deleteMultiplePersonsSuccess() {
        // Test batch delete where the predicate matches *multiple* persons
        // "Meier" matches both Benson and Daniel in TypicalPersons
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("Meier"));
        DeleteCommand deleteCommand = new DeleteCommand(List.of(predicate));

        // Find all persons to be deleted from the *original* model for verification
        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                .filter(predicate).collect(Collectors.toList());
        assertTrue(personsToDelete.size() > 1); // Verify our test setup is correct

        // Expect the success message with the correct count
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS,
                personsToDelete.size());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        // Manually delete all matched persons from the expected model
        for (Person p : personsToDelete) {
            expectedModel.deletePerson(p);
        }

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validCombinedPredicate_deleteSinglePersonSuccess() {
        // Test batch delete using a *combined* (AND) predicate
        // Target: name "Benson" AND tag "friends" (should only match Benson)
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList("Benson"));
        TagContainsKeywordsPredicate tagPredicate = new TagContainsKeywordsPredicate(Arrays.asList("friends"));
        List<Predicate<Person>> predicates = List.of(namePredicate, tagPredicate);

        DeleteCommand deleteCommand = new DeleteCommand(predicates);

        // Verify test setup: combine predicates manually and check match
        Predicate<Person> combinedPredicate = predicates.stream().reduce(Predicate::and).get();
        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                .filter(combinedPredicate).collect(Collectors.toList());
        assertEquals(1, personsToDelete.size());
        assertEquals(BENSON, personsToDelete.get(0));

        // Expect success message for 1 person
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS, 1);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_validPredicate_noPersonFound() {
        // Test batch delete where the predicate matches *no one*
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("NonExistentName"));
        DeleteCommand deleteCommand = new DeleteCommand(List.of(predicate));

        // Expect the "no persons found" message
        String expectedMessage = DeleteCommand.MESSAGE_NO_PERSONS_FOUND_TO_DELETE;

        // Expected model is identical to the original model (no deletions)
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    // --- Tests for equals() and toString() (Updated for new fields) ---

    @Test
    public void equals() {
        // --- Index-based commands ---
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // --- Predicate-based commands (using List constructor) ---
        Predicate<Person> firstPredicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        Predicate<Person> secondPredicate = new NameContainsKeywordsPredicate(List.of("Bob"));

        DeleteCommand deleteFirstPredicateCommand = new DeleteCommand(List.of(firstPredicate));
        DeleteCommand deleteSecondPredicateCommand = new DeleteCommand(List.of(secondPredicate));

        // --- Index comparisons ---
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand)); // same object
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy)); // same index value
        assertFalse(deleteFirstCommand.equals(1)); // different type
        assertFalse(deleteFirstCommand.equals(null)); // vs null
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand)); // different index

        // --- Predicate comparisons (compares the lists) ---
        assertTrue(deleteFirstPredicateCommand.equals(deleteFirstPredicateCommand)); // same object
        DeleteCommand deleteFirstPredicateCommandCopy = new DeleteCommand(List.of(firstPredicate));
        assertTrue(deleteFirstPredicateCommand.equals(deleteFirstPredicateCommandCopy)); // same predicate list
        assertFalse(deleteFirstPredicateCommand.equals(deleteSecondPredicateCommand)); // different predicate list

        // --- Mixed comparisons ---
        // Index-based command vs Predicate-based command
        assertFalse(deleteFirstCommand.equals(deleteFirstPredicateCommand));
    }

    @Test
    public void toStringMethod() {
        // Test toString() for index-based command
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommandIndex = new DeleteCommand(targetIndex);
        String expectedIndex = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expectedIndex, deleteCommandIndex.toString());

        // Test toString() for predicate-based command (should show "predicates=[...]")
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
