package audaque.com.pbting.cache.base.info;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import audaque.com.pbting.cache.base.refresh.policy.EntryRefreshPolicy;

/**
 * @author pbting
 */
public class CacheEntry implements Serializable {

	/**
	 * 这个缓存实体最终将会是被序列化到本地的，因此要根据具体的情况来判断哪些字段需要被序列化
	 */
	private transient static final long serialVersionUID = 1L;

	private transient static final byte NOT_YET = -1;

	public transient static final int INDEFINITE_EXPIRY = -1;

	/**
	 * The entry refresh policy object to use for this cache entry. This is optional.
	 */
	private transient EntryRefreshPolicy policy = null;

	/**
	 * The actual content that is being cached. Wherever possible this object should be serializable. This allows
	 * <code>PersistenceListener</code>s to serialize the cache entries to disk or database.
	 */
	private Object content = null;

	/**
	 * The set of cache groups that this cache entry belongs to, if any.
	 */
	private Set groups = null;

	/**
	 * The unique cache key for this entry
	 */
	private Object key;

	/**
	 * The time this entry was created.
	 */
	private long created = NOT_YET;

	/**
	 * The time this emtry was last updated.
	 */
	private long lastUpdate = NOT_YET;//

	/**
	 * Construct a new CacheEntry using the key provided.
	 * 
	 * @param key The key of this CacheEntry
	 */
	public CacheEntry(Object key,Object content) {
		this(key, content,null);
	}

	public CacheEntry(Object key,Object content,EntryRefreshPolicy policy) {
		this(key,content,policy,null);
	}
	
	public CacheEntry(Object key,Object content,EntryRefreshPolicy policy, String[] groups) {
		this.key = key;
		this.content = content;
		if (groups != null) {
			this.groups = new HashSet(groups.length);

			for (int i = 0; i < groups.length; i++) {
				this.groups.add(groups[i]);
			}
		}

		this.policy = policy;
		/**
		 * 记录当前缓存实体的创建时间,仅仅是创建，对缓存实体的访问的定义是当第一次去缓存容器（这个时候就不管是在以及缓存还是二级缓存） 中要数据时并且命中未过期的情况下，采取更新这一次的最后访问时间
		 */
		this.created = System.currentTimeMillis();
		this.lastUpdate = this.created;

	}

	//
	public synchronized void setContent(Object value) {
		content = value;
		System.out.println("put in cache will update the lastUpdate property." + this.lastUpdate);
	}

	public Object getContent() {
		return content;
	}

	public long getCreated() {
		return created;
	}

	public EntryRefreshPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(EntryRefreshPolicy policy) {
		this.policy = policy;
	}

	public synchronized void setGroups(String[] groups) {
		if (groups != null) {
			this.groups = new HashSet(groups.length);

			for (int i = 0; i < groups.length; i++) {
				this.groups.add(groups[i]);
			}
		} else {
			this.groups = null;
		}
		// 这里又会再一次更新 该属性
		lastUpdate = System.currentTimeMillis();
	}

	public synchronized void setGroups(Collection groups) {
		if (groups != null) {
			this.groups = new HashSet(groups);
		} else {
			this.groups = null;
		}

		lastUpdate = System.currentTimeMillis();
	}

	public Set getGroups() {
		return groups;
	}

	/**
	 * Get the key of this CacheEntry
	 * 
	 * @return The key of this CacheEntry
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Set the date this CacheEntry was last updated.
	 * 
	 * @param update The time (in milliseconds) this CacheEntry was last updated.
	 */
	public void setLastUpdate(long update) {
		lastUpdate = update;
	}

	/**
	 * Get the date this CacheEntry was last updated.
	 * 
	 * @return The date this CacheEntry was last updated.
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Indicates whether this CacheEntry is a freshly created one and has not yet been assigned content or placed in a
	 * cache.
	 * 
	 * @return <code>true</code> if this entry is newly created
	 */
	public boolean isNew() {
		return lastUpdate == NOT_YET;//
	}

	// to judge whether a cache entry need to refresh
	public boolean needsRefresh(int refreshPeriod) {
		return policy.needsRefresh(this, refreshPeriod);
	}

	/**
	 * 刷新他的创建时间和最后一次访问的时间，以全新的状态
	 */
	public void flush() {
		this.lastUpdate = System.currentTimeMillis();
	}
}