package audaque.com.pbting.cache.factory;

import java.io.Serializable;

public interface CacheFactory extends Serializable{

	/**
	 */
	public final static String CACHE_MEMORY_KEY = "cache.memory";

	/**
	 */
	public final static String CACHE_CAPACITY_KEY = "cache.capacity";

	/**
	 */
	public final static String CACHE_ALGORITHM_KEY = "cache.algorithm";

	/**
	 */
	public final static String CACHE_REFRESH_POLICY = "cache.refresh.policy";

	/**
	 */
	public final static String CACHE_DISK_UNLIMITED_KEY = "cache.unlimited.disk";

	/**
	 * the value is cache or msg.if cache then data can read random or msg type read sequence
	 */
	public final static String CACHE_TYPE = "cache.type" ;
	
	public final static String LAZY_CREATE = "lazy.create";
	
	/**
	 */
	public final static String CACHE_BLOCKING_KEY = "cache.blocking";

	/**

	 */
	public static final String PERSISTENCE_CLASS_KEY = "cache.persistence.class";

	/**
	 * A String cache configuration property that specifies if the cache
	 * persistence will only be used in overflow mode, that is, when the memory
	 * cache capacity has been reached.cache.persistence.overflow.only
	 */
	public static final String CACHE_PERSISTENCE_OVERFLOW_KEY = "cache.persistence.overflow.only";

	/**
	 * A String cache configuration property that holds a comma-delimited list
	 * of classnames. These classes specify the event handlers that are to be
	 * applied to the cache.
	 */
	public static final String CACHE_ENTRY_EVENT_LISTENERS_KEY = "cache.event.listeners";

	public final static String REFRESH_PERIOD = "refresh.period";
	
	public final static String CACHE_NAMES ="cache.topic";
}
