package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.ListAliasesCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.UnaliasCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.AliasProvider;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    public static final String[] ALL_COMMANDS = {
        AddCommand.COMMAND_WORD,
        EditCommand.COMMAND_WORD,
        DeleteCommand.COMMAND_WORD,
        ClearCommand.COMMAND_WORD,
        FindCommand.COMMAND_WORD,
        ListCommand.COMMAND_WORD,
        ExitCommand.COMMAND_WORD,
        HelpCommand.COMMAND_WORD,
        SelectCommand.COMMAND_WORD,
        ExportCommand.COMMAND_WORD,
        ImportCommand.COMMAND_WORD,
        AliasCommand.COMMAND_WORD,
        UnaliasCommand.COMMAND_WORD,
        ListAliasesCommand.COMMAND_WORD
    };

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private final CommandHistory commandHistory;


    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;

        // Pass the model as an AliasProvider to the parser
        addressBookParser = new AddressBookParser((AliasProvider) model);

        // Load command history from storage
        CommandHistory loadedHistory;
        try {
            Optional<List<String>> historyOptional = storage.readCommandHistory();
            loadedHistory = new CommandHistory(historyOptional.orElse(new ArrayList<>()));
        } catch (DataLoadingException e) {
            logger.warning("Command history file could not be loaded. Starting with empty history.");
            loadedHistory = new CommandHistory();
        }
        this.commandHistory = loadedHistory;
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;

        Command command = addressBookParser.parseCommand(commandText);
        commandResult = command.execute(model);

        commandHistory.addCommand(commandText);

        try {
            storage.saveAddressBook(model.getAddressBook());
            storage.saveCommandHistory(commandHistory.getHistory());

            if (command instanceof AliasCommand || command instanceof UnaliasCommand) {
                storage.saveUserPrefs(model.getUserPrefs());
            }

        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public CommandHistory getCommandHistory() {
        return commandHistory;
    }
}
