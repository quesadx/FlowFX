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
 * - {@link #delete(String)} retains the original semantics (logically sets the
 *   key's value to {@code null} rather than removing the key). Because
 *   {@link ConcurrentHashMap} does not permit storing {@code null} values, a
 *   private sentinel object is stored instead to represent a logical null.
 * - A separate {@link #remove(String)} convenience method is provided to
 *   actually remove the mapping.</p>
 *
 * This class is final to prevent subclassing and uses the
 * initialization-on-demand holder idiom to provide a thread-safe
 * lazy-loaded singleton instance.
 */
public final class AppContext {

    /**
     * Sentinel object used to represent a logical null value because
     * ConcurrentHashMap does not allow storing nulls.
     */
    private static final Object NULL_SENTINEL = new Object();

    /**
     * Internal context storage. Uses ConcurrentHashMap for safe
     * concurrent access without external synchronization.
     * Values equal to NULL_SENTINEL represent a logical null mapping.
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
     * @return the object associated with {@code key}, or {@code null} if none or if logically null
     */
    public Object get(String key) {
        Object v = context.get(key);
        return v == NULL_SENTINEL ? null : v;
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
        context.put(key, value == null ? NULL_SENTINEL : value);
    }

    /**
     * Preserves historical behavior: logically sets the mapping for {@code key} to {@code null}.
     *
     * <p>Note: this intentionally does not remove the mapping. A sentinel object
     * is stored internally to represent the logical null so that callers relying
     * on key presence continue to function without triggering a NullPointerException
     * from the underlying ConcurrentHashMap.</p>
     *
     * @param key the key whose mapping should be logically set to {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void delete(String key) {
        Objects.requireNonNull(key, "key must not be null");
        context.put(key, NULL_SENTINEL);
    }

    /**
     * Removes the mapping for a key from this context if it is present.
     *
     * @param key the key whose mapping is to be removed
     * @return the previous (logical) value associated with {@code key}, or {@code null}
     *         if there was no mapping or if the mapping represented a logical null
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Object remove(String key) {
        Objects.requireNonNull(key, "key must not be null");
        Object prev = context.remove(key);
        return prev == NULL_SENTINEL ? null : prev;
    }

    /**
     * Returns {@code true} if this context contains a mapping for the specified key.
     *
     * @param key the key whose presence is to be tested
     * @return {@code true} if a mapping for {@code key} exists (even if value is logically null)
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
