package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.parser.AliasProvider;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model, AliasProvider {
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
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());

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
        userPrefs.addAlias(alias, commandString);
    }

    /**
     * Removes a command alias from UserPrefs.
     * @param alias The alias name to remove.
     * @return true if the alias was removed, false otherwise.
     */
    @Override
    public boolean removeAlias(String alias) {
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

        if (person.getFavorite().getIsFavorite()) {
            sortByFavoriteStatus(person);
        }
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        boolean favoriteStatusChanged = !target.getFavorite().equals(editedPerson.getFavorite());

        addressBook.setPerson(target, editedPerson);

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
        ObservableList<Person> personList = addressBook.getPersonList();
        List<Person> allPersons = new ArrayList<>(personList);

        List<Person> favorites = new ArrayList<>();
        List<Person> nonFavorites = new ArrayList<>();

        if (editedPerson != null && editedPerson.getFavorite().getIsFavorite()) {
            favorites.add(editedPerson);
        }

        for (Person person : allPersons) {
            if (editedPerson != null && person.equals(editedPerson)) {
                continue;
            }

            if (person.getFavorite().getIsFavorite()) {
                favorites.add(person);
            } else {
                nonFavorites.add(person);
            }
        }

        if (editedPerson != null && !editedPerson.getFavorite().getIsFavorite()) {
            nonFavorites.add(editedPerson);
        }

        List<Person> sortedList = new ArrayList<>();
        sortedList.addAll(favorites);
        sortedList.addAll(nonFavorites);

        addressBook.setPersons(sortedList);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
