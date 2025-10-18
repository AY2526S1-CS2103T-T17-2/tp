package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * An immutable command history that is serializable to JSON format.
 */
@JsonRootName(value = "commandhistory")
class JsonSerializableCommandHistory {

    private final List<String> history;

    /**
     * Constructs a {@code JsonSerializableCommandHistory} with the given history.
     *
     * @throws NullPointerException if history is null (indicates invalid JSON format).
     */
    @JsonCreator
    public JsonSerializableCommandHistory(@JsonProperty("history") List<String> history) {
        requireNonNull(history, "History field is missing in JSON");
        this.history = new ArrayList<>(history);
    }

    /**
     * Converts this command history into a list of command strings.
     *
     * @return A list of command strings.
     */
    public List<String> toModelType() {
        return new ArrayList<>(history);
    }
}

