package audaque.com.pbting.cache.event;

public class CachewideEventType {

	  /**
     * Get an event type for a cache flush event.
     */
    public static final CachewideEventType CACHE_FLUSHED = new CachewideEventType();

    /**
     * Private constructor to ensure that no object of this type are
     * created externally.
     */
    private CachewideEventType() {
    }
}
