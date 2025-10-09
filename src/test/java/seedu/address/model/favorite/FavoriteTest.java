package seedu.address.model.favorite;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FavoriteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Favorite(null));
    }

    @Test
    public void constructor_invalidFavorite_throwsIllegalArgumentException() {
        String invalidFavorite = "";
        assertThrows(IllegalArgumentException.class, () -> new Favorite(invalidFavorite));
    }

    @Test
    public void isValidFavorite() {
        assertThrows(NullPointerException.class, () -> Favorite.isValidFavorite(null));
    }

}
