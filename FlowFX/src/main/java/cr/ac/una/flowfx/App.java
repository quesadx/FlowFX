package cr.ac.una.flowfx;

import cr.ac.una.flowfx.util.FlowController;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Entry point for the FlowFX JavaFX application.
 *
 * <p>This class loads application resources (fonts) and initializes the main
 * application flow via {@link FlowController}.</p>
 */
public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        // Load custom font if present. Errors loading the font should not prevent app startup.
        try (
            InputStream stream = App.class.getResourceAsStream(
                "/cr/ac/una/flowfx/resources/font/Organo.ttf"
            )
        ) {
            if (stream != null) {
                Font.loadFont(stream, 10);
            } else {
                LOGGER.log(
                    Level.WARNING,
                    "Font resource not found: /cr/ac/una/flowfx/resources/font/Organo.ttf"
                );
            }
        } catch (Exception ex) {
            LOGGER.log(
                Level.WARNING,
                "Unexpected error loading application font",
                ex
            );
        }

        stage.initStyle(StageStyle.UNDECORATED);
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain();
        FlowController.getInstance().goView("MainView");
        // To show a modal selection window:
        // FlowController.getInstance().goViewInWindowModal("PersonSelectionView", stage, false);
    }

    /**
     * Application launcher.
     *
     * @param args command-line arguments forwarded to the JavaFX runtime
     */
    public static void main(String[] args) {
        launch(args);
    }
}
