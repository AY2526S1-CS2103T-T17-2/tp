package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.util.CsvUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;


/**
 * Exports the addressbook into a CSV file
 */
public class ExportCommand extends Command {
    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports the address book to a CSV file.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_EXPORT_ACKNOWLEDGEMENT = "AddressBook exported to CSV file in Downloads folder"
            + " as CampusBook_contacts.csv";
    public static final String FAILED_EXPORT = "Failed to export contacts to CSV";
    public static final String EMPTY_ADDRESSBOOK = "Unable to export due to empty address book";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Path downloadPath = Paths.get(System.getProperty("user.home"), "Downloads",
                                      "CampusBook_contacts.csv");
        List<Person> allContacts = new ArrayList<>(model.getAddressBook().getPersonList());

        if (allContacts.isEmpty()) {
            return new CommandResult(EMPTY_ADDRESSBOOK);
        }
        try {
            CsvUtil.writeContactsToCsv(downloadPath, allContacts);
        } catch (IOException e) {
            throw new CommandException(FAILED_EXPORT);
        }
        return new CommandResult(MESSAGE_EXPORT_ACKNOWLEDGEMENT);
    }
}
