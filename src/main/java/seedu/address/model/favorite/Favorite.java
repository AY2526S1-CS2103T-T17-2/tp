package seedu.address.model.favorite;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Favorite status in the address book.
 * Guarantees: immutable; status is valid as declared in {@link #isValidFavorite(String)}
 */
public class Favorite {

    public static final String MESSAGE_CONSTRAINTS = "Favorite should be either 'true' or 'false'";
    public static final Favorite DEFAULT_NOT_FAVORITE = new Favorite(false);

    private final boolean isFavorite;

    /**
     * Constructs a {@code Favorite}.
     *
     * @param isFavorite A valid favorite status.
     */
    public Favorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    /**
     * Constructs a {@code Favorite}.
     *
     * @param favoriteString A valid favorite string.
     */
    public Favorite(String favoriteString) {
        requireNonNull(favoriteString);
        checkArgument(isValidFavorite(favoriteString), MESSAGE_CONSTRAINTS);
        this.isFavorite = Boolean.parseBoolean(favoriteString);
    }

    /**
     * Returns true if a given string is a valid favorite status.
     */
    public static boolean isValidFavorite(String test) {
        return test.equals("true") || test.equals("false");
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Favorite)) {
            return false;
        }

        Favorite otherFavorite = (Favorite) other;
        return isFavorite == otherFavorite.isFavorite;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(isFavorite);
    }

    @Override
    public String toString() {
        return Boolean.toString(isFavorite);
    }
}

