module cr.ac.una.flowfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.logging;
    requires MaterialFX;
    requires jakarta.persistence;
    requires javafx.graphics;
    requires java.base;
    requires java.instrument;

    opens cr.ac.una.flowfx to javafx.fxml;
    opens cr.ac.una.flowfx.controller to javafx.fxml; 
    exports cr.ac.una.flowfx;
    exports cr.ac.una.flowfx.model;
    opens cr.ac.una.flowfx.model;
}