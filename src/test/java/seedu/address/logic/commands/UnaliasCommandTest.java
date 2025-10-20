// package seedu.address.logic.commands;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
// import static seedu.address.testutil.Assert.assertThrows;

// import java.util.Map;

// import org.junit.jupiter.api.Test;

// import seedu.address.model.UserPrefs;

// public class UnaliasCommandTest {

//     private class ModelStubWithAlias extends CommandTestUtil.ModelStub {
//         private final UserPrefs userPrefs = new UserPrefs();

//         @Override
//         public void addAlias(String alias, String commandString) {
//             userPrefs.addAlias(alias, commandString);
//         }

//         @Override
//         public boolean removeAlias(String alias) {
//             return userPrefs.removeAlias(alias) != null;
//         }

//         @Override
//         public Map<String, String> getCommandAliases() {
//             return userPrefs.getCommandAliases();
//         }
//     }

//     @Test
//     public void constructor_nullAlias_throwsNullPointerException() {
//         assertThrows(NullPointerException.class, () -> new UnaliasCommand(null));
//     }

//     @Test
//     public void execute_aliasExists_removeSuccessful() throws Exception {
//         ModelStubWithAlias modelStub = new ModelStubWithAlias();
//         String aliasName = "la";
//         String commandString = "list";
//         modelStub.addAlias(aliasName, commandString);

//         CommandResult commandResult = new UnaliasCommand(aliasName).execute(modelStub);

//         assertEquals(String.format(UnaliasCommand.MESSAGE_SUCCESS, aliasName),
//                 commandResult.getFeedbackToUser());

//         Map<String, String> aliases = modelStub.getCommandAliases();
//         assertFalse(aliases.containsKey(aliasName));
//     }

//     @Test
//     public void execute_aliasNotExists_throwsCommandException() {
//         ModelStubWithAlias modelStub = new ModelStubWithAlias();
//         String aliasName = "nonexistent";
//         UnaliasCommand unaliasCommand = new UnaliasCommand(aliasName);

//         String expectedMessage = String.format(UnaliasCommand.MESSAGE_ALIAS_NOT_FOUND, aliasName);

//         assertCommandFailure(unaliasCommand, modelStub, expectedMessage);
//     }

//     @Test
//     public void equals() {
//         UnaliasCommand unaliasLa = new UnaliasCommand("la");
//         UnaliasCommand unaliasF = new UnaliasCommand("f");

//         assertTrue(unaliasLa.equals(unaliasLa));

//         UnaliasCommand unaliasLaCopy = new UnaliasCommand("la");
//         assertTrue(unaliasLa.equals(unaliasLaCopy));

//         assertFalse(unaliasLa.equals(1));

//         assertFalse(unaliasLa.equals(null));

//         assertFalse(unaliasLa.equals(unaliasF));
//     }
// }
