package audaque.com.pbting.cache.event;

public interface ScopeEventListener extends CacheEventListener{
	 public void scopeFlushed(ScopeEvent event);
}
