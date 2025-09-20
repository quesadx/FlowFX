package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.service.PersonService;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Utility class for managing person label resolution, caching, and UI field updates.
 * 
 * <p>This utility provides comprehensive person name management functionality including:
 * <ul>
 *   <li>Synchronous and asynchronous person name resolution</li>
 *   <li>Intelligent caching system using AppContext for performance</li>
 *   <li>Direct UI field updates with loading states</li>
 *   <li>Thread-safe operations for concurrent access</li>
 *   <li>Fallback mechanisms for service unavailability</li>
 * </ul>
 * 
 * <p><strong>Caching Strategy:</strong><br>
 * Person labels are cached in AppContext with the key pattern "person.{ID}.label".
 * This provides application-wide consistency and reduces service calls.
 * 
 * <p><strong>Threading Model:</strong><br>
 * Async operations use daemon threads to prevent blocking the JavaFX Application Thread.
 * All UI updates are automatically marshaled to the FX Application Thread.
 * 
 * <p><strong>Error Handling:</strong><br>
 * Service failures gracefully degrade to displaying "ID: {personId}" format.
 * Invalid person IDs (≤ 0) are handled safely without service calls.
 * 
 * @author FlowFX Development Team
 * @version 1.0
 * @since 3.0
 */
public final class PersonLabelUtil {

    private static final Logger LOGGER = Logger.getLogger(PersonLabelUtil.class.getName());
    
    // Thread pool for async operations (using cached thread pool for efficiency)
    private static final ConcurrentHashMap<Long, CompletableFuture<String>> PENDING_REQUESTS = new ConcurrentHashMap<>();

    // Private constructor to prevent instantiation of utility class
    private PersonLabelUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Builds a display label for a person from PersonDTO.
     * 
     * @param person the PersonDTO to build a label for
     * @return formatted display name as "FirstName LastName", or empty string if person is null
     */
    public static String buildPersonLabel(PersonDTO person) {
        if (person == null) return "";
        
        String firstName = person.getFirstName() == null ? "" : person.getFirstName().trim();
        String lastName = person.getLastName() == null ? "" : person.getLastName().trim();
        
        return (firstName + " " + lastName).trim();
    }

    /**
     * Caches a person label in the application context.
     * 
     * @param personId the person ID to cache the label for
     * @param label the display label to cache
     */
    public static void cachePersonLabel(Long personId, String label) {
        if (personId != null && personId > 0 && label != null && !label.isBlank()) {
            AppContext.getInstance().set("person." + personId + ".label", label);
        }
    }

    /**
     * Retrieves a cached person label from the application context.
     * 
     * @param personId the person ID to retrieve the cached label for
     * @return the cached label, or null if not found or invalid
     */
    public static String getCachedPersonLabel(long personId) {
        if (personId <= 0) return null;
        
        Object label = AppContext.getInstance().get("person." + personId + ".label");
        return label instanceof String stringLabel ? stringLabel : null;
    }

    /**
     * Resolves a person's display name synchronously with caching.
     * 
     * @param personId the person ID to resolve
     * @return the person's display name, cached value, or fallback ID format
     */
    public static String resolvePersonNameSync(long personId) {
        if (personId <= 0) return null;
        
        // Check cache first
        String cachedLabel = getCachedPersonLabel(personId);
        if (cachedLabel != null && !cachedLabel.isBlank()) {
            return cachedLabel;
        }
        
        // Fetch from service
        try {
            PersonService personService = new PersonService();
            Respuesta response = personService.find(personId);
            
            if (Boolean.TRUE.equals(response.getEstado())) {
                Object personData = response.getResultado("Person");
                if (personData instanceof PersonDTO person) {
                    String personName = buildPersonLabel(person);
                    if (!personName.isBlank()) {
                        cachePersonLabel(personId, personName);
                        return personName;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.fine("Sync person fetch failed for ID " + personId + ": " + ex.getMessage());
        }
        
        // Fallback to ID display
        return String.valueOf(personId);
    }

    /**
     * Resolves a person's display name asynchronously and executes a callback.
     * 
     * @param personId the person ID to resolve
     * @param callback the callback to execute with the resolved name
     */
    public static void resolvePersonNameAsync(long personId, Consumer<String> callback) {
        if (personId <= 0) {
            callback.accept(null);
            return;
        }
        
        // Check cache first
        String cachedLabel = getCachedPersonLabel(personId);
        if (cachedLabel != null && !cachedLabel.isBlank()) {
            callback.accept(cachedLabel);
            return;
        }
        
        // Check if request is already pending
        CompletableFuture<String> existingRequest = PENDING_REQUESTS.get(personId);
        if (existingRequest != null) {
            existingRequest.thenAccept(callback);
            return;
        }
        
        // Create new async request
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                PersonService personService = new PersonService();
                Respuesta response = personService.find(personId);
                
                return processPersonResponse(response, personId);
            } catch (Exception ex) {
                LOGGER.fine("Async person fetch failed for ID " + personId + ": " + ex.getMessage());
                return "ID: " + personId;
            }
        }).whenComplete((result, throwable) -> {
            // Remove from pending requests when complete
            PENDING_REQUESTS.remove(personId);
        });
        
        // Store pending request to avoid duplicates
        PENDING_REQUESTS.put(personId, future);
        
        // Execute callback when complete
        future.thenAccept(callback);
    }

    /**
     * Updates a text field with the resolved person name, showing loading state during fetch.
     * 
     * @param personId the person ID to resolve and display
     * @param targetField the text field to update
     */
    public static void updatePersonLabelIntoField(long personId, MFXTextField targetField) {
        if (targetField == null || personId <= 0) {
            if (targetField != null) targetField.setText("");
            return;
        }
        
        // Check cache first for immediate update
        String cachedLabel = getCachedPersonLabel(personId);
        if (cachedLabel != null && !cachedLabel.isBlank()) {
            targetField.setText(cachedLabel);
            return;
        }
        
        // Show loading state and fetch asynchronously
        targetField.setText("Loading...");
        resolvePersonNameAsync(personId, resolvedName -> {
            Platform.runLater(() -> {
                if (resolvedName != null) {
                    targetField.setText(resolvedName);
                } else {
                    targetField.setText("");
                }
            });
        });
    }

    /**
     * Loads a person name if the ID is valid and updates the field.
     * 
     * @param personId the person ID to load
     * @param targetField the text field to update
     */
    public static void loadPersonNameIfValid(long personId, MFXTextField targetField) {
        if (personId > 0) {
            String personName = resolvePersonNameSync(personId);
            if (personName != null && !personName.equals(String.valueOf(personId))) {
                Platform.runLater(() -> targetField.setText(personName));
            }
        }
    }

    /**
     * Loads person names immediately for multiple fields during initialization.
     * 
     * @param personFields array of PersonField objects containing ID and target field pairs
     */
    public static void loadPersonNamesImmediately(PersonField... personFields) {
        for (PersonField field : personFields) {
            loadPersonNameIfValid(field.personId(), field.targetField());
        }
    }

    /**
     * Refreshes person labels in multiple fields by re-resolving from cache or service.
     * 
     * @param personFields array of PersonField objects containing ID and target field pairs
     */
    public static void refreshPersonLabels(PersonField... personFields) {
        for (PersonField field : personFields) {
            updatePersonLabelIntoField(field.personId(), field.targetField());
        }
    }

    /**
     * Prefetches person labels for the given IDs to warm the cache.
     * This is useful for improving UI responsiveness when you know which persons will be displayed.
     * 
     * @param personIds array of person IDs to prefetch
     * @param onComplete callback executed when all prefetching is complete (optional)
     */
    public static void prefetchPersonLabels(long[] personIds, Runnable onComplete) {
        if (personIds == null || personIds.length == 0) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        CompletableFuture<?>[] futures = new CompletableFuture[personIds.length];
        
        for (int i = 0; i < personIds.length; i++) {
            final long personId = personIds[i];
            
            if (personId <= 0 || getCachedPersonLabel(personId) != null) {
                // Skip if invalid or already cached
                futures[i] = CompletableFuture.completedFuture(null);
                continue;
            }
            
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    PersonService personService = new PersonService();
                    Respuesta response = personService.find(personId);
                    
                    if (Boolean.TRUE.equals(response.getEstado())) {
                        Object personData = response.getResultado("Person");
                        if (personData instanceof PersonDTO person) {
                            String personName = buildPersonLabel(person);
                            if (!personName.isBlank()) {
                                cachePersonLabel(personId, personName);
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.fine("Prefetch failed for person ID " + personId + ": " + ex.getMessage());
                }
            });
        }
        
        // Wait for all to complete and execute callback
        if (onComplete != null) {
            CompletableFuture.allOf(futures).thenRun(() -> Platform.runLater(onComplete));
        }
    }

    /**
     * Processes person service response and returns display text.
     * 
     * @param response the service response to process
     * @param personId the person ID for fallback display
     * @return the processed display text
     */
    private static String processPersonResponse(Respuesta response, long personId) {
        if (Boolean.TRUE.equals(response.getEstado())) {
            Object personData = response.getResultado("Person");
            if (personData instanceof PersonDTO person) {
                String personLabel = buildPersonLabel(person);
                if (!personLabel.isBlank()) {
                    cachePersonLabel(personId, personLabel);
                    return personLabel;
                }
            }
        }
        return "ID: " + personId;
    }

    /**
     * Record class to encapsulate person ID and target field pairs.
     * 
     * @param personId the person ID
     * @param targetField the MFXTextField to update
     */
    public record PersonField(long personId, MFXTextField targetField) {
        public PersonField {
            if (targetField == null) {
                throw new IllegalArgumentException("Target field cannot be null");
            }
        }
    }

    /**
     * Functional interface for person name resolution callbacks.
     */
    @FunctionalInterface
    public interface PersonNameResolver {
        /**
         * Resolves a person's display name by their ID.
         * 
         * @param personId the person ID to resolve
         * @return the person's display name, or null if not found
         */
        String resolvePersonName(long personId);
    }

    /**
     * Creates a PersonNameResolver that uses this utility's sync resolution.
     * 
     * @return a PersonNameResolver implementation
     */
    public static PersonNameResolver createSyncResolver() {
        return PersonLabelUtil::resolvePersonNameSync;
    }
}