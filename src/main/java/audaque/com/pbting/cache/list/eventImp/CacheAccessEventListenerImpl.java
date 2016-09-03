package audaque.com.pbting.cache.list.eventImp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.event.CacheAccessEvent;
import audaque.com.pbting.cache.event.CacheAccessEventListener;
import audaque.com.pbting.cache.event.CacheAccessEventType;

/**
 * @author pbting
 *
 */
public class CacheAccessEventListenerImpl implements CacheAccessEventListener {

	private final static Log log = 
						LogFactory.getLog(CacheAccessEventListenerImpl.class);
	
	 /**
     * Hit counter
     */
    private int hitCount = 0;

    /**
     * Miss counter
     */
    private int missCount = 0;

    /**
     * Stale hit counter
     */
    private int staleHitCount = 0;

    private float hitProportion = 0.0F;
    
    private int totalAccess = 0 ;
    
    /**
     * Constructor, empty for us
     */
    public CacheAccessEventListenerImpl() {
    }

    /**
     * Returns the cache's current hit count
     *
     * @return The hit count
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * Returns the cache's current miss count
     *
     * @return The miss count
     */
    public int getMissCount() {
        return missCount;
    }

    /**
     * Returns the cache's current stale hit count
     */
    public int getStaleHitCount() {
        return staleHitCount;
    }
	
    
    public String getHitProportion() {
		return new StringBuffer().append(hitProportion).append("%").toString();
	}

	public int getTotalAccess() {
		return totalAccess;
	}

	public void setTotalAccess(int totalAccess) {
		this.totalAccess = totalAccess;
	}

	/**
     * in here will follow the state of access the cache container
     */
	public void accessed(CacheAccessEvent event) {
		//will handle based on the type of event
		CacheAccessEventType type = (CacheAccessEventType) event.getCacheEventType();
		
		this.totalAccess++;
		
		try{
			if(type == CacheAccessEventType.HIT){//is hit in cache
				
				this.hitCount++;
				log.info("[HighCache]：congratulations is hit in cache，and the count of hit is:"+this.hitCount);
			}else if(type == CacheAccessEventType.MISS){//is missing in cache
				
				this.missCount++;
				log.info("[HighCache]:sorry is miss in cache,and the count of missing is:"+this.missCount);
			}else if(type == CacheAccessEventType.STALE_HIT){
				
				this.staleHitCount++;
				log.info("[HighCache]:sorry is stale hit in cahce,and the count of stale is :"+this.staleHitCount);
			}else{
				
				throw new IllegalArgumentException("无效参数");
			}
		}finally{
			
			this.hitProportion = Float.valueOf(this.hitCount)/Float.valueOf(this.totalAccess)*100;
		}
	}

	/**
	 */
	public void reSet(){
		this.hitCount = 0 ;
		this.missCount = 0 ;
		this.staleHitCount = 0 ;
	}
	
	public String toString()
	{
        return ("Hit count = " + hitCount + ", stale hit count = " + staleHitCount + " and miss count = " + missCount);
	}
}
