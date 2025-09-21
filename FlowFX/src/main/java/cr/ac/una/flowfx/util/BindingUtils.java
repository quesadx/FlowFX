package cr.ac.una.flowfx.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import java.util.logging.Logger;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class for binding toggle groups to properties with improved thread safety
 * and proper listener management.
 * 
 * @author ccarranza
 */
public final class BindingUtils {

    private static final Logger LOGGER = Logger.getLogger(BindingUtils.class.getName());
    
    // Store listeners per toggle group to avoid conflicts
    private static final Map<ToggleGroup, ChangeListener<Toggle>> TOGGLE_LISTENERS = new WeakHashMap<>();

    private BindingUtils() {}

    /**
     * Binds a toggle group to an ObjectProperty, creating a unique listener for each binding.
     * This prevents the listener conflict bug where only the last bound toggle group would work.
     * 
     * @param <T> the property value type
     * @param toggleGroup the toggle group to bind
     * @param property the property to bind to
     */
    public static <T> void bindToggleGroupToProperty(
        final ToggleGroup toggleGroup,
        final ObjectProperty<T> property
    ) {
        if (toggleGroup == null || property == null) {
            LOGGER.warning("Cannot bind toggle group: toggleGroup or property is null");
            return;
        }
        
        // Check all toggles for required user data
        toggleGroup
            .getToggles()
            .forEach(toggle -> {
                if (toggle.getUserData() == null) {
                    throw new IllegalArgumentException(
                        "The ToggleGroup contains at least one Toggle without user data!"
                    );
                }
            });
            
        // Remove any existing listener for this toggle group
        unbindToggleGroupToProperty(toggleGroup, property);
        
        // Select initial toggle for current property state
        for (Toggle toggle : toggleGroup.getToggles()) {
            if (
                property.getValue() != null &&
                property.getValue().equals(toggle.getUserData())
            ) {
                toggleGroup.selectToggle(toggle);
                LOGGER.fine("Selected initial toggle with userData: " + toggle.getUserData());
                break;
            }
        }
        
        // Create a new listener for this specific toggle group
        ChangeListener<Toggle> newListener = (
            ObservableValue<? extends Toggle> observable,
            Toggle oldValue,
            Toggle newValue
        ) -> {
            LOGGER.fine("Toggle group selection changed: " + 
                (oldValue != null ? oldValue.getUserData() : "null") + " -> " + 
                (newValue != null ? newValue.getUserData() : "null"));
                
            if (newValue == null) {
                // Keep property unchanged and restore a valid selection
                if (oldValue != null) {
                    toggleGroup.selectToggle(oldValue);
                    LOGGER.fine("Restored previous selection: " + oldValue.getUserData());
                } else {
                    for (Toggle t : toggleGroup.getToggles()) {
                        if (
                            property.getValue() != null &&
                            property.getValue().equals(t.getUserData())
                        ) {
                            toggleGroup.selectToggle(t);
                            LOGGER.fine("Restored toggle for current property value: " + t.getUserData());
                            break;
                        }
                    }
                }
                return;
            }
            @SuppressWarnings("unchecked")
            T val = (T) newValue.getUserData();
            LOGGER.fine("Setting property value to: " + val);
            property.setValue(val);
        };
        
        // Store the listener and add it to the toggle group
        TOGGLE_LISTENERS.put(toggleGroup, newListener);
        toggleGroup.selectedToggleProperty().addListener(newListener);
        
        LOGGER.fine("Successfully bound toggle group to property");
    }

    /**
     * Unbinds a toggle group from its property by removing the stored listener.
     * 
     * @param <T> the property value type
     * @param toggleGroup the toggle group to unbind
     * @param property the property (unused but kept for API compatibility)
     */
    public static <T> void unbindToggleGroupToProperty(
        final ToggleGroup toggleGroup,
        final ObjectProperty<T> property
    ) {
        if (toggleGroup == null) return;
        
        ChangeListener<Toggle> listener = TOGGLE_LISTENERS.remove(toggleGroup);
        if (listener != null) {
            toggleGroup.selectedToggleProperty().removeListener(listener);
            LOGGER.fine("Unbound toggle group from property");
        }
    }
}
