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
 * Marks a person as favorite in the address book.
 */
public class FavCommand extends Command {

    public static final String COMMAND_WORD = "fav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the person identified by the index number used in the displayed person list as favorite.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_FAV_PERSON_SUCCESS = "Marked person as favorite: %1$s";
    public static final String MESSAGE_ALREADY_FAVORITED = "This person is already marked as favorite.";

    private final Index targetIndex;

    /**
     * Creates a FavCommand to mark the person at the specified {@code Index} as favorite.
     */
    public FavCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToFav = lastShownList.get(targetIndex.getZeroBased());

        if (personToFav.getFavorite().getIsFavorite()) {
            throw new CommandException(MESSAGE_ALREADY_FAVORITED);
        }

        Person favoritedPerson = createFavoritedPerson(personToFav);

        model.setPerson(personToFav, favoritedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_FAV_PERSON_SUCCESS, Messages.format(favoritedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the favorite status set to true.
     */
    private static Person createFavoritedPerson(Person personToFav) {
        assert personToFav != null;

        return new Person(
                personToFav.getName(),
                personToFav.getPhone(),
                personToFav.getEmail(),
                personToFav.getAddress(),
                personToFav.getTags(),
                personToFav.getModules(),
                personToFav.getFaculties(),
                new Favorite(true)
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FavCommand)) {
            return false;
        }

        FavCommand otherFavCommand = (FavCommand) other;
        return targetIndex.equals(otherFavCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}

