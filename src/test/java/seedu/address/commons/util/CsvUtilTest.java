package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opencsv.exceptions.CsvValidationException;

import seedu.address.model.faculty.Faculty;
import seedu.address.model.favorite.Favorite;
import seedu.address.model.module.Module;
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
                Set.of(new Module("CS2103T")),
                Set.of(new Faculty("Engineering")),
                Favorite.DEFAULT_NOT_FAVORITE
        );

        Person person2 = new Person(
                new Name("Bob"),
                new Phone("87654321"),
                new Email("bob@example.com"),
                new Address("456 Nowhere Rd"),
                Set.of(new Tag("family")),
                Set.of(new Module("MA1521")),
                Set.of(new Faculty("Science"), new Faculty("Arts")),
                Favorite.DEFAULT_NOT_FAVORITE
        );


        List<Person> contacts = List.of(person1, person2);

        Path csvFile = tempDir.resolve("contacts.csv");
        CsvUtil.writeContactsToCsv(csvFile, contacts);

        assertTrue(Files.exists(csvFile));

        CsvResult result = CsvUtil.readContactsFromCsv(csvFile);
        List<Person> readContacts = result.getValidContacts();

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

        CsvResult result = CsvUtil.readContactsFromCsv(emptyCsv);
        List<Person> contacts = result.getValidContacts();
        assertTrue(contacts.isEmpty());
    }

    @Test
    public void readContactsFromCsv_emptyOptionalFields_success()
            throws IOException, CsvValidationException {

        // ✅ Prepare a temporary CSV file
        Path csvFile = tempDir.resolve("test.csv");
        String csvContent = String.join(System.lineSeparator(),
                "Name,Phone Number,Email,Address,Tags,Modules,Faculties,Favorites",
                "Alice Pauline,94351253,alice@example.com,123 Jurong West Ave 6,,,," // <-- optional fields empty
        );
        Files.writeString(csvFile, csvContent);

        // ✅ Act: Read CSV file
        CsvResult result = CsvUtil.readContactsFromCsv(csvFile);
        List<Person> contacts = result.getValidContacts();

        // ✅ Assert: Only one person read successfully
        assertEquals(1, contacts.size());
        Person alice = contacts.get(0);
        assertNotNull(alice);

        // Check required fields
        assertEquals("Alice Pauline", alice.getName().fullName);
        assertEquals("94351253", alice.getPhone().value);
        assertEquals("alice@example.com", alice.getEmail().value);
        assertEquals("123 Jurong West Ave 6", alice.getAddress().value);

        // Check optional fields are empty
        assertEquals(0, alice.getTags().size());
        assertEquals(0, alice.getModules().size());
        assertEquals(0, alice.getFaculties().size());

        // Check favorite parsed correctly (empty → false)
        assertFalse(alice.getFavorite().getIsFavorite());
    }
}
