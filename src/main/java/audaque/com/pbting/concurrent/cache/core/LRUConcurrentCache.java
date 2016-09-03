package audaque.com.pbting.concurrent.cache.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;

/**
 * LRU（long-recently） 最近最久未使用，是根据上一次访问时间到目前为止最长的时间进行替换，在这段时间内不考虑他的使用频率，是考虑他的
 * 访问时间
 * @author pbting
 *
 */
public class LRUConcurrentCache extends AbstractConcurrentCache {

	private static transient final Log log = LogFactory
			.getLog(GeneralCacheFactory.class);

	private static final long serialVersionUID = -7379608101794788534L;

	private Collection list = 
				Collections.synchronizedCollection( new LinkedHashSet());

	private volatile boolean removeInProgress = false;

	public LRUConcurrentCache() {
		super();
	}

	public LRUConcurrentCache(int capacity) {
		super(capacity);
		maxEntries = capacity;
	}

	//这是为改变key的顺序而为子类留下的接口
	public void itemRetrieved(Object key) {

		// Prevent list operations during remove
		while (removeInProgress) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException ie) {
			}
		}

		//原子操作需同步
		synchronized (list) {
			list.remove(key);
			list.add(key);
		}
	}
	/**
	 * 往里面放一个元素时，不管之前有没有，都remove掉，然后插入到
	 */
	public void itemPut(Object key) {
		synchronized (list) { //
			list.remove(key);
			list.add(key);
		}
	}

	public Object removeItem() {

		Object toRemove = null;

		removeInProgress = true;
		try {
			//how to enter remove succcess
			while (toRemove == null) {
				try {
					toRemove = removeFirst();
				} catch (Exception e) {
					do {
						try {
							Thread.sleep(5);
						} catch (InterruptedException ie) {
						}
					} while (list.isEmpty());
				}
			}
		} finally {
			removeInProgress = false;
		}

		return toRemove;
	}

	public void itemRemoved(Object key) {

		list.remove(key);
	}

	private Object removeFirst() {

		Object toRemove = null;

		synchronized (list) { // A further fix for CACHE-44 and CACHE-246
			Iterator it = list.iterator();
			toRemove = it.next();
			it.remove();//
		}

		return toRemove;
	}
}
