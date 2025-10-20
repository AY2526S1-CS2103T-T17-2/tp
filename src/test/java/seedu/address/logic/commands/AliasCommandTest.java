// package seedu.address.logic.commands;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static seedu.address.testutil.Assert.assertThrows;

// import java.util.Map;

// import org.junit.jupiter.api.Test;

// import seedu.address.model.UserPrefs;

// public class AliasCommandTest {

//     private class ModelStubWithAlias extends CommandTestUtil.ModelStub {
//         private final UserPrefs userPrefs = new UserPrefs();

//         @Override
//         public void addAlias(String alias, String commandString) {
//             userPrefs.addAlias(alias, commandString);
//         }

//         @Override
//         public Map<String, String> getCommandAliases() {
//             return userPrefs.getCommandAliases();
//         }
//     }

//     @Test
//     public void constructor_nullAlias_throwsNullPointerException() {
//         assertThrows(NullPointerException.class, () -> new AliasCommand(null, "list"));
//     }

//     @Test
//     public void constructor_nullCommand_throwsNullPointerException() {
//         assertThrows(NullPointerException.class, () -> new AliasCommand("la", null));
//     }

//     @Test
//     public void execute_aliasAcceptedByModel_addSuccessful() throws Exception {
//         ModelStubWithAlias modelStub = new ModelStubWithAlias();
//         String aliasName = "la";
//         String commandString = "list";

//         CommandResult commandResult = new AliasCommand(aliasName, commandString).execute(modelStub);

//         assertEquals(String.format(AliasCommand.MESSAGE_SUCCESS, aliasName, commandString),
//                 commandResult.getFeedbackToUser());

//         Map<String, String> aliases = modelStub.getCommandAliases();
//         assertTrue(aliases.containsKey(aliasName));
//         assertEquals(commandString, aliases.get(aliasName));
//     }

//     @Test
//     public void equals() {
//         AliasCommand aliasLaList = new AliasCommand("la", "list");
//         AliasCommand aliasFfind = new AliasCommand("f", "find");

//         assertTrue(aliasLaList.equals(aliasLaList));

//         AliasCommand aliasLaListCopy = new AliasCommand("la", "list");
//         assertTrue(aliasLaList.equals(aliasLaListCopy));

//         assertFalse(aliasLaList.equals(1));

//         assertFalse(aliasLaList.equals(null));

//         assertFalse(aliasLaList.equals(aliasFfind));
//     }
// }
