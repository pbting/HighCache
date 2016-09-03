package audaque.com.pbting.cache.event;

import audaque.com.pbting.cache.base.info.HighCache;


public class CacheGroupEvent extends CacheEvent {

	/**
	 */
	private static final long serialVersionUID = 1L;

	private HighCache nchuCache;

	private String groupName;

	public CacheGroupEvent(Object source) {
		super(source);
	}

	public CacheGroupEvent(HighCache nchuCache, Object eventSource, String groupName) {
		super(eventSource);
		this.nchuCache = nchuCache;
		this.groupName = groupName;
	}

	public HighCache getNchuCache() {
		return nchuCache;
	}
	public String getGroupName() {
		return "[group name] :"+ groupName;
	}
}
