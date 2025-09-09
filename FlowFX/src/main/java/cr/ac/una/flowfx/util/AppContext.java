package cr.ac.una.flowfx.util;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application-wide context for sharing simple key/value objects.
 *
 * <p>This class provides a thread-safe singleton container for
 * storing and retrieving objects by string keys. It is intentionally
 * lightweight and does not attempt to manage lifecycle of stored
 * objects beyond keeping references.</p>
 *
 * <p>Important behavior notes preserved from the original implementation:
 * - {@link #delete(String)} retains the original semantics (sets the
 *   key's value to {@code null} rather than removing the key). This
 *   avoids changing behavior of callers that rely on presence of the
 *   key with a null value. A separate {@link #remove(String)} convenience
 *   method is provided to actually remove the mapping.</p>
 *
 * This class is final to prevent subclassing and uses the
 * initialization-on-demand holder idiom to provide a thread-safe
 * lazy-loaded singleton instance.
 */
public final class AppContext {

    /**
     * Internal context storage. Uses ConcurrentHashMap for safe
     * concurrent access without external synchronization.
     */
    private final ConcurrentHashMap<String, Object> context =
        new ConcurrentHashMap<>();

    // Prevent external instantiation
    private AppContext() {
        // Intentionally empty.
    }

    // Holder class idiom for lazy-loaded, thread-safe singleton
    private static final class Holder {

        private static final AppContext INSTANCE = new AppContext();
    }

    /**
     * Returns the singleton instance of the application context.
     *
     * @return the global {@code AppContext} instance
     */
    public static AppContext getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Prevent cloning of the singleton.
     *
     * @throws CloneNotSupportedException always
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException(
            "AppContext is a singleton and cannot be cloned"
        );
    }

    /**
     * Retrieves the object associated with the specified key.
     *
     * @param key the key whose value is to be returned (may be {@code null})
     * @return the object associated with {@code key}, or {@code null} if none
     */
    public Object get(String key) {
        return context.get(key);
    }

    /**
     * Associates the specified value with the specified key in this context.
     * If the context previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to associate (may be {@code null})
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void set(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        context.put(key, value);
    }

    /**
     * Preserves historical behavior: sets the mapping for {@code key} to {@code null}.
     *
     * <p>Note: this intentionally does not remove the mapping. Callers that expect
     * the original semantics should continue to use this method. To actually remove
     * the mapping, use {@link #remove(String)}.</p>
     *
     * @param key the key whose mapping should be set to {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void delete(String key) {
        Objects.requireNonNull(key, "key must not be null");
        context.put(key, null);
    }

    /**
     * Removes the mapping for a key from this context if it is present.
     *
     * @param key the key whose mapping is to be removed
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Object remove(String key) {
        Objects.requireNonNull(key, "key must not be null");
        return context.remove(key);
    }

    /**
     * Returns {@code true} if this context contains a mapping for the specified key.
     *
     * @param key the key whose presence is to be tested
     * @return {@code true} if a mapping for {@code key} exists (even if value is {@code null})
     */
    public boolean containsKey(String key) {
        return context.containsKey(key);
    }

    /**
     * Clears all entries from the context.
     *
     * <p>Note: This is provided as a convenience for tests or application shutdown
     * flows. Use with care in production code.</p>
     */
    public void clear() {
        context.clear();
    }

    @Override
    public String toString() {
        return "AppContext{entries=" + context.size() + "}";
    }
}
