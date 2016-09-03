 package audaque.com.pbting.cache.event;

/**
 * @author pbting
 *
 */
public final class CacheAccessEventType {

	public static final CacheAccessEventType HIT = new CacheAccessEventType();
	
	 /**
     */
    public static final CacheAccessEventType MISS = new CacheAccessEventType();

    /**
     */
    public static final CacheAccessEventType STALE_HIT = new CacheAccessEventType();
    
    private CacheAccessEventType(){}
}
