package audaque.com.pbting.concurrent.cache.core;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FIFOConcurrentCache extends AbstractConcurrentCache {

	private static final long serialVersionUID = -10333778645392679L;

	private ConcurrentLinkedQueue<Object> linkedQuence = new ConcurrentLinkedQueue<Object>();

	public FIFOConcurrentCache() {
		super();
	}

	public FIFOConcurrentCache(int cacpcity) {
		super(cacpcity);
		this.maxEntries = cacpcity;
	}

	@Override
	public void itemPut(Object key) {
		if (!linkedQuence.contains(key))
			linkedQuence.add(key);
	}

	@Override
	public void itemRetrieved(Object key) {
		linkedQuence.remove(key);
		linkedQuence.add(key);
	}

	@Override
	public void itemRemoved(Object key) {
		linkedQuence.remove(key);
	}

	@Override
	public Object removeItem() {

		return linkedQuence.poll();
	}
}
