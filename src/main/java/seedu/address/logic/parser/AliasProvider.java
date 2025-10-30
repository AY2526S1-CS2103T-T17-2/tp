package seedu.address.logic.parser;

import java.util.Map;

/**
 * Interface for providing command aliases.
 */
public interface AliasProvider {
    /**
     * Returns a map of command aliases to their actual commands.
     */
    Map<String, String> getCommandAliases();
}
