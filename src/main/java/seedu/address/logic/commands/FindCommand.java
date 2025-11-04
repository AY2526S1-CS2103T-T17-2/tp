package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Finds all persons who match all of the specified criteria "
        + "(name, tag, module, and/or faculty) and displays them as a list with index numbers.\n"
        + "Parameters: "
        + "[" + PREFIX_NAME + "NAME_KEYWORD [MORE_KEYWORDS]...] "
        + "[" + PREFIX_TAG + "TAG_KEYWORD [MORE_KEYWORDS]...] "
        + "[" + PREFIX_MODULE + "MODULE_KEYWORD [MORE_KEYWORDS]...] "
        + "[" + PREFIX_FACULTY + "FACULTY_KEYWORD [MORE_KEYWORDS]...]\n"
        + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "alice "
        + PREFIX_MODULE + "CS2103T " + PREFIX_FACULTY + "computing";


    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    public Predicate<Person> getPredicate() {
        return predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int listSize = model.getFilteredPersonList().size();
        return new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, listSize));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
