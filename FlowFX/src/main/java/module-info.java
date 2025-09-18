module cr.ac.una.flowfx {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires transitive javafx.base;

    // Librerías externas
    requires MaterialFX;
    requires jakarta.persistence;
    requires jakarta.xml.ws;
    requires jakarta.json;
    // Abre paquetes para reflexión (FXML/JAXB/Metro)
    opens cr.ac.una.flowfx to javafx.fxml;
    opens cr.ac.una.flowfx.controller to javafx.fxml;
    opens cr.ac.una.flowfx.model to javafx.fxml, jakarta.xml.bind;

    // IMPORTANTE: abrir el paquete de clases generadas del WS para reflexión en JAXB/Metro
    // Abrir sin restricción evita problemas con módulos sin nombre o distintas implementaciones.
    opens cr.ac.una.flowfx.ws;

    // Exporta los paquetes que se usan fuera del módulo
    exports cr.ac.una.flowfx;
    exports cr.ac.una.flowfx.model;
}