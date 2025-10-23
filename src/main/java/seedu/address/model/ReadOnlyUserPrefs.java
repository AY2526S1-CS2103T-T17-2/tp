package seedu.address.model;

import java.nio.file.Path;
import java.util.Map;

import seedu.address.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    Path getAddressBookFilePath();

    // Add this method
    /**
     * Returns an unmodifiable view of the command aliases.
     */
    Map<String, String> getCommandAliases();

}
