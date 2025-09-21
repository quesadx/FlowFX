package cr.ac.una.flowfx.controller;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * Controller for the Creative Zone view, providing freehand drawing on a
 * responsive canvas with pencil/eraser tools, color selection, and line width
 * controls. Built to be easily extensible for future tools like straight-line
 * drawing or shape stencils.
 */
public class CreativeZoneController extends Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CreativeZoneController.class.getName());

    private static final double MIN_LINE_WIDTH = 1d;
    private static final double MAX_LINE_WIDTH = 50d;
    private static final double DEFAULT_LINE_WIDTH = 4d;
    private static final Color DEFAULT_BACKGROUND = Color.WHITE;
    private static final Color DEFAULT_COLOR = Color.BLACK;

    private enum Tool {
        PENCIL,
        ERASER
    }

    @FXML private AnchorPane root;
    @FXML private AnchorPane apMainCanvasAnchoring;
    @FXML private Canvas cvMainCanvas;
    @FXML private Label lblLineWidth;
    @FXML private ColorPicker cpColorPicker;
    @FXML private Circle csPencilSelected;
    @FXML private Circle csEraserSelected;

    private GraphicsContext gc;
    private Tool currentTool = Tool.PENCIL;
    private Color currentColor = DEFAULT_COLOR;
    private double lineWidth = DEFAULT_LINE_WIDTH;
    private boolean drawing;
    private double lastX;
    private double lastY;
    private WritableImage lastSnapshot;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        csPencilSelected.setVisible(true);
        csEraserSelected.setVisible(false);
        setupCanvas();
        setupBindings();
        updateLineWidthLabel();
    }

    @Override
    public void initialize() {
        // No-op required by base Controller
    }

    @FXML
    private void onActionSaveCanvas(ActionEvent event) {
        // Save current canvas as PNG
        try {
            WritableImage image = cvMainCanvas.snapshot(new SnapshotParameters(), null);
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Guardar sketch");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
            chooser.setInitialFileName("sketch.png");
            File file = chooser.showSaveDialog(root.getScene() != null ? root.getScene().getWindow() : null);
            if (file != null) {
                ImageIO.write(toBufferedImage(image), "png", file);
                LOGGER.log(Level.INFO, "Sketch guardado en {0}", file.getAbsolutePath());
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error al intentar guardar sketch", ex);
        }
    }

    @FXML
    private void onActionCleanCanvas(ActionEvent event) {
        fillBackground(DEFAULT_BACKGROUND);
        lastSnapshot = cvMainCanvas.snapshot(new SnapshotParameters(), null);
    }

    @FXML
    private void onMousePressedMainCanvas(MouseEvent event) {
        drawing = true;
        lastX = event.getX();
        lastY = event.getY();
        configureStrokeForCurrentTool();
        gc.beginPath();
        gc.moveTo(lastX, lastY);
        gc.stroke();
    }

    @FXML
    private void onMouseDraggedMainCanvas(MouseEvent event) {
        if (!drawing) {
            return;
        }
        double x = event.getX();
        double y = event.getY();
        configureStrokeForCurrentTool();
        gc.lineTo(x, y);
        gc.stroke();
        lastX = x;
        lastY = y;
    }

    @FXML
    private void onMouseReleasedMainCanvas(MouseEvent event) {
        if (!drawing) {
            return;
        }
        drawing = false;
        // Finalize path and snapshot for resize preservation
        gc.closePath();
        lastSnapshot = cvMainCanvas.snapshot(new SnapshotParameters(), null);
    }

    @FXML
    private void onActionBtnPencil(ActionEvent event) {
        // Two buttons are wired to the same handler (pencil/eraser). We detect which one
        // by inspecting its Tooltip text. Defaults to pencil if not found.
        System.out.println("Pencil selected");
        currentTool = Tool.PENCIL;
        csPencilSelected.setVisible(true);
        csEraserSelected.setVisible(false);
        configureStrokeForCurrentTool();
    }

    @FXML
    private void onActionBtnEraser(ActionEvent event) {
        // Two buttons are wired to the same handler (pencil/eraser). We detect which one
        // by inspecting its Tooltip text. Defaults to pencil if not found.
        System.out.println("Eraser selected");
        currentTool = Tool.ERASER;
        csPencilSelected.setVisible(false);
        csEraserSelected.setVisible(true);
        configureStrokeForCurrentTool();
    }


    @FXML
    private void onActionBtnIncreasePencilWidth(ActionEvent event) {
        setLineWidth(lineWidth + 1d);
    }

    @FXML
    private void onActionBtnDecreasePencilWidth(ActionEvent event) {
        setLineWidth(lineWidth - 1d);
    }

    // -----------------------------------------------------
    // Internal setup and helpers
    // -----------------------------------------------------

    private void setupCanvas() {
        gc = cvMainCanvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        gc.setLineWidth(lineWidth);
        gc.setStroke(currentColor);
        // Prepare a white background so eraser behaves predictably
        fillBackground(DEFAULT_BACKGROUND);
        lastSnapshot = cvMainCanvas.snapshot(new SnapshotParameters(), null);
    }

    private void setupBindings() {
        // Bind canvas size to anchoring pane to be responsive
        cvMainCanvas.widthProperty().bind(apMainCanvasAnchoring.widthProperty());
        cvMainCanvas.heightProperty().bind(apMainCanvasAnchoring.heightProperty());

        // When the canvas size changes (due to binding), redraw last snapshot to preserve content
        cvMainCanvas.widthProperty().addListener(obs -> preserveOnResize());
        cvMainCanvas.heightProperty().addListener(obs -> preserveOnResize());
    }

    private void preserveOnResize() {
        // Reapply background and redraw last snapshot in the top-left corner
        fillBackground(DEFAULT_BACKGROUND);
        if (lastSnapshot != null) {
            gc.drawImage(lastSnapshot, 0, 0);
        }
    }

    private void configureStrokeForCurrentTool() {
        gc.setLineWidth(lineWidth);
        if (currentTool == Tool.ERASER) {
            gc.setStroke(DEFAULT_BACKGROUND);
        } else {
            gc.setStroke(cpColorPicker.getValue());
        }
    }

    private void setLineWidth(double value) {
        lineWidth = max(MIN_LINE_WIDTH, min(MAX_LINE_WIDTH, value));
        updateLineWidthLabel();
        gc.setLineWidth(lineWidth);
    }

    // Ya no se usa, pero la dejo por si acaso
    private void setColor(Color color) {
        currentColor = color != null ? color : DEFAULT_COLOR;
        if (currentTool == Tool.PENCIL) {
            gc.setStroke(currentColor);
        }
    }

    private void updateLineWidthLabel() {
        if (lblLineWidth != null) {
            lblLineWidth.setText(String.format(Locale.ROOT, "%.0f px", lineWidth));
        }
    }

    private void fillBackground(Color color) {
        gc.setFill(color);
        gc.fillRect(0, 0, cvMainCanvas.getWidth(), cvMainCanvas.getHeight());
        // Restore stroke after fill
        configureStrokeForCurrentTool();
    }

    private static BufferedImage toBufferedImage(WritableImage image) {
        int w = (int) Math.round(image.getWidth());
        int h = (int) Math.round(image.getHeight());
        BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        javafx.scene.image.PixelReader reader = image.getPixelReader();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                bimg.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        return bimg;
    }
}
