package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * Panel on the right side that displays full contact details for a selected
 * Person.
 */
public class ContactDetailsPanel extends UiPart<Region> {

    private static final String FXML = "ContactDetailsPanel.fxml";

    private Person person;

    @FXML
    private VBox detailsContainer;

    @FXML
    private Label name;
    @FXML
    private Label favorite;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private FlowPane faculties;
    @FXML
    private FlowPane modules;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code ContactDetailsPanel} with no person selected.
     */
    public ContactDetailsPanel() {
        super(FXML);
        clear();
    }

    /**
     * Updates the panel to show details for the given {@code Person}.
     */
    public void setPerson(Person person) {
        if (person == null) {
            clear();
            return;
        }

        this.person = person;
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        address.setText(person.getAddress().value);

        // Faculty, Module, Tag labels (sorted, same as PersonCard)
        faculties.getChildren().clear();
        modules.getChildren().clear();
        tags.getChildren().clear();

        person.getFaculties().stream()
                .sorted(Comparator.comparing(faculty -> faculty.facultyName))
                .forEach(faculty -> faculties.getChildren().add(new Label(faculty.facultyName)));

        person.getModules().stream()
                .sorted(Comparator.comparing(module -> module.moduleName))
                .forEach(module -> modules.getChildren().add(new Label(module.moduleName)));

        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        // Favorite indicator (same logic as PersonCard)
        if (person.getFavorite().getIsFavorite()) {
            favorite.setText("★");
            favorite.setVisible(true);
            favorite.setManaged(true);
        } else {
            favorite.setText("");
            favorite.setVisible(false);
            favorite.setManaged(false);
        }
    }

    /**
     * Clears the panel (no person selected).
     */
    public void clear() {
        name.setText("No contact selected");
        favorite.setText("");
        favorite.setVisible(false);
        favorite.setManaged(false);
        phone.setText("-");
        email.setText("-");
        address.setText("-");
        faculties.getChildren().clear();
        modules.getChildren().clear();
        tags.getChildren().clear();
    }
}
