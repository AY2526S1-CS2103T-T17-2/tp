package seedu.address.commons.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import seedu.address.model.faculty.Faculty;
import seedu.address.model.favorite.Favorite;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Handles CSV utility functions.
 */
public class CsvUtil {
    private static final String[] HEADERS = {"Name", "Phone Number", "Email", "Address", "Tags", "Faculties"};

    /**
     * Reads contacts from a CSV file.
     * @param csvPath path to the CSV file
     * @return list of persons
     * @throws IOException if file access fails
     * @throws CsvValidationException if CSV is invalid
     */
    public static List<Person> readContactsFromCsv(Path csvPath)
            throws IOException, CsvValidationException {
        List<Person> contacts = new ArrayList<>();


        try (Reader r = Files.newBufferedReader(csvPath);
             CSVReader csvReader = new CSVReader(r)) {

            String[] nextLine;

            //Skip the header row
            csvReader.readNext();

            while ((nextLine = csvReader.readNext()) != null) {
                Name name = new Name(nextLine[0].trim());
                Phone phoneNo = new Phone(nextLine[1].trim());
                Email email = new Email(nextLine[2].trim());
                Address address = new Address(nextLine[3]);
                Set<Tag> allTags = parseTags(nextLine[4]);
                Set<Faculty> allFaculties = parseFaculties(nextLine[5]);

                Person newPerson = new Person(name, phoneNo, email, address, allTags, allFaculties,
                        Favorite.DEFAULT_NOT_FAVORITE);

                contacts.add(newPerson);
            }
        }
        return contacts;
    }

    /**
     * Writes a list of {@link Person} objects to a CSV file at the specified path.
     * Each {@code Person} is written with the following fields in order:
     * Name, Phone Number, Email, Address, Tags, Faculties.
     * Tags and Faculties are joined using the pipe character '|'.
     * If the CSV file does not exist, it will be created. If it exists, its contents
     * will be overwritten.
     *
     * @param csvPath  the {@link Path} to the CSV file to write
     * @param contacts the list of {@link Person} objects to write to the CSV
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public static void writeContactsToCsv(Path csvPath, List<Person> contacts) throws IOException {
        try (Writer writer = Files.newBufferedWriter(csvPath);
             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            csvWriter.writeNext(HEADERS);

            for (Person p : contacts) {
                String name = p.getName().toString();
                String phone = p.getPhone().toString();
                String email = p.getEmail().toString();
                String address = p.getAddress().toString();

                String tags = p.getTags().stream()
                        .map(tag -> tag.tagName)
                        .collect(Collectors.joining("|"));

                String faculties = p.getFaculties().stream()
                        .map(Faculty -> Faculty.facultyName)
                        .collect(Collectors.joining("|"));

                String[] line = {name, phone, email, address, tags, faculties};
                csvWriter.writeNext(line);
            }
        }
    }

    /**
     * Helper method to parse a pipe-separated string into a set of {@link Tag}.
     */
    private static Set<Tag> parseTags(String tagString) {
        Set<Tag> allTags = new HashSet<>();
        if (tagString != null && !tagString.isEmpty()) {
            String[] tagParts = tagString.split("\\|");
            for (String t : tagParts) {
                t = t.trim();
                if (!t.isEmpty()) {
                    allTags.add(new Tag(t));
                }
            }
        }
        return allTags;
    }

    /**
     * Helper method to parse a pipe-separated string into a set of {@link Faculty}.
     */
    private static Set<Faculty> parseFaculties(String facultyString) {
        Set<Faculty> allFaculties = new HashSet<>();
        if (facultyString != null && !facultyString.isEmpty()) {
            String[] facultyParts = facultyString.split("\\|");
            for (String t : facultyParts) {
                t = t.trim();
                if (!t.isEmpty()) {
                    allFaculties.add(new Faculty(t));
                }
            }
        }
        return allFaculties;
    }
}

