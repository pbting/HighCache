package audaque.com.pbting.cache.event;

import java.util.Date;

public class ScopeEvent extends CacheEvent {

	/**
	 */
	private static final long serialVersionUID = 1L;

	private Date date;

	private int scope = 0;

	 private ScopeEventType eventType = null;
	
	/**
	 * @param source
	 */
	public ScopeEvent(Object source) {
		super(source);
	}

	public ScopeEvent(ScopeEventType eventType, int scope, Date date, String origin) {
		super(origin);
		this.eventType = eventType;
		this.scope = scope;
		this.date = date;
	}

	/**
	 */
	public Date getDate() {
		return date;
	}

	/**
	 */
	public ScopeEventType getEventType() {
		return (ScopeEventType) this.getSource();
	}

	/**
	 */
	public int getScope() {
		return scope;
	}
}
