package audaque.com.pbting.cache.event;

import audaque.com.pbting.cache.base.info.CacheEntry;
import audaque.com.pbting.cache.base.info.HighCache;

/**
 * @author pbting
 *
 */
public class CacheEntryEvent extends CacheEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HighCache nchuCache ;
	
	private CacheEntry cacheEntry ;
	
	
	public CacheEntryEvent(Object source) {
		super(source);
	}
	
	/**
	 * 
	 */
	public CacheEntryEvent(HighCache nchuCache,CacheEntry cacheEntry,Object orignalEvent){
		super(orignalEvent);
		this.nchuCache = nchuCache;
		this.cacheEntry = cacheEntry;
	}

	public HighCache getNchuCache() {
		return nchuCache;
	}

	public CacheEntry getCacheEntry() {
		return cacheEntry;
	}
	
	/**
	 * @return
	 */
	public Object getKey(){
		
		return cacheEntry.getKey();
	}
}
