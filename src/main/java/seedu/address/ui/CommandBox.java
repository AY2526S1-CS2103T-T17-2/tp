package seedu.address.ui;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
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
    public CommandBox(CommandExecutor commandExecutor, Logic logic) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.logic = logic;
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
