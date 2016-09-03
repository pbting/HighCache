package audaque.com.pbting.cache.base.refresh.policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.info.CacheEntry;

public class ExpiresRefreshPolicy implements EntryRefreshPolicy {

	/**
	 * 日志输出
	 */
	private final Log log = LogFactory.getLog(ExpiresRefreshPolicy.class);

	private static final long serialVersionUID = 1L;

	/** 
	 * 
	 */
	private int refreshPeriod;

	public ExpiresRefreshPolicy() {
		super();
	}

	public ExpiresRefreshPolicy(int refreshPeriod) {
		super();
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * @return the refreshPeriod in seconds
	 */
	public int getRefreshPeriod() {
		return refreshPeriod;
	}

	/**
	 * @param refreshPeriod the refresh period in seconds
	 */
	public void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * 
	 */
	public boolean needsRefresh(CacheEntry entry, int refreshPeriod) {

		boolean needsRefresh = false;

		if ((refreshPeriod >= 0) && (System.currentTimeMillis() > (entry.getLastUpdate() + refreshPeriod))) {
			log.info("ExpiresRefreshPolicy-key:"+entry.getKey()+" 需要刷新");
			needsRefresh = true;
		}
		return needsRefresh;
	}
}
