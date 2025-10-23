package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class ListAliasesCommandTest {

    @Test
    public void execute_noAliases_showsNoAliasesMessage() {
        Model model = new ModelManager();
        CommandResult commandResult = new ListAliasesCommand().execute(model);
        assertEquals(ListAliasesCommand.MESSAGE_NO_ALIASES, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_aliasesExist_showsAliases() {
        Model model = new ModelManager();
        model.addAlias("la", "list");
        model.addAlias("f", "find");

        CommandResult commandResult = new ListAliasesCommand().execute(model);
        String actualFeedback = commandResult.getFeedbackToUser();

        // Check contains, as HashMap order is not guaranteed
        assertTrue(actualFeedback.contains("f -> find"));
        assertTrue(actualFeedback.contains("la -> list"));
        assertTrue(actualFeedback.startsWith("Defined aliases:"));
    }
}
