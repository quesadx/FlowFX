package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.Initializable;

/**
 * Minimal placeholder controller for the Add Project view.
 *
 * <p>
 * The original project contained an empty file for this controller. To avoid
 * broken references from FXML or other code, this lightweight implementation
 * provides the required lifecycle hooks and a logger. Keep logic minimal —
 * real behavior should be implemented when the view and requirements are known.
 * </p>
 */
public class AddProject extends Controller implements Initializable {

    private static final Logger LOGGER =
        Logger.getLogger(AddProject.class.getName());

    /**
     * FXMLLoader initialization entry point (URL + ResourceBundle).
     *
     * @param url the location used to resolve relative paths for the root object,
     *            or null if unknown
     * @param rb  the resources used to localize the root object, or null if none
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.fine("initialize(URL, ResourceBundle) invoked for AddProject");
        // No-op by default. UI wiring should be implemented here when needed.
    }

    /**
     * Secondary initialization hook used by this application's controller pattern.
     * Subclasses / callers may call this to perform view-specific setup.
     */
    @Override
    public void initialize() {
        LOGGER.fine("initialize() invoked for AddProject");
        // Intentionally left blank; implement view setup here when required.
    }
}
