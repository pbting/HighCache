package audaque.com.pbting.cache.event;

/**
 * @author pbting
 *
 */
public final class CacheEntryEventType {

	private CacheEntryEventType(){}
	
	
	 /**
     */
    public static final CacheEntryEventType ENTRY_ADDED = new CacheEntryEventType();

    /**
     */
    public static final CacheEntryEventType ENTRY_UPDATED = new CacheEntryEventType();

    /**
     * Get an event type for an entry flushed.
     */
    public static final CacheEntryEventType ENTRY_FLUSHED = new CacheEntryEventType();

    /**
     * Get an event type for an entry removed.
     */
    public static final CacheEntryEventType ENTRY_REMOVED = new CacheEntryEventType();

    /**
     * Get an event type for a group flush event.
     */
    public static final CacheEntryEventType GROUP_FLUSHED = new CacheEntryEventType();

    /**
     * Get an event type for a pattern flush event.
     */
    public static final CacheEntryEventType PATTERN_FLUSHED = new CacheEntryEventType();
}
