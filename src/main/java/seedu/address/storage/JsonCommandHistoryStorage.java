package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;

/**
 * A class to access command history stored in the hard disk as a json file
 */
public class JsonCommandHistoryStorage implements CommandHistoryStorage {

    private Path filePath;

    public JsonCommandHistoryStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getCommandHistoryFilePath() {
        return filePath;
    }

    @Override
    public Optional<List<String>> readCommandHistory() throws DataLoadingException {
        return readCommandHistory(filePath);
    }

    /**
     * Similar to {@link #readCommandHistory()}
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if the file format is not as expected.
     */
    public Optional<List<String>> readCommandHistory(Path filePath) throws DataLoadingException {
        Optional<JsonSerializableCommandHistory> jsonCommandHistory =
                JsonUtil.readJsonFile(filePath, JsonSerializableCommandHistory.class);
        if (!jsonCommandHistory.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(jsonCommandHistory.get().toModelType());
    }

    @Override
    public void saveCommandHistory(List<String> commandHistory) throws IOException {
        requireNonNull(commandHistory);

        FileUtil.createIfMissing(filePath);
        JsonSerializableCommandHistory jsonCommandHistory =
                new JsonSerializableCommandHistory(commandHistory);
        JsonUtil.saveJsonFile(jsonCommandHistory, filePath);
    }

}

