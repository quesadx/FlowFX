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
        popup.setScaleX(0.8);
        popup.setScaleY(0.8);
        
        for (MFXButton button : buttonsToDisable) {
            button.setDisable(true);
        }
        
        FadeTransition popupFade = new FadeTransition(duration, popup);
        popupFade.setFromValue(0);
        popupFade.setToValue(1);
        
        ScaleTransition popupScale = new ScaleTransition(duration, popup);
        popupScale.setFromX(0.8);
        popupScale.setFromY(0.8);
        popupScale.setToX(1.0);
        popupScale.setToY(1.0);
        
        GaussianBlur blurEffect;
        if (cover.getEffect() instanceof GaussianBlur) {
            blurEffect = (GaussianBlur) cover.getEffect();
        } else {
            blurEffect = new GaussianBlur(0);
            cover.setEffect(blurEffect);
        }
        
        Timeline blurTimeline = new Timeline();
        KeyValue blurValue = new KeyValue(blurEffect.radiusProperty(), BLUR_RADIUS);
        KeyFrame blurFrame = new KeyFrame(duration, blurValue);
        blurTimeline.getKeyFrames().add(blurFrame);
        
        ParallelTransition showTransition = new ParallelTransition(popupFade, popupScale, blurTimeline);
        showTransition.play();
    }
    
    public static void hidePopup(Node popup, Node cover, MFXButton... buttonsToEnable) {
        hidePopup(popup, cover, DEFAULT_DURATION, buttonsToEnable);
    }
    
    public static void hidePopup(Node popup, Node cover, Duration duration, MFXButton... buttonsToEnable) {
        FadeTransition popupFade = new FadeTransition(duration, popup);
        popupFade.setFromValue(1);
        popupFade.setToValue(0);
        
        ScaleTransition popupScale = new ScaleTransition(duration, popup);
        popupScale.setFromX(1.0);
        popupScale.setFromY(1.0);
        popupScale.setToX(0.8);
        popupScale.setToY(0.8);
        
        Timeline blurTimeline = new Timeline();
        if (cover.getEffect() instanceof GaussianBlur) {
            GaussianBlur blurEffect = (GaussianBlur) cover.getEffect();
            KeyValue blurValue = new KeyValue(blurEffect.radiusProperty(), 0);
            KeyFrame blurFrame = new KeyFrame(duration, blurValue);
            blurTimeline.getKeyFrames().add(blurFrame);
        }
        
        ParallelTransition hideTransition = new ParallelTransition(popupFade, popupScale, blurTimeline);
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