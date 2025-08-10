package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.App;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import cr.ac.una.flowfx.controller.Controller;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;

public class FlowController {

    private static FlowController INSTANCE = null;
    private static Stage mainStage;
    private static ResourceBundle idioma;
    private static HashMap<String, FXMLLoader> loaders = new HashMap<>();
    // Keep a reference to the main layout (PrincipalView root) even if we wrap the Scene root
    private static BorderPane appRoot;
    // The stacked root used to host global overlays (e.g., dock)
    private static StackPane rootStack;

    private FlowController() {
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

    public static FlowController getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void InitializeFlow(Stage stage, ResourceBundle idioma) {
        getInstance();
    FlowController.mainStage = stage;
    FlowController.idioma = idioma;
    }

    private FXMLLoader getLoader(String name) {
        FXMLLoader loader = loaders.get(name);
        if (loader == null) {
            synchronized (FlowController.class) {
                if (loader == null) {
                    try {
                        loader = new FXMLLoader(App.class.getResource("view/" + name + ".fxml"), FlowController.idioma);
                        loader.load();
                        loaders.put(name, loader);
                    } catch (Exception ex) {
                        loader = null;
                        java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Creando loader [" + name + "].", ex);
                    }
                }
            }
        }
        return loader;
    }

    public void goMain() {
        try {
            // Load main layout so we can wrap it in a StackPane for global overlays
            FXMLLoader baseLoader = new FXMLLoader(App.class.getResource("view/PrincipalView.fxml"), idioma);
            Parent base = baseLoader.load();
            if (base instanceof BorderPane) {
                appRoot = (BorderPane) base;
            }
            // Fallback safety in case FXML root changes type
            Parent contentRoot = appRoot != null ? appRoot : base;

            // Wrap in a StackPane to layer overlays above all views
            rootStack = new StackPane();
            rootStack.getChildren().add(contentRoot);
            Scene scene = new Scene(rootStack);
            mainStage.setScene(scene);
            MFXThemeManager.addOn(mainStage.getScene(), Themes.DEFAULT, Themes.LEGACY);
            // Ensure app stylesheet overrides theme styles by appending it last
            try {
                String appCss = App.class.getResource("view/styles.css").toExternalForm();
                if (!mainStage.getScene().getStylesheets().contains(appCss)) {
                    mainStage.getScene().getStylesheets().add(appCss);
                } else {
                    // Move to end to ensure precedence
                    mainStage.getScene().getStylesheets().remove(appCss);
                    mainStage.getScene().getStylesheets().add(appCss);
                }
            } catch (Exception ignored) { }

            // Attach global dock overlay once the scene is ready
            try {
                DockOverlayManager.attach(mainStage, rootStack);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.WARNING, "Dock overlay attach failed", ex);
            }
            mainStage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Error inicializando la vista base.", ex);
        }
    }

    public void goView(String viewName) {
        goView(viewName, "Center", null);
    }

    public void goView(String viewName, String accion) {
        goView(viewName, "Center", accion);
    }

    public void goView(String viewName, String location, String accion) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        //controller.setAccion(accion);
        controller.initialize();
        Stage stage = controller.getStage();
        if (stage == null) {
            stage = mainStage;
            controller.setStage(stage);
        }
    switch (location) {
            case "Center":
        // Prefer the stored main BorderPane if available; fallback to scene root cast
        BorderPane borderPane = appRoot != null ? appRoot : (BorderPane) stage.getScene().getRoot();
                //VBox vBox = (VBox)borderPane.getCenter();
                //vBox.getChildren().clear();
                //vBox.getChildren().add(loader.getRoot());
                
                /*VBox vBox = ((VBox) ((BorderPane) stage.getScene().getRoot()).getCenter());
                vBox.getChildren().clear();
                vBox.getChildren().add(loader.getRoot());*/
                borderPane.setCenter(loader.getRoot());
                break;
            case "Top":
        BorderPane borderPane2 = appRoot != null ? appRoot : (BorderPane) stage.getScene().getRoot();
                HBox hbox = (HBox)borderPane2.getTop();
                hbox.getChildren().clear();
                hbox.getChildren().add(loader.getRoot());
                break;
            case "Bottom":
                break;
            case "Right":
                break;
            case "Left":
                break;
            default:
                break;
        }
    }

    public void goViewInStage(String viewName, Stage stage) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setStage(stage);
        stage.getScene().setRoot(loader.getRoot());
        MFXThemeManager.addOn(stage.getScene(), Themes.DEFAULT, Themes.LEGACY);
        // Ensure app stylesheet overrides theme styles by appending it last
        try {
            String appCss = App.class.getResource("view/styles.css").toExternalForm();
            if (!stage.getScene().getStylesheets().contains(appCss)) {
                stage.getScene().getStylesheets().add(appCss);
            } else {
                stage.getScene().getStylesheets().remove(appCss);
                stage.getScene().getStylesheets().add(appCss);
            }
        } catch (Exception ignored) { }
        
    }

    public void goViewInWindow(String viewName) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.initialize();
        Stage stage = new Stage();
        stage.getIcons().add(new Image("cr/ac/una/unaplanillal2024/resources/LogoUNArojo.png"));
        stage.setTitle(controller.getNombreVista());
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        controller.setStage(stage);
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        // Ensure app stylesheet overrides theme styles by appending it last
        try {
            String appCss = App.class.getResource("view/styles.css").toExternalForm();
            scene.getStylesheets().add(appCss);
        } catch (Exception ignored) { }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void goViewInWindowModal(String viewName, Stage parentStage, Boolean resizable) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.initialize();
        Stage stage = new Stage();
        stage.getIcons().add(new Image("cr/ac/una/unaplanilla/resources/LogoUNArojo.png"));
        stage.setTitle(controller.getNombreVista());
        stage.setResizable(resizable);
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        controller.setStage(stage);
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        // Ensure app stylesheet overrides theme styles by appending it last
        try {
            String appCss = App.class.getResource("view/styles.css").toExternalForm();
            scene.getStylesheets().add(appCss);
        } catch (Exception ignored) { }
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.centerOnScreen();
        stage.showAndWait();

    }

    public Controller getController(String viewName) {
        return getLoader(viewName).getController();
    }
    
    public void limpiarLoader(String view){
    loaders.remove(view);
    }

    public static void setIdioma(ResourceBundle idioma) {
        FlowController.idioma = idioma;
    }
    
    public void initialize() {
    loaders.clear();
    }

    public void salir() {
    mainStage.close();
    }

}
