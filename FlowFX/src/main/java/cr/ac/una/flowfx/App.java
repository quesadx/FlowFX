package cr.ac.una.flowfx;

import cr.ac.una.flowfx.util.FlowController;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(
            getClass().getResourceAsStream(
                "/cr/ac/una/flowfx/resources/font/Organo.ttf"
            ),
            10
        );

        stage.initStyle(StageStyle.UNDECORATED);
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain();
        FlowController.getInstance().goView("MainView");
        //FlowController.getInstance().goViewInWindowModal("PersonSelectionView", stage, false);
    }

    public static void main(String[] args) {
        launch();
    }
}
