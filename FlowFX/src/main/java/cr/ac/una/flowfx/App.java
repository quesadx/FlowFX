package cr.ac.una.flowfx;

import cr.ac.una.flowfx.util.FlowController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain();
        FlowController.getInstance().goView("MainView");
    }


    public static void main(String[] args) {
        launch();
    }

}