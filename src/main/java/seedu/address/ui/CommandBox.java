package seedu.address.ui;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.*;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final Logic logic;
    private String temporaryInput = "";

    @FXML
    private TextField commandTextField;

    private ResultDisplay resultDisplay;

    private static final String[] BUILT_IN_COMMANDS = {
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
            ImportCommand.COMMAND_WORD//,
//            AliasCommand.COMMAND_WORD,
//            UnaliasCommand.COMMAND_WORD,
//            ListAliasesCommand.COMMAND_WORD
    };

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.logic = null;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor} and {@code Logic}.
     * This constructor enables command history navigation.
     */
    public CommandBox(CommandExecutor commandExecutor, Logic logic, ResultDisplay resultDisplay) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.logic = logic;
        this.resultDisplay = resultDisplay;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        setupKeyboardNavigation();
    }

    /**
     * Sets up keyboard navigation for command history.
     */
    private void setupKeyboardNavigation() {
        commandTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                handleUpArrow();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                handleDownArrow();
                event.consume();
            } else if (event.getCode() ==  KeyCode.TAB) {
                handleTab();
                event.consume();
            }
        });
    }

    /**
     * Handles the UP arrow key press to navigate to the previous command in history.
     */
    private void handleUpArrow() {
        if (logic == null) {
            return;
        }

        // Save the current input before navigating
        if (temporaryInput.isEmpty()) {
            temporaryInput = commandTextField.getText();
        }

        Optional<String> previousCommand = logic.getCommandHistory().getPreviousCommand();
        if (previousCommand.isPresent()) {
            commandTextField.setText(previousCommand.get());
            commandTextField.positionCaret(commandTextField.getText().length());
        }
    }

    /**
     * Handles the DOWN arrow key press to navigate to the next command in history.
     */
    private void handleDownArrow() {
        if (logic == null) {
            return;
        }

        Optional<String> nextCommand = logic.getCommandHistory().getNextCommand();
        if (nextCommand.isPresent()) {
            commandTextField.setText(nextCommand.get());
            commandTextField.positionCaret(commandTextField.getText().length());
        } else {
            // Reached the end of history, restore the temporary input
            commandTextField.setText(temporaryInput);
            commandTextField.positionCaret(commandTextField.getText().length());
            temporaryInput = "";
        }
    }

    /**
     * Handles the TAB key press to navigate to the next command in history.
     */
    private void handleTab() {
        String currInput = commandTextField.getText();
        String completion = autocomplete(currInput);

        if (completion != null) {
            commandTextField.setText(completion);
            commandTextField.positionCaret(completion.length());
        } else {
            StringBuilder suggestions = new StringBuilder("Possible commands: ");
            for (String cmd : BUILT_IN_COMMANDS) {
                if (cmd.startsWith(currInput)) {
                    suggestions.append(cmd).append(" ");
                }
            }
            if (suggestions.length() > 18) {
                resultDisplay.setFeedbackToUser(suggestions.toString().trim());
            }
        }
    }

    private String autocomplete(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        String lowerInput = input.toLowerCase();
        String match = null;
        int count = 0;

        for (String cmd : BUILT_IN_COMMANDS) {
            if (cmd.startsWith(lowerInput)) {
                match = cmd;
                count++;
            }
        }

        // If exactly one match, return it
        return count == 1 ? match : null;
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
            temporaryInput = ""; // Reset temporary input after command execution
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
