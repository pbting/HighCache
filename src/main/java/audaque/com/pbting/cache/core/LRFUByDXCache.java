package audaque.com.pbting.cache.core;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 最少使用缓存置换算法的实现latest-seldom use cache
 * 
 * @author pbting
 *
 */
public class LRFUByDXCache extends LRFUAbstractCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LRFUByDXCache() {
		super();
	}

	public LRFUByDXCache(int capacity) {

		super(capacity);
		this.maxEntries = capacity;
	}

	private final static Log log = LogFactory.getLog(LFUCache.class);

	private ReentrantLock lock = new ReentrantLock();

	@Override
	public float getFactor(SRUKey sruKey) {
		long currentTimeInterval = 
					System.currentTimeMillis()- createTime+EFACTOR;
//System.out.println(currentTimeInterval+"<--------"+sruKey.key+"--------->"+createTime);		
		try {
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// ();
			
			ArrayList<Long> timeSequence = sruKey.timeSequence;
			float factor = 0;
			// 算出这个时间序列的数学期望
			for (Long ts : timeSequence) {
				factor += (Float.valueOf(ts) / currentTimeInterval) * ts;

			}

			// 计算出来的结果太大，会影响均方差的计算导无穷大的情况，因此要进行一个降值的超过
			float EX = (float) (Math.abs(factor) / (Math.pow(2, 26)));

			// 开始计算均方差
			float dx = 0f;

			for (Long ts : timeSequence) {
				dx += (ts - EX) * (ts - EX)*(Float.valueOf(ts)/currentTimeInterval);
			}
//System.out.println(sruKey.key+"------>EX:"+EX + "------------->DX:" + dx);
			// 返回他的均方差
			return (float) Math.sqrt(dx);
		} finally {

			lock.unlock();
		}
	}
}
