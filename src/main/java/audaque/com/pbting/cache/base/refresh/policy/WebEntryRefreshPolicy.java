package audaque.com.pbting.cache.base.refresh.policy;

public interface WebEntryRefreshPolicy extends EntryRefreshPolicy {
	 /**
     * Initializes the refresh policy.
     *
     * @param key   The cache key that is being checked.
     * @param param Any optional parameters that were supplied
     */
    public void init(String key, String param);

}
