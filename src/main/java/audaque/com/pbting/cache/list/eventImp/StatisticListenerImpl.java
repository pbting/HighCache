package audaque.com.pbting.cache.list.eventImp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.event.CacheAccessEvent;
import audaque.com.pbting.cache.event.CacheAccessEventListener;
import audaque.com.pbting.cache.event.CacheAccessEventType;
import audaque.com.pbting.cache.event.CacheEntryEvent;
import audaque.com.pbting.cache.event.CacheGroupEvent;
import audaque.com.pbting.cache.event.CachePatternEvent;
import audaque.com.pbting.cache.event.CachewideEvent;
import audaque.com.pbting.cache.event.ScopeEvent;

public class StatisticListenerImpl implements CacheAccessEventListener {

	public Log log = LogFactory.getLog(StatisticListenerImpl.class);
	
    /**
     * Hit counter.
     */
    private static int hitCount = 0;

    /**
     * Miss counter.
     */
    private static int missCount = 0;

    /**
     * Stale hit counter.
     */
    private static int staleHitCount = 0;

    /**
     * Hit counter sum.
     */
    private static int hitCountSum = 0;

    /**
     * Miss counter sum.
     */
    private static int missCountSum = 0;

    /**
     * Stale hit counter.
     */
    private static int staleHitCountSum = 0;

    /**
     */
    private static int flushCount = 0;
    
    /**
     */
    private static int entriesAdded = 0;

    /**
     */
    private static int entriesRemoved = 0;

    /**
     */
    private static int entriesUpdated = 0;

    /**
     * Constructor, empty for us.
     */
    public StatisticListenerImpl() {

    }
	
    /**
     */
	public void accessed(CacheAccessEvent event) {
		
		CacheAccessEventType cacheAccessEventType = event.getCacheEventType();
		
		if(cacheAccessEventType == CacheAccessEventType.HIT ){
			
			this.hitCount++;
		}else if(cacheAccessEventType == CacheAccessEventType.MISS){
			
			this.missCount++;
		}else if(cacheAccessEventType == CacheAccessEventType.STALE_HIT){
			
			this.staleHitCount++;
		}else {
			
			throw new IllegalArgumentException("illegal argument please check the input ");
		}
		
	}

	/**
	 * @param infor
	 */
	private void flushed(String infor){
		
		if(log.isDebugEnabled())
			log.info(infor);
		
		this.flushCount++;
		
		this.hitCountSum += this.hitCount;
		this.missCountSum += this.missCountSum;
		this.staleHitCountSum += this.staleHitCount;
		
		this.hitCount = 0 ;
		this.missCount = 0;
		this.staleHitCount = 0 ;
		
	}
	
	/**
	 * @param scopeEvent
	 */
	public void scopeFlushed(ScopeEvent scopeEvent ){
		
		this.flushed("scope : "+ ScopeEventListenerImpl.SCOPE_NAMES[scopeEvent.getScope()]);
	}
	
	public void cacheEntryAdd(CacheEntryEvent cacheEntryEvent){
		
		this.entriesAdded++;
	}
	
	public void cacheEntryFlushed(CacheEntryEvent cacheEntryEvent) {
		
		if(!cacheEntryEvent.getSource().toString().equals(HighCache.NESTED_EVENT)){
			
			this.flushed("entry :"+ cacheEntryEvent.getKey() +" /" + cacheEntryEvent.getSource().toString());
		}
	}
	
	public void cacheEntryRemoved(CacheEntryEvent cacheEntryEvent) {
		
		this.entriesRemoved++;
	}
	
	public void cacheEntryUpdated(CacheEntryEvent cacheEntryEvent) {
		
		this.entriesUpdated++;
	}
	
	public void cacheGroupFlushed(CacheGroupEvent cacheGroupEvent){
		
		this.flushed("group :" + cacheGroupEvent.getSource().toString());
	}
	
	public void cachePatternFlushed(CachePatternEvent cachePatternEvent){
		this.flushed("pattern :"+ cachePatternEvent.getPattern());
	}
	
	public void cacheFlushed(CachewideEvent cachewideEvent){
		
		this.flushed("wide:"+cachewideEvent.getDate());
	}
	
	@Override
	public String toString() {
		
		return "StatisticListenerImpl: Hit = " + hitCount + " / " + hitCountSum
        + ", stale hit = " + staleHitCount + " / " + staleHitCountSum
        + ", miss = " + missCount + " / " + missCountSum + ", flush = "
        + flushCount + ", entries (added, removed, updates) = " 
        + entriesAdded + ", " + entriesRemoved + ", " + entriesUpdated;
	}

	/**
	 * @return the hitCount
	 */
	public static int getHitCount() {
		return hitCount;
	}

	/**
	 * @return the missCount
	 */
	public static int getMissCount() {
		return missCount;
	}

	/**
	 * @return the staleHitCount
	 */
	public static int getStaleHitCount() {
		return staleHitCount;
	}

	/**
	 * @return the hitCountSum
	 */
	public static int getHitCountSum() {
		return hitCountSum;
	}

	/**
	 * @return the missCountSum
	 */
	public static int getMissCountSum() {
		return missCountSum;
	}

	/**
	 * @return the staleHitCountSum
	 */
	public static int getStaleHitCountSum() {
		return staleHitCountSum;
	}

	/**
	 * @return the flushCount
	 */
	public static int getFlushCount() {
		return flushCount;
	}

	/**
	 * @return the entriesAdded
	 */
	public static int getEntriesAdded() {
		return entriesAdded;
	}

	/**
	 * @return the entriesRemoved
	 */
	public static int getEntriesRemoved() {
		return entriesRemoved;
	}

	/**
	 * @return the entriesUpdated
	 */
	public static int getEntriesUpdated() {
		return entriesUpdated;
	}
}
