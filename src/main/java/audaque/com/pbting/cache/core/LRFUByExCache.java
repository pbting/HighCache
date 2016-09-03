package audaque.com.pbting.cache.core;

import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 最少使用缓存置换算法的实现latest-seldom use cache
 * 
 * @author pbting
 *
 */
public class LRFUByExCache extends LRFUAbstractCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LRFUByExCache() {
		super();
	}

	public LRFUByExCache(int capacity) {

		super(capacity);
		this.maxEntries = capacity;
	}

	private final static Log log = LogFactory.getLog(LFUCache.class);

	/**
	 * 这部分代码需要同步
	 */
	private ReentrantLock lock = new ReentrantLock();

	@Override
	public float getFactor(SRUKey sruKey) {
		long currentTimeInterval = 
				System.currentTimeMillis()- this.createTime+EFACTOR;
		try {

			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			float factor = 0;

			// 算出这个时间序列的数学期望,这部分代码需要保持同步，在读的情况保证其他线程不能往里面写数据
			for (Long ts : sruKey.timeSequence) {
				factor += (Float.valueOf(ts) / currentTimeInterval) * ts;

			}
			
			float tempFactor = (float) Math.pow(sruKey.count,5.2);
			return Math.abs(Math.abs(factor)-tempFactor);
		} finally {
			lock.unlock();
		}
	}

}
