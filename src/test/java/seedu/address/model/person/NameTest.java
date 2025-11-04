package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-allowed special characters
        assertFalse(Name.isValidName("@john")); // starts with non-allowed character

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names

        // valid names with special characters
        assertTrue(Name.isValidName("Ravi Kumar s/o Suresh Kumar")); // contains slash
        assertTrue(Name.isValidName("O'Connor")); // contains apostrophe
        assertTrue(Name.isValidName("José")); // contains accented character
        assertTrue(Name.isValidName("Mary-Jane")); // contains hyphen
        assertTrue(Name.isValidName("Dr. Smith")); // contains period
        assertTrue(Name.isValidName("Jean-Pierre O'Connor")); // multiple special characters

        // names with command prefix-like patterns (without space before prefix)
        // These are valid at the Name validation level, but will cause parsing issues if used in commands
        assertTrue(Name.isValidName("Johnn/Smith")); // prefix-like pattern without space before slash
        assertTrue(Name.isValidName("Alicep/123")); // prefix-like pattern without space
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
