package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.favorite.Favorite;
import seedu.address.model.person.Person;

/**
 * Removes a person from favorites in the address book.
 */
public class UnfavCommand extends Command {

    public static final String COMMAND_WORD = "unfav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the person identified by the index number used in the displayed person list "
            + "from favorites.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNFAV_PERSON_SUCCESS = "Removed person from favorites: %1$s";
    public static final String MESSAGE_NOT_FAVORITED = "This person is not marked as favorite.";

    private final Index targetIndex;

    /**
     * Creates an UnfavCommand to remove the person at the specified {@code Index} from favorites.
     */
    public UnfavCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUnfav = lastShownList.get(targetIndex.getZeroBased());

        if (!personToUnfav.getFavorite().getIsFavorite()) {
            throw new CommandException(MESSAGE_NOT_FAVORITED);
        }

        Person unfavoritedPerson = createUnfavoritedPerson(personToUnfav);

        model.setPerson(personToUnfav, unfavoritedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_UNFAV_PERSON_SUCCESS, Messages.format(unfavoritedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the favorite status set to false.
     */
    private static Person createUnfavoritedPerson(Person personToUnfav) {
        assert personToUnfav != null;

        return new Person(
                personToUnfav.getName(),
                personToUnfav.getPhone(),
                personToUnfav.getEmail(),
                personToUnfav.getAddress(),
                personToUnfav.getTags(),
                personToUnfav.getModules(),
                personToUnfav.getFaculties(),
                new Favorite(false)
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnfavCommand)) {
            return false;
        }

        UnfavCommand otherUnfavCommand = (UnfavCommand) other;
        return targetIndex.equals(otherUnfavCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}

