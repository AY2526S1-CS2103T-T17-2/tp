package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Required for alias
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs); // Ensures we have a mutable UserPrefs
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());

        // Sort the address book by favorite status on initialization
        sortByFavoriteStatus();
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== Alias Methods ======================================================================

    /**
     * Returns an unmodifiable view of the command alias map from UserPrefs.
     */
    @Override
    public Map<String, String> getCommandAliases() {
        return userPrefs.getCommandAliases();
    }

    /**
     * Adds a new command alias to UserPrefs.
     * @param alias The alias name.
     * @param commandString The full command string it maps to.
     */
    @Override
    public void addAlias(String alias, String commandString) {
        // The mutable UserPrefs object held by ModelManager is updated
        userPrefs.addAlias(alias, commandString);
    }

    /**
     * Removes a command alias from UserPrefs.
     * @param alias The alias name to remove.
     * @return true if the alias was removed, false otherwise.
     */
    @Override
    public boolean removeAlias(String alias) {
        // The mutable UserPrefs object held by ModelManager is updated
        return userPrefs.removeAlias(alias) != null;
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // If adding a favorite contact, place it at the top
        if (person.getFavorite().getIsFavorite()) {
            sortByFavoriteStatus(person);
        }
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        // Check if favorite status has actually changed
        boolean favoriteStatusChanged = !target.getFavorite().equals(editedPerson.getFavorite());

        // Update the person data
        addressBook.setPerson(target, editedPerson);

        // Only sort if favorite status has changed
        if (favoriteStatusChanged) {
            sortByFavoriteStatus(editedPerson);
        }
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the filtered list of {@code Person}.
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Sorting Methods ============================================================================

    /**
     * Sorts the entire address book by favorite status.
     * Favorite contacts appear first, followed by non-favorites.
     * Within each group, the original order is preserved.
     */
    private void sortByFavoriteStatus() {
        sortByFavoriteStatus(null);
    }

    /**
     * Sorts the address book by favorite status, with special handling for the edited person.
     * If a person is newly favorited, they are placed at the top of the favorites group.
     * If a person is unfavorited, they are placed at the bottom of the non-favorites group.
     *
     * @param editedPerson The person whose favorite status was changed, or null for general sorting
     */
    private void sortByFavoriteStatus(Person editedPerson) {
        // Get the complete underlying list (not affected by filters)
        ObservableList<Person> personList = addressBook.getPersonList();
        List<Person> allPersons = new ArrayList<>(personList);

        List<Person> favorites = new ArrayList<>();
        List<Person> nonFavorites = new ArrayList<>();

        // If editedPerson is newly favorited, add them first
        if (editedPerson != null && editedPerson.getFavorite().getIsFavorite()) {
            favorites.add(editedPerson);
        }

        // Separate remaining contacts, preserving original order within each group
        for (Person person : allPersons) {
            // Skip the editedPerson as it's already handled
            if (editedPerson != null && person.equals(editedPerson)) {
                continue;
            }

            if (person.getFavorite().getIsFavorite()) {
                favorites.add(person);
            } else {
                nonFavorites.add(person);
            }
        }

        // If editedPerson is unfavorited, add them last to non-favorites
        if (editedPerson != null && !editedPerson.getFavorite().getIsFavorite()) {
            nonFavorites.add(editedPerson);
        }

        // Recombine: favorites first, then non-favorites
        List<Person> sortedList = new ArrayList<>();
        sortedList.addAll(favorites);
        sortedList.addAll(nonFavorites);

        // Update the underlying list
        addressBook.setPersons(sortedList);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
