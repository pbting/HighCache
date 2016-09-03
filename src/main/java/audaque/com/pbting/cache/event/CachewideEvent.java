package audaque.com.pbting.cache.event;

import java.util.Date;

import audaque.com.pbting.cache.base.info.HighCache;

public class CachewideEvent extends CacheEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CachewideEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	private HighCache nchuCache;

	private Date date;

	public CachewideEvent(HighCache cache, Date date, Object origin) {
		super(origin);
		this.date = date;
		this.nchuCache = cache;
	}

	/**
	 * Retrieve the cache map that the event occurred on.
	 */
	public HighCache getCache() {
		return nchuCache;
	}

	/**
	 * Retrieve the date/time that the cache flush is scheduled for.
	 */
	public Date getDate() {
		return date;
	}
}
