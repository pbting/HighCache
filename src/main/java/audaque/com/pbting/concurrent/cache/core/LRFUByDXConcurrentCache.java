package audaque.com.pbting.concurrent.cache.core;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 最少使用缓存置换算法的实现latest-seldom use cache
 * 
 * @author pbting
 */
public class LRFUByDXConcurrentCache extends LRFUAbstractConcurrentCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LRFUByDXConcurrentCache() {
		super();
	}

	public LRFUByDXConcurrentCache(int capacity) {

		super(capacity);
		this.maxEntries = capacity;
	}

	private ReentrantLock lock = new ReentrantLock();

	@Override
	public float getFactor(SRUKey sruKey) {
		long currentTimeInterval = System.currentTimeMillis() - createTime + EFACTOR;
		try {
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // ();

			// ArrayList<Long> timeSequence = sruKey.timeSequence;
			// float factor = 0;
			// // 算出这个时间序列的数学期望
			// for (Long ts : timeSequence) {
			// factor += (Float.valueOf(ts) / currentTimeInterval) * ts;
			// }

			sruKey.lastFactor += (Float.valueOf(sruKey.lastIntervalTime) / currentTimeInterval) * sruKey.lastIntervalTime;

			// 计算出来的结果太大，会影响均方差的计算导无穷大的情况，因此要进行一个降值的超过
			float EX = (float) (Math.abs(sruKey.lastFactor*sruKey.lastFactor) / (Math.pow(3, 6)));

			// 开始计算均方差
			// float dx = 0f;
			//
			// for (Long ts : timeSequence) {
			// dx += (ts - EX) * (ts - EX)*(Float.valueOf(ts)/currentTimeInterval);
			// }

			sruKey.lastDx += ((sruKey.lastIntervalTime - EX) * (sruKey.lastIntervalTime - EX) * (Float.valueOf(sruKey.lastIntervalTime) / currentTimeInterval));
			// 返回他的均方差
			return (float) Math.sqrt(sruKey.lastDx);
		} finally {

			lock.unlock();
		}
	}

}
