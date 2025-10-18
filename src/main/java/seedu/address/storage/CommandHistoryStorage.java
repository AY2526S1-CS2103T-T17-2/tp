package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;

/**
 * Represents a storage for command history.
 */
public interface CommandHistoryStorage {

    /**
     * Returns the file path of the command history data file.
     */
    Path getCommandHistoryFilePath();

    /**
     * Returns command history data from storage.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if the loading of data from file failed.
     */
    Optional<List<String>> readCommandHistory() throws DataLoadingException;

    /**
     * Saves the given command history to the storage.
     * @param commandHistory cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveCommandHistory(List<String> commandHistory) throws IOException;

}

