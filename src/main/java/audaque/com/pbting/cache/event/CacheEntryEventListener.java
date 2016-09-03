package audaque.com.pbting.cache.event;

/**
 * 
 * @author pbting
 * 
 */
public interface CacheEntryEventListener extends CacheEventListener{

	/**
	 */
	public void cacheEntryAdded(CacheEntryEvent event);

	/**
	 */
	public void cacheEntryFlushed(CacheEntryEvent event);

	/**
	 */
	public void cacheEntryRemoved(CacheEntryEvent event);

	/**
	 */
	public void cacheEntryUpdated(CacheEntryEvent event);
	
	/**
	 * an event fired when a cache group will be flush
	 */
	public void cacheFlushed(CachewideEvent event);
	
	/**
	 * an event fired when a pattern flushed 
	 */
	public void cachePatternFlushed(CachePatternEvent event);
	
	/**
	 * @param event
	 */
	public void cacheGroupFlushed(CacheGroupEvent event);
	
}
