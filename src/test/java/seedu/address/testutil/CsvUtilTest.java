package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opencsv.exceptions.CsvValidationException;

import seedu.address.commons.util.CsvUtil;
import seedu.address.model.faculty.Faculty;
import seedu.address.model.favorite.Favorite;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

class CsvUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void writeAndReadContacts_success() throws IOException, CsvValidationException {
        Person person1 = new Person(
                new Name("Alice"),
                new Phone("12345678"),
                new Email("alice@example.com"),
                new Address("123 Wonderland St"),
                Set.of(new Tag("friend"), new Tag("colleague")),
                Set.of(new Faculty("Engineering")),
                Favorite.DEFAULT_NOT_FAVORITE
        );

        Person person2 = new Person(
                new Name("Bob"),
                new Phone("87654321"),
                new Email("bob@example.com"),
                new Address("456 Nowhere Rd"),
                Set.of(new Tag("family")),
                Set.of(new Faculty("Science"), new Faculty("Arts")),
                Favorite.DEFAULT_NOT_FAVORITE
        );

        List<Person> contacts = List.of(person1, person2);

        Path csvFile = tempDir.resolve("contacts.csv");
        CsvUtil.writeContactsToCsv(csvFile, contacts);

        assertTrue(Files.exists(csvFile));

        List<Person> readContacts = CsvUtil.readContactsFromCsv(csvFile);

        assertEquals(contacts.size(), readContacts.size());

        Person readPerson1 = readContacts.get(0);
        Person readPerson2 = readContacts.get(1);

        assertEquals(person1.getName(), readPerson1.getName());
        assertEquals(person1.getPhone(), readPerson1.getPhone());
        assertEquals(person1.getEmail(), readPerson1.getEmail());
        assertEquals(person1.getAddress(), readPerson1.getAddress());
        assertEquals(person1.getTags(), readPerson1.getTags());
        assertEquals(person1.getFaculties(), readPerson1.getFaculties());

        assertEquals(person2.getName(), readPerson2.getName());
        assertEquals(person2.getPhone(), readPerson2.getPhone());
        assertEquals(person2.getEmail(), readPerson2.getEmail());
        assertEquals(person2.getAddress(), readPerson2.getAddress());
        assertEquals(person2.getTags(), readPerson2.getTags());
        assertEquals(person2.getFaculties(), readPerson2.getFaculties());
    }

    @Test
    void readContacts_emptyFile_returnsEmptyList() throws IOException, CsvValidationException {
        Path emptyCsv = tempDir.resolve("empty.csv");
        Files.createFile(emptyCsv);

        List<Person> contacts = CsvUtil.readContactsFromCsv(emptyCsv);
        assertTrue(contacts.isEmpty());
    }
}
