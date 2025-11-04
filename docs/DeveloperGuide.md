---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CampusBook Developer Guide

<!-- * Table of Contents -->
## **Table of Contents**
1. [Acknowledgements](#acknowledgements)
2. [Setting Up, Getting Started](#setting-up-getting-started)
3. [Design](#design)
4. [Implementation](#implementation)
5. [Documentation, Logging, Testing, Configuration and Dev-Ops Guides](#documentation-logging-testing-configuration-dev-ops)
6. [Appendix: Requirements](#appendix-requirements)
7. [Appendix: Instructions for manual testing](#appendix-instructions-for-manual-testing)
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements** {#acknowledgements}

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

AI Usage Declaration: I, Nicholas Lim ZiXian, used ChatGPT, after writing the required code myself, to generate alternative implementations, compare, and use that experience to improve my coding skills. I also used ChatGPT to help with troubleshooting/debugging.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started** {#setting-up-getting-started}

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design** {#design}

<!-- * Table of Contents -->
### Table of Contents
1. [Architecture](#architecture)
2. [UI Component](#ui-component)
3. [Logic Component](#logic-component)
4. [Model Component](#model-component)
5. [Storage Component](#storage-component)
6. [Common Classes](#common-classes)

--------------------------------------------------------------------------------------------------------------------

### Architecture {#architecture}

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

--------------------------------------------------------------------------------------------------------------------

### UI component {#ui-component}

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter`, `ContactDetailsPanel` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

--------------------------------------------------------------------------------------------------------------------

### Logic component {#logic-component}

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

--------------------------------------------------------------------------------------------------------------------

### Model component {#model-component}
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

--------------------------------------------------------------------------------------------------------------------

### Storage component {#storage-component}

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

--------------------------------------------------------------------------------------------------------------------

### Common classes {#common-classes}

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation** {#implementation}

This section describes some noteworthy details on how certain features are implemented.

---
## Alias Feature

The command alias mechanism allows users to create shortcuts for common commands (e.g., `la` as an alias for `list`). This feature is designed to be decoupled and testable, avoiding direct dependencies between the parser and the data model.

The implementation involves the following key components:

* **`Model` Component**: Alias data (a `Map<String, String>`) is persisted in `UserPrefs`, which is managed by the `Model`. `ModelManager` is responsible for the concrete logic of reading and writing aliases and implements the `AliasProvider` interface.

* **`Logic` Component**: `AliasCommand` and `UnaliasCommand` call the `Model`'s interface to add or remove aliases. `LogicManager` saves the updated `UserPrefs`. `AddressBookParser` holds a reference to an `AliasProvider` (injected by `LogicManager`) to perform alias substitution.

The following object diagram shows how `AddressBookParser` depends on the `AliasProvider` interface, which is implemented by `ModelManager`. This decouples the parser from the concrete `Model` implementation.

<puml src="diagrams/AliasProviderClassDiagram.puml" width="250" />

Design Considerations for Decoupling `AddressBookParser` from `Model`:

* **Problem:** The `AddressBookParser` needs access to the alias list (stored in `Model`) to perform substitution, but a direct dependency would violate architectural principles and complicate testing.

* **Alternative 1: Direct dependency on `Model`**: This is simple to implement initially. However, it violates the Dependency Inversion Principle, tightly couples `AddressBookParser` to the `Model`, and makes unit testing difficult (`AddressBookParserTest` would need a complete mocked `Model`).

* **Alternative 2 (Current Choice): Use an `AliasProvider` interface**: An `AliasProvider` interface is defined in the `logic.parser` package, (exposing `getCommandAliases()`). `ModelManager` implements this interface. `LogicManager` constructs `AddressBookParser` by injecting `ModelManager` as the `AliasProvider` type. This achieves
**Decoupling** (parser only depends on the abstraction) and **Testability** (a simple stub can be injected for tests).

## Import/export feature

The current import/export mechanism allows users to save and load contacts from CSV files. This is facilitated by the `CsvUtil` class, which handles serialization and deserialization between the AddressBook and .csv files.

The feature introduces two new commands:

* `ImportCommand`: Imports person data from a specified CSV file into the AddressBook.

* `ExportCommand`: Exports the current AddressBook data into a specified CSV file.

Internally, the ImportCommand and ExportCommand interact with the Model component through the following operations:

* `ImportCommand#execute(Path filePath)` Reads and merges contact data from the given CSV file into the current AddressBook.

* `ExportCommand#execute()` Writes the current AddressBook data into the default destination.

The import/export logic is encapsulated within CsvUtil, which defines:

* `CsvUtil#readContactsFromCsv(Path csvPath)` — Parses the CSV file into a list of Person objects.

* `CsvUtil#writeContactsToCsv(Path csvPath, List<Person> contacts)` — Converts the contact's data into a CSV representation and writes it to the file.

Given below is an example usage scenario and how the import/export mechanism behaves at each step.

Step 1. The user executes `import myData.csv` command to import their contacts from the `myData` file into the address book. This will add on the imported contacts into their already existing ones.

The following sequence diagram shows how an import operation goes through the `Logic` component:

<puml src="diagrams/ImportSequenceDiagram-Logic.puml" alt="ImportSequenceDiagram-Logic" />

Step 2. The user executes `export` command to export every contact in the address book.

The following sequence diagram shows how an export operation goes through the `Logic` component:

<puml src="diagrams/ExportSequenceDiagram-Logic.puml" alt="ExportSequenceDiagram-Logic" />






The following activity diagram summarizes what happens when a user executes an `import` command

<puml src="diagrams/ImportActivityDiagram.puml" width="250" />

--------------------------------------------------------------------------------------------------------------------

## Favorite/Unfavorite feature

The favorite/unfavorite feature allows users to mark contacts as favorites and sort them to the top of the contact list. This is implemented through the `FavCommand` and `UnfavCommand` classes.

The feature introduces two new commands:

* `FavCommand`: Marks a person as favorite based on their index in the displayed list.
* `UnfavCommand`: Removes the favorite status from a person based on their index in the displayed list.

The favorite status is stored as part of the `Person` object through a `Favorite` field, which is:
* Persisted in the JSON storage file (`JsonAdaptedPerson`)
* Used for sorting the contact list (favorites appear first)
* Displayed in the UI with a star icon (★)

Key components involved:

* `FavCommand#execute(Model model)` — Marks the person at the specified index as favorite
* `UnfavCommand#execute(Model model)` — Removes the favorite status from the person at the specified index
* `ModelManager#setPerson(Person target, Person editedPerson)` — Updates the person in the address book
* `ModelManager#updateFilteredPersonList(Predicate predicate)` — Refreshes the displayed list
* `ModelManager#sortPersons()` — Sorts persons with favorites at the top
* `JsonAdaptedPerson` — Serializes/deserializes the `favorite` field

Given below is an example usage scenario and how the favorite/unfavorite mechanism behaves at each step.

Step 1. The user views their contact list and wants to mark contact #1 as a favorite.

Step 2. The user executes `fav 1` command to mark the first person in the displayed list as favorite.

The following sequence diagram shows how a fav operation goes through the `Logic` component:

<puml src="diagrams/FavSequenceDiagram-Logic.puml" alt="FavSequenceDiagram-Logic" />

Step 3. The `FavCommand` retrieves the person at index 1 from the filtered list. A new `Person` object is created with the same details but with `favorite` set to `true`.

Step 4. The `Model` updates the person in the address book via `setPerson()`, which automatically triggers a re-sort, moving favorites to the top.

Step 5. The UI refreshes to show the updated list with the favorited contact at the top, marked with a star (★).

Step 6. The changes are automatically saved to the JSON storage file.

#### Design Considerations

**Aspect: How to store favorite status:**

* **Alternative 1 (current choice):** Store as a field in the `Person` class.
  * Pros: Simple to implement, easy to persist, integrates naturally with existing edit operations.
  * Cons: Adds another field to every person object.

* **Alternative 2:** Maintain a separate list of favorite person IDs.
  * Pros: Potentially more memory efficient if few contacts are favorited.
  * Cons: More complex to maintain consistency, harder to persist, requires additional data structure.

**Aspect: How to sort favorites:**

* **Alternative 1 (current choice):** Automatic sorting in `ModelManager` when list is updated.
  * Pros: Ensures favorites are always at the top, consistent user experience.
  * Cons: Performance impact on every list update.

* **Alternative 2:** Manual sorting only when user requests.
  * Pros: Better performance for large contact lists.
  * Cons: Inconsistent display, user needs to remember to sort.

--------------------------------------------------------------------------------------------------------------------

## Command History feature

The command history feature allows users to navigate through previously entered commands using arrow keys. The history is persisted across application sessions.

The command history mechanism is facilitated by the `CommandHistory` class, which stores up to 10 recent commands and provides navigation capabilities.

Key components involved:

* `CommandHistory#addCommand(String command)` — Adds a new command to the history
* `CommandHistory#getPreviousCommand()` — Retrieves the previous command in the history
* `CommandHistory#getNextCommand()` — Retrieves the next command in the history
* `CommandBox#handleUpArrow()` — Navigates to the previous command
* `CommandBox#handleDownArrow()` — Navigates to the next command
* `CommandHistoryStorage` — Interface for reading/writing command history
* `JsonCommandHistoryStorage` — JSON-based implementation of command history storage
* `JsonSerializableCommandHistory` — JSON-friendly wrapper for command history data

The implementation has the following key features:

* **Limited Size:** The history stores a maximum of 10 commands. When this limit is reached, the oldest command is removed (FIFO queue).
* **Smart Filtering:** Empty or whitespace-only commands are not added to history. Consecutive duplicate commands are not added (non-consecutive duplicates are allowed).
* **Persistence:** The command history is saved to `commandhistory.json` and loaded on application startup.
* **Navigation State:** The `CommandHistory` maintains a `currentPosition` pointer for navigation. Up arrow moves backward through history (decrements position), down arrow moves forward through history (increments position), and at the end of history, the original input is restored.

Given below is an example usage scenario and how the command history mechanism behaves at each step.

Step 1. The user launches the application. `LogicManager` loads the command history from `commandhistory.json`.

Step 2. The user executes several commands: `list`, `find Alice`, `fav 1`.

Step 3. Each successful command is added to the `CommandHistory` via `CommandHistory#addCommand()`.

Step 4. The user starts typing a new command but wants to reference a previous one.

Step 5. The user presses the ↑ (up arrow) key in the command box.

The following sequence diagram shows how command history navigation works when the user presses the Up Arrow key:

<puml src="diagrams/CommandHistorySequenceDiagram.puml" alt="CommandHistorySequenceDiagram" />

The `CommandBox#handleUpArrow()` method is triggered, which:
   * Saves the current input as temporary input (if not already saved)
   * Retrieves the command history from `Logic`
   * Calls `CommandHistory#getPreviousCommand()`
   * Displays the previous command (`fav 1`) if present

Step 6. The user presses ↑ again to see `find Alice`, then ↑ again to see `list`.

Step 7. The user presses ↓ (down arrow) to navigate forward through the history.

Step 8. When reaching the end of the history, the command box restores the original input that was saved in Step 5.

Step 9. When the user exits the application, the command history is saved to `commandhistory.json` via `Storage#saveCommandHistory()`.

#### Design Considerations

**Aspect: History size limit:**

* **Alternative 1 (current choice):** Fixed limit of 10 commands.
  * Pros: Bounded memory usage, predictable behavior, sufficient for most use cases.
  * Cons: Very active users may want more history.

* **Alternative 2:** Unlimited history per session, cleared on exit.
  * Pros: No arbitrary limit during session.
  * Cons: Potential memory issues with very long sessions, loses history on restart.

**Aspect: History persistence:**

* **Alternative 1 (current choice):** Persist history across sessions.
  * Pros: Convenient for users, maintains context across work sessions.
  * Cons: Requires additional storage file, slightly more complex implementation.

* **Alternative 2:** Clear history on application exit.
  * Pros: Simpler implementation, no storage needed, privacy benefit.
  * Cons: Users lose convenient access to previous commands.

--------------------------------------------------------------------------------------------------------------------

## Command Autocomplete feature

The command autocomplete feature allows users to quickly complete commands and file paths by pressing the TAB key. This improves typing efficiency and reduces errors.

The autocomplete mechanism is facilitated by the `CommandBox` UI component, which intercepts TAB key presses and provides suggestions based on the current input.

Key components involved:

* `CommandBox#handleTab()` — Handles TAB key press and performs autocompletion
* `CommandBox#getCommandSuggestions(String input)` — Returns list of matching commands
* `CommandBox#getFileSuggestions(String input)` — Returns list of matching files (for `import` command)
* `LogicManager.ALL_COMMANDS` — Static array containing all available command words for autocompletion

The implementation supports the following:

* **Command Word Completion:** When the user types part of a command word and presses TAB, if there's a single match, it completes the command word automatically. If there are multiple matches, it shows a list of possible completions.
* **File Path Completion:** For the `import` command specifically, it detects CSV files in the user's Downloads folder and suggests matching file names based on the current input, supporting both absolute paths and filenames.
* **Smart Matching:** The autocomplete uses prefix matching to find relevant suggestions.

Given below are example usage scenarios:

**Scenario 1: Command Completion**

Step 1. The user types `fi` in the command box.

Step 2. The user presses TAB.

The following sequence diagram shows how command autocomplete works:

<puml src="diagrams/AutocompleteSequenceDiagram.puml" alt="AutocompleteSequenceDiagram" />

The `CommandBox#handleTab()` method is triggered, which calls `autocomplete("fi")` to search through `BUILT_IN_COMMANDS` (which references `LogicManager.ALL_COMMANDS`).

Step 3. Since only `find` matches, it automatically completes to `find ` and positions the cursor at the end.

**Scenario 2: Multiple Matches**

Step 1. The user types `a` in the command box.

Step 2. The user presses TAB.

Step 3. Multiple commands match (`add`, `alias`).

Step 4. The system displays a list of matching commands in the result display.

**Scenario 3: File Path Completion**

Step 1. The user types `import cont` in the command box.

Step 2. The user presses TAB.

Step 3. The system detects this is an `import` command and searches the Downloads folder for CSV files starting with "cont".

Step 4. If `contacts.csv` is found, it completes to `import contacts.csv`.

#### Design Considerations

**Aspect: Scope of autocomplete:**

* **Alternative 1 (current choice):** Autocomplete commands and import file paths only.
  * Pros: Focused on high-value use cases, simpler implementation.
  * Cons: Could be extended to other parameters.

* **Alternative 2:** Autocomplete all command parameters.
  * Pros: More comprehensive assistance.
  * Cons: Complex to implement, may be intrusive for experienced users.

**Aspect: File path search location:**

* **Alternative 1 (current choice):** Search only in Downloads folder.
  * Pros: Matches common user behavior, reduces search scope.
  * Cons: May not work if files are stored elsewhere.

* **Alternative 2:** Search entire file system.
  * Pros: More flexible, finds files anywhere.
  * Cons: Performance issues, security concerns, overwhelming number of matches.

--------------------------------------------------------------------------------------------------------------------

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops** {#documentation-logging-testing-configuration-dev-ops}

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements** {#appendix-requirements}

### Product scope

**Target user profile**:

* is a NUS Student
* has a need to manage a significant number of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage NUS-related contacts faster than a typical mouse/GUI driven app.

--------------------------------------------------------------------------------------------------------------------

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                   | I want to …​                                                      | So that I can…​                                                             |
|----------|-------------------------------------------|-------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `* * *`  | new user                                  | see usage instructions                                            | refer to instructions to learn how to use the various commands                      |
| `* * *`  | new user                                  | select my faculty                                                 | preload faculty admin contacts without having to search on official NUS websites                                              |
| `* * *`  | user                                      | add a new person                                                  | keep a list of contacts of people in school                                 |
| `* * *`  | user                                      | delete a person                                                   | remove entries that I no longer need                                        |
| `* * *`  | user                                      | list contacts                                                     | view all my saved contacts                                                  |
| `* * *`  | user                                      | find a person by name                                             | locate details of persons without having to go through the entire list      |
| `* * *`  | user                                      | tag contacts                                                      | filter and find people by tag                                               |
| `* *`    | user                                      | hide private contact details                                      | minimize chance of someone else seeing them by accident                     |
| `* *`    | user                                      | see logs of changes                                               | review my past edits and restore previous information if I made a mistake   |
| `* *`    | frequent user                             | add favourite contacts and let them be seen at the top of the list                                               | access the most frequently used contacts without finding them                                               |
| `* *`    | returning user                            | have command history navigation                                   | reuse or tweak previous commands quickly                                    |
| `* *`    | neat student                              | color-code tags or contacts                                       | visually distinguish categories easily                                      |
| `*`      | user                                      | see the last update time of a contact                             | know how recent the information is                                          |
| `*`      | user with many people in the address book | sort persons by a certain field                                   | locate a person easily                                                      |
| `*`      |  frequent user                             | use command aliases                                               | create command shortcuts and increase typing efficiency               |
| `*`      | cautious user                             | detect duplicates                                                 | prevent creating multiple entries for the same person                       |
| `*`      | cautious user                             | be able to preview all my changes before saving                   | double-check all my changes                                                 |
| `*`      | efficient user                            | have tab autocomplete                                             | type the commands easily, without having to type the full command manually  |
| `*`      | efficient user                            | be able to edit batches of profiles at the same time              | add the same data to several people at the same time                        |

--------------------------------------------------------------------------------------------------------------------

### Use cases

(For all use cases below, the **System** is the `CampusBook` and the **Actor** is the `user`, unless specified otherwise)

**UC01: Add a person**

Guarantees: Person will only be added into the list if all the required fields are present

**MSS**

1.  User requests to add a new person with required details
2.  CampusBook adds the person to the contact list
3.  CampusBook displays success message

    Use case ends.

**Extensions**

* 1a. The given command is invalid or missing required fields.

    * 1a1. CampusBook shows an error message.

      Use case ends.



**UC02: Delete a person**

Guarantees: Person will only be deleted from the list if the index given is valid

**MSS**

1.  User requests to list persons
2.  CampusBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  CampusBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given command is invalid.

    * 3a1. CampusBook shows an error message.

      Use case resumes at step 2.


**UC03: Edit a person**

Guarantees: Person will only be editted in the list if the index given, and field format is valid (e.g. johnd@example@com is invalid, and person will not be editted)

**MSS**

1.  User requests to list persons
2.  CampusBook shows a list of persons
3.  User requests to edit a specific person in the list
4.  CampusBook edits the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given command is invalid.

    * 3a1. CampusBook shows an error message.

      Use case resumes at step 2.

**UC04: Find a person**

Guarantees: Person will only be found from the list if at least a part of the given name matches at least a part of the name of an existing person's name

**MSS**

1.  User requests to find a person by name
2.  CampusBook searches for matching contacts
3.  CampusBook displays the matching person

    Use case ends.

**Extensions**

* 1a. The given command is invalid.

    * 1a1. CampusBook shows an error message.

      Use case ends.

* 3a. There are no matches for the given name.

    * 3a1. CampusBook shows a message indicating no person matched.

      Use case ends.

* 3b. Multiple persons match the search criteria.

    * 3b1. CampusBook displays all matching persons.

      Use case ends.

**UC05: Clear the entries**

Guarantees: Person entries will be cleared

**MSS**

1.  User requests to list persons
2.  CampusBook shows a list of persons
3.  User requests to clear all entries in the list
4.  CampusBook clears the list

    Use case ends.

**Extensions**

* 2a. The list is empty.
  Use case ends.


**UC06: Tag a person**

Guarantees: Tags will only be added if they follow a valid format (no spaces, alphanumeric, e.g. `friend`, `professor`).

**MSS**

1. User requests to list persons
2. CampusBook shows a list of persons
3. User requests to add a tag to a specific person in the list
4. CampusBook adds the tag to the person

Use case ends.

**Extensions**

* 2a. The list is empty.
  Use case ends.

* 3a. The given tag is invalid (e.g. contains spaces or special characters).
  * 3a1. CampusBook shows an error message.
    Use case resumes at step 2.


**UC07: Mark a person as favorite**

Guarantees: Favorite contacts will always appear at the top of the contact list when listed.

**MSS**

1. User requests to list persons
2. CampusBook shows a list of persons
3. User requests to mark a specific person as favorite using their index
4. CampusBook marks that person as favorite and updates the display order

Use case ends.

**Extensions**

* 2a. The list is empty.
  Use case ends.

* 3a. The given index is invalid.
  * 3a1. CampusBook shows an error message.
    Use case resumes at step 2.

* 4a. The person is already a favorite.
  * 4a1. CampusBook shows a message indicating the person is already marked as favorite.
    Use case ends.


**UC08: Export contacts to CSV**

Guarantees: The exported file will contain all current contacts in a valid CSV format.

**MSS**

1. User requests to export contacts
2. CampusBook retrieves all information and formats the text into a CSV file
3. Downloads the CSV file on the user's computer

Use case ends.

**Extensions**

* 1a. User cancels the export operation*
  * 1a1. CampusBook aborts the export process.
    Use case ends.

* 2a. File cannot be created or written (e.g., invalid path or permission error)
  * 2a1. CampusBook notifies the user that the export failed and provides the error message.


**UC09: Import contacts from CSV**

Guarantees: Only valid contacts will be imported. Invalid rows are skipped with warnings.
Precondition: The imported file is a valid CSV file that follows the format.

**MSS**

1. User requests to import contacts from a CSV file
2. CampusBook validates the file format
3. CampusBook imports all contacts from the file
4. CampusBook displays success message with number of contacts imported

Use case ends.

**Extensions**

* 2a. File is missing or corrupted.
  * 2a1. CampusBook shows an error message.
    Use case ends.

* 3a. File contains invalid contact entries.
  * 3a1. CampusBook skips invalid entries and imports only valid ones.
  * 3a2. CampusBook logs a warning for each skipped entry.
    Use case resumes at step 4.

* 3b. File contains duplicate persons.
  * 3b1. CampusBook skips duplicates.
  * 3b2. CampusBook logs a warning for duplicates.
    Use case resumes at step 4.

**UC10: Select a faculty**

Guarantees: Default administrative contacts for the selected faculty will be preloaded.

**MSS**

1. User requests to select a faculty
2. CampusBook validates the faculty name
3. CampusBook preloads the administrative contacts for the selected faculty

Use case ends.

**Extensions**

* 2a. The given faculty is invalid.
  * 2a1. CampusBook shows an error message and lists valid faculties.
    Use case resumes at step 1.

* 3a. Some contacts already exist in the list.
  * 3a1. CampusBook skips duplicates and logs a warning.
    Use case ends.

**UC11: Navigate command history**

Guarantees: User can navigate and reuse previously executed commands (up to the last 10 commands).

**MSS**

1. User presses the Up Arrow key in the command box.
2. CampusBook loads the most recent command from history into the command box.
3. User presses Up Arrow again.
4. CampusBook loads the previous command from history.
5. User executes the displayed command.

Use case ends.

**Extensions**

* 1a. There is no command history.
  * 1a1. CampusBook does nothing (command box remains unchanged).
    Use case ends.

* 3a. User is at the oldest command and presses Up Arrow.
  * 3a1. CampusBook keeps displaying the oldest command.
    Use case resumes at step 5.

* 3b. User presses Down Arrow instead of Up Arrow.
  * 3b1. CampusBook loads the next command in history (or restores original input if at the end).
    Use case resumes at step 5.

* 5a. User edits the displayed command instead of executing it directly.
  * 5a1. User modifies the command text.
  * 5a2. User executes the modified command.
    Use case ends.

* 5b. User types new text while viewing history.
  * 5b1. CampusBook resets navigation and treats the new text as current input.
    Use case ends.

---

**Notes / Preconditions**

- Command history stores up to 10 most recent commands.
- Empty commands and consecutive duplicate commands are not saved to history.
- Command history is persistent across application sessions (saved to `commandhistory.json`).

---

**UC12: Use command autocomplete**

Guarantees: User can quickly complete command words and file paths using TAB key.

**MSS**

1. User types a partial command word in the command box.
2. User presses the TAB key.
3. CampusBook searches for matching commands.
4. CampusBook finds exactly one match and autocompletes the command word.

Use case ends.

**Extensions**

* 3a. Multiple matches are found.
  * 3a1. CampusBook displays a list of all matching commands in the result display.
  * 3a2. User types more characters to narrow down the match.
  * 3a3. User presses TAB again.
    Use case resumes at step 3.

* 3b. No matches are found.
  * 3b1. CampusBook does nothing (input remains unchanged).
    Use case ends.

* 1a. User is typing an `import` command with a partial file path.
  * 1a1. User presses TAB after typing `import` and part of a filename.
  * 1a2. CampusBook searches for matching CSV files in the Downloads folder.
  * 1a3. If exactly one match is found, CampusBook autocompletes the filename.
  * 1a4. If multiple matches are found, CampusBook displays a list of matching files.
    Use case ends.

---

**Notes / Preconditions**

- Autocomplete works for all command keywords defined in `LogicManager.ALL_COMMANDS`.
- File path autocomplete only works for `import` command and searches the Downloads folder.
- Autocomplete uses prefix matching (case-insensitive for commands).

---

--------------------------------------------------------------------------------------------------------------------

### Non-Functional Requirements

#### Environment & Portability ####
1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.

2.  All features (including seeding NUS contacts and startup message) shall work offline.

#### Performance ####
1. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.

2. Application should respond to user commands within 1 second for typical operations (add, edit, find).

3. The startup motivational message must not delay startup by more than 100ms.

4. Exporting/Importing 2000 contacts should not take more than 3s.

#### Usability & Accessibility ####
1. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

2. The application should be usable by an NUS student who has never used the application before, with guidance from the User Guide.

3. The application should provide the relevant help messages to educate students on how to use the application appropriately.

4. A novice to CLI should be able to use the application, with guidance from the User Guide.

#### Data Quality ####
1. Faculty Admin contacts must be regularly checked by developers, and updated in releases if they are changed by the relevant departments.

2. Course/Module codes must be regularly checked by developers, and updated in releases if they are changed by the relevant departments.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS

* **App**: Used interchangeably with the term *application*, which is a software designed to perform a specific task (e.g. web browsers, games, AB3)

* **Private contact detail**: A contact detail that is not meant to be shared with others

* **AB3**: AddressBook Level 3, the base project we are extending

* **Faculty Admin**: Administrative staff managing a faculty's affairs (e.g. registration, internship, dean's office)

* **Favorite Contact**: A user-marked important contact that can be viewed at the top of the contacts list

* **Seed Contacts**: Predefined contact list (e.g. NUS faculty admins) that can be bulk-loaded into your CampusBook

* **Export/Import**: Functions to save or load contacts from CSV format

* **Command-Line Interface (CLI)**: Text-based user interface where users type commands

* **Motivational Message**: A short text displayed at startup to motivate the student to study harder

* **User Guide**: A comprehensive guide to teach new users how to use the application

* **Release**: A set of changes that updates or adds new functionality to a software product or service

* **Course/Module Code**: A unique code given to a course/module given by NUS, which has a 2 or 3 letter prefix indicating the discipline, followed by four digits, and an optional suffix (e.g. DSA1101, CS2103T, MA2001)

* **Startup**: The act of running the application for the first time after exiting it previously

*  **CSV**: Comma Separated Values, where a CSV file is one commonly used to store tabular data, and individual data elements within a record separated by commas

*  **Command Alias**: A custom shorthand name for a longer, more complex command, file, or function

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing** {#appendix-instructions-for-manual-testing}

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Marking and unmarking favorites

1. Marking a person as favorite

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list. Contact at index 1 is not marked as favorite.

   1. Test case: `fav 1`<br>
      Expected: First contact is marked as favorite. A star (★) appears next to their name. The contact moves to the top of the list. Success message shown in the status message.

   1. Test case: `fav 1` (executed again)<br>
      Expected: Message indicating the person is already marked as favorite.

   1. Test case: `fav 0`<br>
      Expected: No person is marked as favorite. Error details shown in the status message.

   1. Other incorrect fav commands to try: `fav`, `fav x`, `fav -1` (where x is larger than the list size)<br>
      Expected: Error message shown.

1. Unmarking a favorite

   1. Prerequisites: Contact at index 1 is marked as favorite (has a star ★).

   1. Test case: `unfav 1`<br>
      Expected: First contact is unmarked as favorite. The star (★) disappears. The contact may move down in the list. Success message shown in the status message.

   1. Test case: `unfav 1` (executed again on a non-favorite contact)<br>
      Expected: Message indicating the person is not marked as favorite.

   1. Other incorrect unfav commands to try: `unfav`, `unfav 0`, `unfav x` (where x is larger than the list size)<br>
      Expected: Error message shown.

1. Sorting behavior

   1. Prerequisites: Have at least 3 contacts, with contacts named "Alice", "Bob", and "Charlie" (in alphabetical order).

   1. Test case: Mark "Charlie" as favorite using `fav 3`<br>
      Expected: "Charlie" moves to position 1 in the list.

   1. Test case: Mark "Bob" as favorite using `fav 2` (Bob is now at index 2)<br>
      Expected: Both favorites appear at the top, sorted alphabetically: "Bob" at position 1, "Charlie" at position 2.

   1. Test case: Unmark "Bob" using `unfav 1`<br>
      Expected: "Bob" moves below all favorited contacts but above non-favorited contacts in alphabetical order.

### Command history navigation

1. Basic navigation

   1. Prerequisites: Start the app with an existing command history, or execute several commands like `list`, `find Alice`, `fav 1`.

   1. Test case: Click in the command box and press ↑ (Up Arrow)<br>
      Expected: The most recent command appears in the command box.

   1. Test case: Press ↑ again<br>
      Expected: The previous command appears.

   1. Test case: Press ↓ (Down Arrow)<br>
      Expected: The next command in history appears.

   1. Test case: Keep pressing ↑ until at the oldest command, then press ↑ again<br>
      Expected: The command box remains showing the oldest command.

   1. Test case: Keep pressing ↓ until at the newest position, then press ↓ again<br>
      Expected: The command box clears (or shows your original input before navigation started).

1. Temporary input preservation

   1. Prerequisites: Command history contains at least one command.

   1. Test case: Type `find` (but don't execute it), then press ↑<br>
      Expected: Previous command appears, replacing "find".

   1. Test case: Press ↓ to return to the end of history<br>
      Expected: The original text "find" is restored.

1. History persistence

   1. Prerequisites: Execute several commands like `list`, `add n/Test p/12345678 e/test@example.com a/Test Address`.

   1. Test case: Exit the application using `exit`, then relaunch it. Press ↑ in the command box<br>
      Expected: The most recent command from the previous session appears.

   1. Test case: Execute more than 10 commands, then press ↑ repeatedly<br>
      Expected: Only the 10 most recent commands are accessible.

### Command autocomplete

1. Command word autocomplete

   1. Test case: Type `fi` in the command box and press TAB<br>
      Expected: The text autocompletes to `find `.

   1. Test case: Type `a` in the command box and press TAB<br>
      Expected: A list of matching commands (`add`, `alias`) is shown in the result display.

   1. Test case: Type `xyz` and press TAB<br>
      Expected: No autocomplete occurs (no matching commands).

1. File path autocomplete (for import command)

   1. Prerequisites: Have a file named `contacts.csv` in your Downloads folder.

   1. Test case: Type `import cont` and press TAB<br>
      Expected: The text autocompletes to `import contacts.csv`.

   1. Test case: Type `import` and press TAB<br>
      Expected: A list of all CSV files in the Downloads folder is shown.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
