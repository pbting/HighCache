package audaque.com.pbting.cache.event;

import audaque.com.pbting.cache.base.info.CacheEntry;

public class CacheAccessEvent extends CacheEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param source
	 */
	public CacheAccessEvent(Object source) {
		super(source);
	}

	private CacheEntry cacheEntry ;
	
	private CacheAccessEventType cacheAccessEventType ;
	
	/**
	 * 
	 */
	public CacheAccessEvent(CacheAccessEventType cacheAccessEventType,CacheEntry cacheEntry){
		this(cacheAccessEventType,cacheEntry,null);
	}
	
	 public CacheAccessEvent(CacheAccessEventType eventType, CacheEntry entry, Object origin) {
	        super(origin);
	       this.cacheAccessEventType = eventType;
	       this.cacheEntry = entry;
	    }
	
	public CacheEntry getCacheEntry(){
		
		return this.cacheEntry;
	}
	
	public Object getCacheEntryKey(){
		
		return this.cacheEntry.getKey();
	}
	
	public CacheAccessEventType getCacheEventType(){
		
		return this.cacheAccessEventType;
	}
}
