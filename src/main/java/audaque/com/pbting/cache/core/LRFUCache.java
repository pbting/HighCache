package audaque.com.pbting.cache.core;

public class LRFUCache extends LRFUAbstractCache {

	/**
	 * 基于这种的实现只考虑最后两次的访问时间差，记忆无记忆的最近最少缓存置换策略。
	 */
	private static final long serialVersionUID = 1L;

	public LRFUCache(){
		super();
	}
	
	public LRFUCache(int capacity){
		
		super(capacity);
		this.maxEntries = capacity;
	}
	
	@Override
	public float getFactor(SRUKey sruKey) {

		return System.currentTimeMillis()-sruKey.lastAccessTime+EFACTOR;
	}

}
