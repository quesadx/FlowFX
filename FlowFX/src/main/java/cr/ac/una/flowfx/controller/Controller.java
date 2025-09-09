package cr.ac.una.flowfx.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Base controller for application views.
 *
 * <p>This abstract class provides common properties and helpers for view
 * controllers such as stage management, view name, action token, and utility
 * methods for event handling.</p>
 */
public abstract class Controller {

    private static final Logger LOGGER = Logger.getLogger(
        Controller.class.getName()
    );

    private Stage stage;
    private String accion;
    private String nombreVista;

    /**
     * Returns the action token associated with this controller.
     *
     * @return action string or null if none set
     */
    public String getAccion() {
        return accion;
    }

    /**
     * Sets an action token for the controller. The token may be used by the
     * controller to determine context-specific behavior.
     *
     * @param accion action string (may be null)
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }

    /**
     * Sets the stage associated with this controller.
     *
     * @param stage the JavaFX stage to associate (may be null)
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Returns the stage associated with this controller.
     *
     * @return stage or null if not set
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Returns the display name for the view managed by this controller.
     *
     * @return view name or null if not set
     */
    public String getNombreVista() {
        return nombreVista;
    }

    /**
     * Sets the display name for the view managed by this controller.
     *
     * @param nombreVista view name (may be null)
     */
    public void setNombreVista(String nombreVista) {
        this.nombreVista = nombreVista;
    }

    /**
     * Sends a synthetic TAB key event to the control that originated the given
     * event. The provided event will be consumed to avoid duplicate handling.
     *
     * @param event the original key event; if null the method is a no-op
     */
    public void sendTabEvent(KeyEvent event) {
        if (event == null) {
            LOGGER.log(Level.FINE, "sendTabEvent called with null event");
            return;
        }

        try {
            event.consume();
            KeyEvent keyEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                null,
                null,
                KeyCode.TAB,
                false,
                false,
                false,
                false
            );

            Object source = event.getSource();
            if (source instanceof Control) {
                ((Control) source).fireEvent(keyEvent);
            } else {
                LOGGER.log(
                    Level.FINE,
                    "sendTabEvent source is not a Control: {0}",
                    source
                );
            }
        } catch (Exception ex) {
            LOGGER.log(
                Level.FINER,
                "Unexpected error while sending TAB event",
                ex
            );
        }
    }

    /**
     * Initialize the controller. Subclasses must implement setup logic and UI
     * wiring here.
     */
    public abstract void initialize();
}
