package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandHistory(null));
    }

    @Test
    public void constructor_emptyList_success() {
        CommandHistory history = new CommandHistory(new ArrayList<>());
        assertEquals(0, history.getHistory().size());
    }

    @Test
    public void constructor_withInitialCommands_success() {
        List<String> initialCommands = Arrays.asList("list", "help", "exit");
        CommandHistory history = new CommandHistory(initialCommands);
        assertEquals(3, history.getHistory().size());
        assertEquals("list", history.getHistory().get(0));
        assertEquals("help", history.getHistory().get(1));
        assertEquals("exit", history.getHistory().get(2));
    }

    @Test
    public void constructor_exceedsMaxSize_trimsToMaxSize() {
        List<String> commands = Arrays.asList(
            "cmd1", "cmd2", "cmd3", "cmd4", "cmd5",
            "cmd6", "cmd7", "cmd8", "cmd9", "cmd10",
            "cmd11", "cmd12"
        );
        CommandHistory history = new CommandHistory(commands);

        // Verify size is limited to 10
        assertEquals(10, history.getHistory().size());

        // Verify behavior: oldest commands are removed, newest are kept
        assertFalse(history.getHistory().contains("cmd1"));
        assertFalse(history.getHistory().contains("cmd2"));
        assertEquals("cmd3", history.getHistory().get(0));
        assertEquals("cmd12", history.getHistory().get(9));
        assertTrue(history.getHistory().contains("cmd12"));
    }

    @Test
    public void addCommand_nullCommand_throwsNullPointerException() {
        CommandHistory history = new CommandHistory();
        assertThrows(NullPointerException.class, () -> history.addCommand(null));
    }

    @Test
    public void addCommand_emptyCommand_notAdded() {
        CommandHistory history = new CommandHistory();
        history.addCommand("");
        assertEquals(0, history.getHistory().size());

        history.addCommand("   ");
        assertEquals(0, history.getHistory().size());
    }

    @Test
    public void addCommand_validCommand_success() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        assertEquals(1, history.getHistory().size());
        assertEquals("list", history.getHistory().get(0));

        history.addCommand("help");
        assertEquals(2, history.getHistory().size());
        assertEquals("help", history.getHistory().get(1));
    }

    @Test
    public void addCommand_consecutiveDuplicate_notAdded() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        history.addCommand("list");
        assertEquals(1, history.getHistory().size());

        history.addCommand("help");
        history.addCommand("help");
        assertEquals(2, history.getHistory().size());
    }

    @Test
    public void addCommand_nonConsecutiveDuplicate_added() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        history.addCommand("help");
        history.addCommand("list");
        assertEquals(3, history.getHistory().size());
        assertEquals("list", history.getHistory().get(0));
        assertEquals("help", history.getHistory().get(1));
        assertEquals("list", history.getHistory().get(2));
    }

    @Test
    public void addCommand_exceedsLimit_removesOldest() {
        CommandHistory history = new CommandHistory();
        for (int i = 1; i <= 12; i++) {
            history.addCommand("cmd" + i);
        }

        // Verify size is limited to 10
        assertEquals(10, history.getHistory().size());

        // Verify behavior: oldest commands (cmd1, cmd2) are removed
        assertFalse(history.getHistory().contains("cmd1"));
        assertFalse(history.getHistory().contains("cmd2"));
        assertEquals("cmd3", history.getHistory().get(0));
        assertEquals("cmd12", history.getHistory().get(9));
    }

    @Test
    public void getPreviousCommand_emptyHistory_returnsEmpty() {
        CommandHistory history = new CommandHistory();
        Optional<String> command = history.getPreviousCommand();
        assertFalse(command.isPresent());
    }

    @Test
    public void getPreviousCommand_success() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        history.addCommand("help");
        history.addCommand("exit");

        Optional<String> cmd1 = history.getPreviousCommand();
        assertTrue(cmd1.isPresent());
        assertEquals("exit", cmd1.get());

        Optional<String> cmd2 = history.getPreviousCommand();
        assertTrue(cmd2.isPresent());
        assertEquals("help", cmd2.get());

        Optional<String> cmd3 = history.getPreviousCommand();
        assertTrue(cmd3.isPresent());
        assertEquals("list", cmd3.get());
    }

    @Test
    public void getPreviousCommand_atStart_returnsEmpty() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        history.addCommand("help");

        history.getPreviousCommand();
        history.getPreviousCommand();

        // Already at the beginning
        Optional<String> command = history.getPreviousCommand();
        assertFalse(command.isPresent());
    }

    @Test
    public void getNextCommand_emptyHistory_returnsEmpty() {
        CommandHistory history = new CommandHistory();
        Optional<String> command = history.getNextCommand();
        assertFalse(command.isPresent());
    }

    @Test
    public void getNextCommand_success() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        history.addCommand("help");
        history.addCommand("exit");

        // Navigate to the beginning
        history.getPreviousCommand();
        history.getPreviousCommand();
        history.getPreviousCommand();

        // Now navigate forward
        Optional<String> cmd1 = history.getNextCommand();
        assertTrue(cmd1.isPresent());
        assertEquals("help", cmd1.get());

        Optional<String> cmd2 = history.getNextCommand();
        assertTrue(cmd2.isPresent());
        assertEquals("exit", cmd2.get());
    }

    @Test
    public void getNextCommand_atEnd_returnsEmpty() {
        CommandHistory history = new CommandHistory();
        history.addCommand("list");
        history.addCommand("help");

        // Already at the end (no navigation has occurred)
        Optional<String> command = history.getNextCommand();
        assertFalse(command.isPresent());
    }

    @Test
    public void navigationCycle_success() {
        CommandHistory history = new CommandHistory();
        history.addCommand("cmd1");
        history.addCommand("cmd2");
        history.addCommand("cmd3");

        // Navigate up
        assertEquals("cmd3", history.getPreviousCommand().get());
        assertEquals("cmd2", history.getPreviousCommand().get());
        assertEquals("cmd1", history.getPreviousCommand().get());

        // Try to go further up
        assertFalse(history.getPreviousCommand().isPresent());

        // Navigate down
        assertEquals("cmd2", history.getNextCommand().get());
        assertEquals("cmd3", history.getNextCommand().get());

        // Try to go further down
        assertFalse(history.getNextCommand().isPresent());
    }
}
