package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;

public class JsonCommandHistoryStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data",
            "JsonCommandHistoryStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readCommandHistory_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readCommandHistory(null));
    }

    @Test
    public void readCommandHistory_missingFile_emptyResult() throws Exception {
        assertFalse(readCommandHistory("NonExistentFile.json").isPresent());
    }

    @Test
    public void readCommandHistory_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () ->
                readCommandHistory("notJsonFormatCommandHistory.json"));
    }

    @Test
    public void readCommandHistory_invalidCommandHistory_throwsDataLoadingException() {
        assertThrows(DataLoadingException.class, () ->
                readCommandHistory("invalidCommandHistory.json"));
    }

    @Test
    public void readCommandHistory_validFile_success() throws Exception {
        Optional<List<String>> result = readCommandHistory("validCommandHistory.json");
        assertTrue(result.isPresent());
        List<String> history = result.get();
        assertEquals(3, history.size());
        assertEquals("list", history.get(0));
        assertEquals("help", history.get(1));
        assertEquals("exit", history.get(2));
    }

    @Test
    public void readCommandHistory_emptyHistory_success() throws Exception {
        Optional<List<String>> result = readCommandHistory("emptyCommandHistory.json");
        assertTrue(result.isPresent());
        List<String> history = result.get();
        assertEquals(0, history.size());
    }

    @Test
    public void saveCommandHistory_nullHistory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveCommandHistory(null, "SomeFile.json"));
    }

    @Test
    public void saveCommandHistory_validHistory_success() throws Exception {
        Path filePath = testFolder.resolve("tempCommandHistory.json");
        List<String> originalHistory = Arrays.asList("list", "help", "find n/Alex");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);
        storage.saveCommandHistory(originalHistory);

        // Read back and verify
        Optional<List<String>> readBack = storage.readCommandHistory();
        assertTrue(readBack.isPresent());
        List<String> history = readBack.get();
        assertEquals(3, history.size());
        assertEquals("list", history.get(0));
        assertEquals("help", history.get(1));
        assertEquals("find n/Alex", history.get(2));
    }

    @Test
    public void saveCommandHistory_emptyHistory_success() throws Exception {
        Path filePath = testFolder.resolve("tempCommandHistory.json");
        List<String> emptyHistory = Arrays.asList();
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);
        storage.saveCommandHistory(emptyHistory);

        // Read back and verify
        Optional<List<String>> readBack = storage.readCommandHistory();
        assertTrue(readBack.isPresent());
        assertEquals(0, readBack.get().size());
    }

    @Test
    public void saveAndReadCommandHistory_allInOrder() throws Exception {
        Path filePath = testFolder.resolve("tempCommandHistory.json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);

        // Save first set
        List<String> history1 = Arrays.asList("cmd1", "cmd2", "cmd3");
        storage.saveCommandHistory(history1);
        assertEquals(history1, storage.readCommandHistory().get());

        // Overwrite with new set
        List<String> history2 = Arrays.asList("new1", "new2");
        storage.saveCommandHistory(history2);
        assertEquals(history2, storage.readCommandHistory().get());
    }

    @Test
    public void getCommandHistoryFilePath_success() {
        Path expectedPath = Paths.get("data", "commandhistory.json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(expectedPath);
        assertEquals(expectedPath, storage.getCommandHistoryFilePath());
    }

    private Optional<List<String>> readCommandHistory(String filePath) throws Exception {
        return new JsonCommandHistoryStorage(Paths.get(filePath))
                .readCommandHistory(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String commandHistoryFileInTestDataFolder) {
        return commandHistoryFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(commandHistoryFileInTestDataFolder)
                : null;
    }

    private void saveCommandHistory(List<String> history, String filePath) throws IOException {
        new JsonCommandHistoryStorage(Paths.get(filePath)).saveCommandHistory(history);
    }
}
