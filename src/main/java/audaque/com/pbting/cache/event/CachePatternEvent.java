package audaque.com.pbting.cache.event;

import audaque.com.pbting.cache.base.info.HighCache;

public class CachePatternEvent extends CacheEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CachePatternEvent(Object source) {
		super(source);
	}

	private HighCache nchuCache ;
	
	private String pattern ;
	
	public CachePatternEvent(HighCache nchuCache,String pattern,Object eventSource){
		super(eventSource);
		this.nchuCache = nchuCache;
		this.pattern = pattern;
	}

	public HighCache getNchuCache() {
		return nchuCache;
	}

	public String getPattern() {
		return pattern;
	}
}
