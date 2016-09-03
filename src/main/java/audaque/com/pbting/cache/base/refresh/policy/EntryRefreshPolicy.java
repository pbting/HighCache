package audaque.com.pbting.cache.base.refresh.policy;

import java.io.Serializable;

import audaque.com.pbting.cache.base.info.CacheEntry;

public interface EntryRefreshPolicy extends Serializable {

	 public boolean needsRefresh(CacheEntry entry,int refreshPeriod);
}
