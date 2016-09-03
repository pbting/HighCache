package audaque.com.pbting.cache.event;

public interface CacheAccessEventListener extends CacheEventListener{
	
	/**
	 * @param event 
	 */
	 public void accessed(CacheAccessEvent event);
}
