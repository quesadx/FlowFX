package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.Initializable;

/**
 * Lightweight placeholder for a reusable UI widget.
 *
 * <p>This class exists to provide a minimal, well-documented implementation
 * so FXML references or other code that expect a `Widget` controller do not
 * break the build. It intentionally contains no business logic; real widget
 * behavior should be added when the corresponding view and requirements are
 * available.</p>
 *
 * <p>The class implements both the framework {@link Initializable} hook and
 * the project's `Controller` secondary initialization method (declared in
 * `Controller`). This mirrors other controllers in the project and keeps the
 * lifecycle consistent.</p>
 */
public class Widget extends Controller implements Initializable {

    private static final Logger LOGGER =
        Logger.getLogger(Widget.class.getName());

    /**
     * FXMLLoader initialization entry point (URL + ResourceBundle).
     *
     * @param url the location used to resolve relative paths for the root object,
     *            or null if unknown
     * @param rb  the resources used to localize the root object, or null if none
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.fine("Widget.initialize(URL, ResourceBundle) invoked");
        // No-op by default. Place FXML wiring here when needed.
    }

    /**
     * Secondary initialization hook used by the project's controller pattern.
     * Callers may invoke this after setting controller state if required.
     */
    @Override
    public void initialize() {
        LOGGER.fine("Widget.initialize() invoked");
        // Intentionally left blank.
    }
}
