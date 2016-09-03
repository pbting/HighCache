package audaque.com.pbting.concurrent.cache.core;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 最近最少使用缓存置换算法的实现latest-seldom use cache
 * 
 * @author pbting
 */
public class LRFUByExConcurrentCache extends LRFUAbstractConcurrentCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LRFUByExConcurrentCache() {
		super();
	}

	public LRFUByExConcurrentCache(int capacity) {

		super(capacity);
		this.maxEntries = capacity;
	}

	/**
	 * 这部分代码需要同步
	 */
	private ReentrantLock lock = new ReentrantLock();

	@Override
	public float getFactor(SRUKey sruKey) {
		try {
			long currentTimeInterval = System.currentTimeMillis() - super.createTime + EFACTOR;
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sruKey.lastFactor += ((Float.valueOf(sruKey.lastIntervalTime) / currentTimeInterval) * sruKey.lastIntervalTime);
			return Math.abs(Math.abs(sruKey.lastFactor) - (float) Math.pow(sruKey.count, 5.2));
		} finally {
			lock.unlock();
		}
	}
}
