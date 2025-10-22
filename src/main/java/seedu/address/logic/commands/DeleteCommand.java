package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index or criteria from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list OR "
            + "deletes all persons matching the specified criteria (similar to find command).\n"
            + "Parameters (Option 1 - by index): INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Parameters (Option 2 - by criteria): "
            + "[" + seedu.address.logic.parser.CliSyntax.PREFIX_NAME + "NAME_KEYWORD [MORE_KEYWORDS]...] "
            + "[" + seedu.address.logic.parser.CliSyntax.PREFIX_TAG + "TAG_KEYWORD [MORE_KEYWORDS]...] "
            + "[" + seedu.address.logic.parser.CliSyntax.PREFIX_MODULE + "MODULE_KEYWORD [MORE_KEYWORDS]...] "
            + "[" + seedu.address.logic.parser.CliSyntax.PREFIX_FACULTY + "FACULTY_KEYWORD [MORE_KEYWORDS]...]\n"
            + "Example: " + COMMAND_WORD + " " + seedu.address.logic.parser.CliSyntax.PREFIX_NAME + "alex "
            + seedu.address.logic.parser.CliSyntax.PREFIX_TAG + "friends";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS = "Deleted %1$d persons.";
    public static final String MESSAGE_NO_PERSONS_FOUND_TO_DELETE = "No persons found matching the criteria to delete.";


    private final Index targetIndex; // For single delete
    private final List<Predicate<Person>> predicates;

    /**
     * Constructor for deleting a single person by index.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
        this.predicates = null; // Indicate deletion by index
    }

    /**
     * Constructor for deleting multiple persons by predicate list.
     */
    public DeleteCommand(List<Predicate<Person>> predicates) {
        this.targetIndex = null;
        this.predicates = predicates;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (targetIndex != null) {
            // Logic for deleting by index (original logic)
            return executeDeleteByIndex(model);
        } else {
            // Logic for deleting by predicate (batch delete)
            return executeDeleteByPredicate(model);
        }
    }

    /**
     * Executes the delete command for a single person identified by index.
     */
    private CommandResult executeDeleteByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    /**
     * Executes the delete command for multiple persons identified by a predicate.
     */
    private CommandResult executeDeleteByPredicate(Model model) {
        Predicate<Person> combinedPredicate = predicates.stream()
                                                        .reduce(Predicate::and)
                                                        .orElse(x -> true);

        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                                              .filter(combinedPredicate) // Use the combined predicate
                                              .collect(Collectors.toList());

        if (personsToDelete.isEmpty()) {
            return new CommandResult(MESSAGE_NO_PERSONS_FOUND_TO_DELETE);
        }

        for (Person person : personsToDelete) {
            model.deletePerson(person);
        }

        return new CommandResult(String.format(MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS, personsToDelete.size()));
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;

        return Objects.equals(targetIndex, otherDeleteCommand.targetIndex)
                && Objects.equals(predicates, otherDeleteCommand.predicates);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (targetIndex != null) {
            builder.add("targetIndex", targetIndex);
        }
        if (predicates != null) {
            builder.add("predicates", predicates);
        }
        return builder.toString();
    }
}
