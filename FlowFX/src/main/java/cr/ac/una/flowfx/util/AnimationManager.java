package cr.ac.una.flowfx.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;
import io.github.palexdev.materialfx.controls.MFXButton;

public class AnimationManager {
    
    private static final Duration DEFAULT_DURATION = Duration.millis(300);
    private static final double BLUR_RADIUS = 10.0;
    
    public static void showPopup(Node popup, Node cover, MFXButton... buttonsToDisable) {
        showPopup(popup, cover, DEFAULT_DURATION, buttonsToDisable);
    }
    
    public static void showPopup(Node popup, Node cover, Duration duration, MFXButton... buttonsToDisable) {
        popup.setVisible(true);
        popup.setManaged(true);
        popup.setOpacity(0);
        popup.setScaleX(0.7);
        popup.setScaleY(0.7);

        for (MFXButton button : buttonsToDisable) {
            button.setDisable(true);
        }

        FadeTransition popupFade = new FadeTransition(duration, popup);
        popupFade.setFromValue(0);
        popupFade.setToValue(1);

        // Springy scale: overshoot, then settle
        Timeline scaleTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(popup.scaleXProperty(), 0.7),
                new KeyValue(popup.scaleYProperty(), 0.7)
            ),
            new KeyFrame(duration.multiply(0.7),
                new KeyValue(popup.scaleXProperty(), 1.08),
                new KeyValue(popup.scaleYProperty(), 1.08)
            ),
            new KeyFrame(duration,
                new KeyValue(popup.scaleXProperty(), 1.0),
                new KeyValue(popup.scaleYProperty(), 1.0)
            )
        );

        GaussianBlur blurEffect;
        if (cover.getEffect() instanceof GaussianBlur) {
            blurEffect = (GaussianBlur) cover.getEffect();
        } else {
            blurEffect = new GaussianBlur(0);
            cover.setEffect(blurEffect);
        }

        Timeline blurTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(blurEffect.radiusProperty(), 0)),
            new KeyFrame(duration, new KeyValue(blurEffect.radiusProperty(), BLUR_RADIUS))
        );

        ParallelTransition showTransition = new ParallelTransition(popupFade, scaleTimeline, blurTimeline);
        showTransition.play();
    }
    
    public static void hidePopup(Node popup, Node cover, MFXButton... buttonsToEnable) {
        hidePopup(popup, cover, DEFAULT_DURATION, buttonsToEnable);
    }

    public static void hidePopup(Node popup) {
        FadeTransition popupFade = new FadeTransition(DEFAULT_DURATION, popup);
        popupFade.setFromValue(1);
        popupFade.setToValue(0);

        ScaleTransition popupScale = new ScaleTransition(DEFAULT_DURATION, popup);
        popupScale.setFromX(1.0);
        popupScale.setFromY(1.0);
        popupScale.setToX(0.8);
        popupScale.setToY(0.8);

        ParallelTransition hideTransition = new ParallelTransition(popupFade, popupScale);
        hideTransition.setOnFinished(e -> {
            popup.setVisible(false);
            popup.setManaged(false);
        });
        hideTransition.play();
    }
    
    public static void hidePopup(Node popup, Node cover, Duration duration, MFXButton... buttonsToEnable) {
        FadeTransition popupFade = new FadeTransition(duration, popup);
        popupFade.setFromValue(1);
        popupFade.setToValue(0);

        // Springy scale: shrink with bounce
        Timeline scaleTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(popup.scaleXProperty(), 1.0),
                new KeyValue(popup.scaleYProperty(), 1.0)
            ),
            new KeyFrame(duration.multiply(0.5),
                new KeyValue(popup.scaleXProperty(), 1.08),
                new KeyValue(popup.scaleYProperty(), 1.08)
            ),
            new KeyFrame(duration,
                new KeyValue(popup.scaleXProperty(), 0.7),
                new KeyValue(popup.scaleYProperty(), 0.7)
            )
        );

        Timeline blurTimeline = new Timeline();
        if (cover.getEffect() instanceof GaussianBlur) {
            GaussianBlur blurEffect = (GaussianBlur) cover.getEffect();
            blurTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(blurEffect.radiusProperty(), BLUR_RADIUS)),
                new KeyFrame(duration, new KeyValue(blurEffect.radiusProperty(), 0))
            );
        }

        ParallelTransition hideTransition = new ParallelTransition(popupFade, scaleTimeline, blurTimeline);
        hideTransition.setOnFinished(e -> {
            popup.setVisible(false);
            popup.setManaged(false);
            cover.setEffect(null);
            for (MFXButton button : buttonsToEnable) {
                button.setDisable(false);
            }
        });
        hideTransition.play();
    }
    
    public static void fadeIn(Node node) {
        fadeIn(node, DEFAULT_DURATION);
    }
    
    public static void fadeIn(Node node, Duration duration) {
        node.setVisible(true);
        node.setManaged(true);
        node.setOpacity(0);
        
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    public static void fadeOut(Node node) {
        fadeOut(node, DEFAULT_DURATION);
    }
    
    public static void fadeOut(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        fade.play();
    }
    
    public static void scaleIn(Node node) {
        scaleIn(node, DEFAULT_DURATION);
    }
    
    public static void scaleIn(Node node, Duration duration) {
        node.setVisible(true);
        node.setManaged(true);
        node.setScaleX(0);
        node.setScaleY(0);
        
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1);
        scale.setToY(1);
        scale.play();
    }
    
    public static void scaleOut(Node node) {
        scaleOut(node, DEFAULT_DURATION);
    }
    
    public static void scaleOut(Node node, Duration duration) {
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(0);
        scale.setToY(0);
        scale.setOnFinished(e -> {
            node.setVisible(false);
            node.setManaged(false);
        });
        scale.play();
    }
}