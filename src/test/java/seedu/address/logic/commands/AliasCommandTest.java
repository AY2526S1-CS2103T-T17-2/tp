package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class AliasCommandTest {
    private class ModelStubWithAlias extends ModelManager {
        private final UserPrefs userPrefs = new UserPrefs();

        @Override
        public void addAlias(String alias, String commandString) {
            userPrefs.addAlias(alias, commandString);
        }

        @Override
        public Map<String, String> getCommandAliases() {
            return userPrefs.getCommandAliases();
        }
    }

    @Test
    public void constructor_nullAlias_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AliasCommand(null, "list"));
    }

    @Test
    public void constructor_nullCommand_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AliasCommand("la", null));
    }

    @Test
    public void execute_aliasAcceptedByModel_addSuccessful() throws Exception {
        ModelStubWithAlias modelStub = new ModelStubWithAlias();
        String aliasName = "la";
        String commandString = "list";

        CommandResult commandResult = new AliasCommand(aliasName, commandString).execute(modelStub);

        assertEquals(String.format(AliasCommand.MESSAGE_SUCCESS, aliasName, commandString),
                commandResult.getFeedbackToUser());
        Map<String, String> aliases = modelStub.getCommandAliases();
        assertTrue(aliases.containsKey(aliasName));
        assertEquals(commandString, aliases.get(aliasName));
    }

    @Test
    public void execute_aliasIsCommand_throwsCommandException() {
        Model model = new ModelManager();
        AliasCommand aliasCommand = new AliasCommand("list", "find");
        assertThrows(CommandException.class, String.format(AliasCommand.MESSAGE_ALIAS_IS_COMMAND, "list"), () -> aliasCommand.execute(model));
    }

    @Test
    public void execute_chainedAlias_throwsCommandException() {
        Model model = new ModelManager();
        model.addAlias("l", "list");
        AliasCommand aliasCommand = new AliasCommand("la", "l");
        assertThrows(CommandException.class, String.format(AliasCommand.MESSAGE_CHAINED_ALIAS_NOT_ALLOWED, "l"), () -> aliasCommand.execute(model));
    }

    @Test
    public void execute_commandNotFound_throwsCommandException() {
        Model model = new ModelManager();
        AliasCommand aliasCommand = new AliasCommand("p", "play");
        assertThrows(CommandException.class, String.format(AliasCommand.MESSAGE_COMMAND_NOT_FOUND, "play"), () -> aliasCommand.execute(model));
    }


    @Test
    public void equals() {
        AliasCommand aliasLaList = new AliasCommand("la", "list");
        AliasCommand aliasFfind = new AliasCommand("f", "find");

        // same object -> returns true
        assertTrue(aliasLaList.equals(aliasLaList));

        // same values -> returns true
        AliasCommand aliasLaListCopy = new AliasCommand("la", "list");
        assertTrue(aliasLaList.equals(aliasLaListCopy));

        // different types -> returns false
        assertFalse(aliasLaList.equals(1));

        // null -> returns false
        assertFalse(aliasLaList.equals(null));

        // different alias -> returns false
        assertFalse(aliasLaList.equals(aliasFfind));
    }
}
