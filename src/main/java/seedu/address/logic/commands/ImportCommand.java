package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;

import seedu.address.commons.util.CsvUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;


/**
 * Imports a CSV file and loads the contacts into the address book.
 */
public class ImportCommand extends Command {
    public static final String COMMAND_WORD = "import";
    public static final String INVALID_PATH_ERROR = "Path input is not valid";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Imports contacts from a CSV file into the current address book. "
            + "Duplicate entries and invalid data formats will be ignored.\n"
            + "The CSV file must include valid 'name', 'email', 'phone', and 'address' columns.\n"
            + "Parameters: [FILEPATH]\n"
            + "If FILEPATH is:\n"
            + "1. Empty — the app will look for 'Campusbook_contacts.csv' in your Downloads folder.\n"
            + "2. A file name — it will be resolved relative to your Downloads folder.\n"
            + "3. An absolute path — it will use the specified file directly.\n"
            + "Example:\n"
            + "  " + COMMAND_WORD + "\n"
            + "  " + COMMAND_WORD + " contacts.csv\n"
            + "  " + COMMAND_WORD + " C://Users//djsud//Downloads//CampusBook_contacts.csv";


    private Path path;

    /**
     * Constructs an {@code ImportCommand} with the specified CSV file path.
     *
     * @param path the {@link Path} to the CSV file to import; must not be null
     * @throws NullPointerException if {@code path} is null
     */
    public ImportCommand(Path path) {
        requireNonNull(path);
        this.path = path;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> contacts;
        try {
            contacts = CsvUtil.readContactsFromCsv(path);
        } catch (CsvValidationException | IOException e) {
            throw new CommandException(INVALID_PATH_ERROR);
        }
        int addedCount = 0;
        for (Person p : contacts) {
            if (!model.hasPerson(p)) {
                model.addPerson(p);
                addedCount++;
            }
        }
        int skippedCount = contacts.size() - addedCount;

        String message = String.format(
                "Imported %d contact(s). Skipped %d duplicate row(s).",
                addedCount, skippedCount
        );

        return new CommandResult(message);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
