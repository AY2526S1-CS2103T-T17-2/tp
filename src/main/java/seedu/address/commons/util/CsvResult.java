package seedu.address.commons.util;

import java.util.List;

import seedu.address.model.person.Person;

/**
 * Provides a class for CsvUtil return format
 */
public class CsvResult {
    private final List<Person> validContacts;
    private final List<String> errorMessages;

    /**
     * Constructs a {@code CsvResult} with the specified valid contacts and error messages.
     *
     * @param validContacts the list of successfully parsed {@link Person} objects; must not be {@code null}
     * @param errorMessages the list of error messages for invalid or skipped rows; must not be {@code null}
     */
    public CsvResult(List<Person> validContacts, List<String> errorMessages) {
        this.validContacts = validContacts;
        this.errorMessages = errorMessages;
    }

    public List<Person> getValidContacts() {
        return validContacts;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }
}

