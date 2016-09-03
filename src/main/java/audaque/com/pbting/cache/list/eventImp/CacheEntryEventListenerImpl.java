package audaque.com.pbting.cache.list.eventImp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.event.CacheEntryEvent;
import audaque.com.pbting.cache.event.CacheEntryEventListener;
import audaque.com.pbting.cache.event.CacheGroupEvent;
import audaque.com.pbting.cache.event.CachePatternEvent;
import audaque.com.pbting.cache.event.CachewideEvent;

public class CacheEntryEventListenerImpl implements CacheEntryEventListener {

	private final static Log log = LogFactory.getLog(CacheEntryEventListenerImpl.class);
	  /**
     * Counter for the cache flushes
     */
    private int cacheFlushedCount = 0;

    /**
     * Counter for the added entries
     */
    private int entryAddedCount = 0;

    /**
     * Counter for the flushed entries
     */
    private int entryFlushedCount = 0;

    /**
     * Counter for the removed entries
     */
    private int entryRemovedCount = 0;

    /**
     * Counter for the updated entries
     */
    private int entryUpdatedCount = 0;

    /**
     * Counter for the flushed groups
     */
    private int groupFlushedCount = 0;

    /**
     * Counter for the pattern flushes
     */
    private int patternFlushedCount = 0;

    /**
     * Constructor, empty for us
     */
    public CacheEntryEventListenerImpl() {
    }
    
    /**
     */
	public void cacheEntryAdded(CacheEntryEvent event) {
		
		if(log.isDebugEnabled()){
			
			log.debug("CacheEntry class:cacheEntryAdded is called.");
		}
		
		
		this.entryAddedCount++;
	}

	public void cacheEntryFlushed(CacheEntryEvent event) {
		
		if(log.isInfoEnabled()){
			log.info("CacheEntry class:cacheEntryFlushed is called.");
		}
		
		this.entryFlushedCount++;
//		
		System.out.println("缓存刷新的次数:"+this.entryFlushedCount);
	}

	public void cacheEntryRemoved(CacheEntryEvent event) {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:cacheEntryRemoved is called.");
		}
		
		this.entryRemovedCount++;
	}

	public void cacheEntryUpdated(CacheEntryEvent event) {

		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:cacheEntryUpdated is called");
		}

		this.entryUpdatedCount++;
	}

	public void cacheFlushed(CachewideEvent event) {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:cacheFlushed is called");
		}
		
		this.entryFlushedCount++;
	}

	public void cachePatternFlushed(CachePatternEvent event) {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:cachePatternFlushed is called.");
		}
		
		this.patternFlushedCount++;
	}
	/**
	 * @return the cacheFlushedCount
	 */
	public int getCacheFlushedCount() {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class: getCacheFlushedCount is called.");
		}
		
		return cacheFlushedCount;
	}
	/**
	 * @return the entryAddedCount
	 */
	public int getEntryAddedCount() {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:getEntryAddedCount is called.");
		}
		
		return entryAddedCount;
	}
	/**
	 * @return the entryFlushedCount
	 */
	public int getEntryFlushedCount() {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class: getEntryFlushedCount is called.");
		}
		
		return entryFlushedCount;
	}
	/**
	 * @return the entryRemovedCount
	 */
	public int getEntryRemovedCount() {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:getEntryRemovedCount is called.");
		}
		
		return entryRemovedCount;
	}
	/**
	 * @return the entryUpdatedCount
	 */
	public int getEntryUpdatedCount() {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class:getEntryUpdatedCount is called.");
		}
		
		return entryUpdatedCount;
	}
	/**
	 * @return the groupFlushedCount
	 */
	public int getGroupFlushedCount() {
		
		if(log.isInfoEnabled()){
			
			log.info("CacheEntry class: getGroupFlushedCount is called.");
		}
		
		return groupFlushedCount;
	}
	/**
	 * @return the patternFlushedCount
	 */
	public int getPatternFlushedCount() {
		return patternFlushedCount;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

	public void cacheGroupFlushed(CacheGroupEvent event) {
		
	}
}
