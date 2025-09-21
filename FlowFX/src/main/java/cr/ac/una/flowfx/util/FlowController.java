package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.App;
import cr.ac.una.flowfx.controller.Controller;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Central controller that manages view navigation and stage handling.
 *
 * <p>This class keeps a cache of loaded FXML loaders, hosts the main application
 * root, and provides methods to open views in-place, in new stages or modal
 * windows.</p>
 *
 * <p>Public API and method signatures are preserved to avoid breaking callers.</p>
 */
public class FlowController {

    private static final Logger LOGGER = Logger.getLogger(
        FlowController.class.getName()
    );

    private static volatile FlowController INSTANCE;
    private static Stage mainStage;
    private static ResourceBundle idioma;
    private static final HashMap<String, FXMLLoader> loaders = new HashMap<>();
    // Keep a reference to the main layout (PrincipalView root) even if we wrap the Scene root
    private static BorderPane appRoot;
    // The stacked root used to host global overlays (e.g., dock)
    private static StackPane rootStack;

    private FlowController() {
        // Intentionally empty
    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (FlowController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlowController();
                }
            }
        }
    }

    /**
     * Returns the singleton instance of the flow controller.
     *
     * @return the singleton instance
     */
    public static FlowController getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    /**
     * Configures the application icon and title for a stage.
     * 
     * @param stage the stage to configure
     */
    private void configureStageIconAndTitle(Stage stage) {
        if (stage == null) return;
        
        try {
            // Set application title
            stage.setTitle("FlowFX");
            
            // Set application icon using the flower icon - try multiple path variations
            String[] iconPaths = {
                "resources/icons/lucide--flower.png",
                "/cr/ac/una/flowfx/resources/icons/lucide--flower.png",
                "cr/ac/una/flowfx/resources/icons/lucide--flower.png"
            };
            
            boolean iconLoaded = false;
            for (String iconPath : iconPaths) {
                try {
                    var iconUrl = App.class.getResource(iconPath);
                    if (iconUrl != null) {
                        Image icon = new Image(iconUrl.toExternalForm());
                        stage.getIcons().clear();
                        stage.getIcons().add(icon);
                        LOGGER.fine("Application icon loaded successfully from: " + iconPath);
                        iconLoaded = true;
                        break;
                    }
                } catch (Exception iconEx) {
                    LOGGER.fine("Failed to load icon from path: " + iconPath);
                }
            }
            
            if (!iconLoaded) {
                LOGGER.warning("Application icon could not be loaded from any of the attempted paths");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to configure application icon and title", ex);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Initializes the controller with the primary stage and an optional resource bundle.
     *
     * @param stage  primary stage to use as default owner for views
     * @param idioma resource bundle for localization (may be null)
     */
    public void InitializeFlow(Stage stage, ResourceBundle idioma) {
        Objects.requireNonNull(stage, "stage must not be null");
        FlowController.mainStage = stage;
        FlowController.idioma = idioma;
    }

    /**
     * Retrieves a cached loader for the given view name or creates/loads it if missing.
     *
     * @param name view name (without .fxml)
     * @return the loaded FXMLLoader or null if loading failed
     */
    private FXMLLoader getLoader(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("view name is required");
        }
        synchronized (loaders) {
            FXMLLoader loader = loaders.get(name);
            if (loader != null) {
                return loader;
            }
            try {
                loader = new FXMLLoader(
                    App.class.getResource("view/" + name + ".fxml"),
                    FlowController.idioma
                );
                loader.load();
                loaders.put(name, loader);
                return loader;
            } catch (Exception ex) {
                LOGGER.log(
                    Level.SEVERE,
                    "Error creating loader for view: " + name,
                    ex
                );
                return null;
            }
        }
    }

    /**
     * Sets up the main application scene and shows the primary stage.
     */
    public void goMain() {
        try {
            FXMLLoader baseLoader = new FXMLLoader(
                App.class.getResource("view/PrincipalView.fxml"),
                idioma
            );
            Parent base = baseLoader.load();
            if (base instanceof BorderPane) {
                appRoot = (BorderPane) base;
            }
            Parent contentRoot = appRoot != null ? appRoot : base;

            rootStack = new StackPane();
            rootStack.getChildren().add(contentRoot);
            Scene scene = new Scene(rootStack);
            mainStage.setScene(scene);

            // Configure application icon and title
            configureStageIconAndTitle(mainStage);

            MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);

            // Ensure app stylesheet overrides theme styles by appending it last
            try {
                String appCss = App.class.getResource(
                    "view/styles.css"
                ).toExternalForm();
                if (!scene.getStylesheets().contains(appCss)) {
                    scene.getStylesheets().add(appCss);
                } else {
                    scene.getStylesheets().remove(appCss);
                    scene.getStylesheets().add(appCss);
                }
            } catch (Exception ex) {
                LOGGER.log(
                    Level.FINE,
                    "Application stylesheet not found or failed to load.",
                    ex
                );
            }

            // Attach global dock overlay once the scene is ready (best-effort)
            try {
                DockOverlayManager.attach(mainStage, rootStack);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Dock overlay attach failed", ex);
            }

            mainStage.show();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error initializing base view.", ex);
        }
    }

    /**
     * Convenience overload to open a view in the center region.
     *
     * @param viewName view name
     */
    public void goView(String viewName) {
        goView(viewName, "Center", null);
    }

    /**
     * Convenience overload to open a view in the center region with an action.
     *
     * @param viewName view name
     * @param accion   action string (may be null)
     */
    public void goView(String viewName, String accion) {
        goView(viewName, "Center", accion);
    }

    /**
     * Opens a view in a specific location of the current stage's root.
     *
     * @param viewName view name (without .fxml)
     * @param location one of Center, Top, Bottom, Left, Right
     * @param accion   optional action string for the controller
     */
    public void goView(String viewName, String location, String accion) {
        FXMLLoader loader = getLoader(viewName);
        if (loader == null) {
            LOGGER.log(
                Level.WARNING,
                "Loader for view {0} is null, aborting goView.",
                viewName
            );
            return;
        }
        Controller controller = loader.getController();
        if (controller == null) {
            LOGGER.log(
                Level.WARNING,
                "Controller for view {0} is null, aborting goView.",
                viewName
            );
            return;
        }

        // Preserve public API: controller initialization must be invoked
        controller.initialize();

        Stage stage = controller.getStage();
        if (stage == null) {
            stage = mainStage;
            controller.setStage(stage);
        }

        switch (location) {
            case "Center": {
                BorderPane borderPane = appRoot != null
                    ? appRoot
                    : (BorderPane) stage.getScene().getRoot();
                borderPane.setCenter(loader.getRoot());
                break;
            }
            case "Top": {
                BorderPane borderPane2 = appRoot != null
                    ? appRoot
                    : (BorderPane) stage.getScene().getRoot();
                HBox hbox = (HBox) borderPane2.getTop();
                if (hbox != null) {
                    hbox.getChildren().clear();
                    hbox.getChildren().add(loader.getRoot());
                }
                break;
            }
            case "Bottom":
            case "Right":
            case "Left":
            default:
                // No-op for unsupported locations; keep behavior stable.
                break;
        }
    }

    /**
     * Replaces the root of the provided stage with the given view.
     *
     * @param viewName view name
     * @param stage    target stage
     */
    public void goViewInStage(String viewName, Stage stage) {
        FXMLLoader loader = getLoader(viewName);
        if (loader == null) {
            LOGGER.log(
                Level.WARNING,
                "Loader for view {0} is null, aborting goViewInStage.",
                viewName
            );
            return;
        }
        Controller controller = loader.getController();
        if (controller != null) {
            controller.setStage(stage);
        }
        stage.getScene().setRoot(loader.getRoot());
        MFXThemeManager.addOn(stage.getScene(), Themes.DEFAULT, Themes.LEGACY);

        try {
            String appCss = App.class.getResource(
                "view/styles.css"
            ).toExternalForm();
            if (!stage.getScene().getStylesheets().contains(appCss)) {
                stage.getScene().getStylesheets().add(appCss);
            } else {
                stage.getScene().getStylesheets().remove(appCss);
                stage.getScene().getStylesheets().add(appCss);
            }
        } catch (Exception ex) {
            LOGGER.log(
                Level.FINE,
                "Application stylesheet not found for stage.",
                ex
            );
        }
    }

    /**
     * Opens a view in a new non-modal window.
     *
     * @param viewName view name
     */
    public void goViewInWindow(String viewName) {
        FXMLLoader loader = getLoader(viewName);
        if (loader == null) {
            LOGGER.log(
                Level.WARNING,
                "Loader for view {0} is null, aborting goViewInWindow.",
                viewName
            );
            return;
        }
        Controller controller = loader.getController();
        if (controller == null) {
            LOGGER.log(
                Level.WARNING,
                "Controller for view {0} is null, aborting goViewInWindow.",
                viewName
            );
            return;
        }
        controller.initialize();

        Stage stage = new Stage();
        
        // Configure application icon and title
        configureStageIconAndTitle(stage);
        // Override title with controller-specific title if available
        if (controller.getNombreVista() != null && !controller.getNombreVista().trim().isEmpty()) {
            stage.setTitle("FlowFX - " + controller.getNombreVista());
        }
        
        stage.setOnHidden((WindowEvent event) -> {
            if (
                controller.getStage() != null &&
                controller.getStage().getScene() != null
            ) {
                controller.getStage().getScene().setRoot(new Pane());
            }
            controller.setStage(null);
        });
        controller.setStage(stage);

        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        try {
            String appCss = App.class.getResource(
                "view/styles.css"
            ).toExternalForm();
            scene.getStylesheets().add(appCss);
        } catch (Exception ex) {
            LOGGER.log(
                Level.FINE,
                "Application stylesheet not found for window.",
                ex
            );
        }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Opens a view in a modal undecorated window.
     *
     * @param viewName    view name
     * @param parentStage owner stage for modality
     * @param resizable   whether the window is resizable
     */
    public void goViewInWindowModal(
        String viewName,
        Stage parentStage,
        Boolean resizable
    ) {
        FXMLLoader loader = getLoader(viewName);
        if (loader == null) {
            LOGGER.log(
                Level.WARNING,
                "Loader for view {0} is null, aborting goViewInWindowModal.",
                viewName
            );
            return;
        }
        Controller controller = loader.getController();
        if (controller == null) {
            LOGGER.log(
                Level.WARNING,
                "Controller for view {0} is null, aborting goViewInWindowModal.",
                viewName
            );
            return;
        }
        controller.initialize();

        Stage stage = new Stage();
        
        // Configure application icon and title
        configureStageIconAndTitle(stage);
        // Override title with controller-specific title if available
        if (controller.getNombreVista() != null && !controller.getNombreVista().trim().isEmpty()) {
            stage.setTitle("FlowFX - " + controller.getNombreVista());
        }
        
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(resizable);
        stage.setOnHidden((WindowEvent event) -> {
            if (
                controller.getStage() != null &&
                controller.getStage().getScene() != null
            ) {
                controller.getStage().getScene().setRoot(new Pane());
            }
            controller.setStage(null);
        });
        controller.setStage(stage);

        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        try {
            String appCss = App.class.getResource(
                "view/styles.css"
            ).toExternalForm();
            scene.getStylesheets().add(appCss);
        } catch (Exception ex) {
            LOGGER.log(
                Level.FINE,
                "Application stylesheet not found for modal.",
                ex
            );
        }
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.centerOnScreen();
        stage.showAndWait();
    }

    /**
     * Returns the controller instance for a given view.
     *
     * @param viewName view name
     * @return controller or null if loader missing
     */
    public Controller getController(String viewName) {
        FXMLLoader loader = getLoader(viewName);
        return loader != null ? loader.getController() : null;
    }

    /**
     * Removes a loader from the cache so it will be reloaded next time.
     *
     * @param view view name
     */
    public void limpiarLoader(String view) {
        synchronized (loaders) {
            loaders.remove(view);
        }
    }

    /**
     * Sets the resource bundle used by loaders.
     *
     * @param idioma resource bundle
     */
    public static void setIdioma(ResourceBundle idioma) {
        FlowController.idioma = idioma;
    }

    /**
     * Clears cached loaders.
     */
    public void initialize() {
        synchronized (loaders) {
            loaders.clear();
        }
    }

    /**
     * Closes the primary stage.
     */
    public void salir() {
        if (mainStage != null) {
            mainStage.close();
        }
    }
}
