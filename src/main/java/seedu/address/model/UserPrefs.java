package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Path addressBookFilePath = Paths.get("data" , "addressbook.json");
    private Map<String, String> commandAliases = new HashMap<>();

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);
        setGuiSettings(newUserPrefs.getGuiSettings());
        setAddressBookFilePath(newUserPrefs.getAddressBookFilePath());
        setCommandAliases(new HashMap<>(newUserPrefs.getCommandAliases()));
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    public Path getAddressBookFilePath() {
        return addressBookFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        this.addressBookFilePath = addressBookFilePath;
    }

    @Override
    public Map<String, String> getCommandAliases() {
        return new HashMap<>(commandAliases);
    }

    public void setCommandAliases(Map<String, String> commandAliases) {
        requireNonNull(commandAliases);
        this.commandAliases = new HashMap<>(commandAliases);
    }

    /**
     * Adds a command alias.
     *
     * @param alias The alias name.
     * @param commandString The full command string it maps to.
     */
    public void addAlias(String alias, String commandString) {
        requireNonNull(alias);
        requireNonNull(commandString);
        this.commandAliases.put(alias, commandString);
    }

    /**
     * Removes a command alias.
     *
     * @param alias The alias name to remove.
     * @return The command string that was associated with the alias, or null if not found.
     */
    public String removeAlias(String alias) {
        requireNonNull(alias);
        return this.commandAliases.remove(alias);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UserPrefs)) {
            return false;
        }

        UserPrefs otherUserPrefs = (UserPrefs) other;
        return guiSettings.equals(otherUserPrefs.guiSettings)
                && addressBookFilePath.equals(otherUserPrefs.addressBookFilePath)
                && commandAliases.equals(otherUserPrefs.commandAliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, addressBookFilePath, commandAliases);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings);
        sb.append("\nLocal data file location : " + addressBookFilePath);
        sb.append("\nCommand Aliases : " + commandAliases);
        return sb.toString();
    }

}
