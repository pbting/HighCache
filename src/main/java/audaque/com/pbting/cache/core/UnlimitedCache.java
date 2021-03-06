package audaque.com.pbting.cache.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnlimitedCache extends AbstractMapCache {

	  private static final long serialVersionUID = 7615611393249532285L;
	    
	    private final Log log = LogFactory.getLog(this.getClass());

	    /**
	     * Creates an unlimited cache by calling the super class's constructor
	     * with an <code>UNLIMITED</code> maximum number of entries.
	     */
	    public UnlimitedCache() {
	        super();
	        maxEntries = UNLIMITED;
	    }

	    /**
	     * Overrides the <code>setMaxEntries</code> with an empty implementation.
	     * This property cannot be modified and is ignored for an
	     * <code>UnlimitedCache</code>.
	     */
	    public void setMaxEntries(int maxEntries) {
	    	log.warn("Cache max entries can't be set in " + this.getClass().getName() + ", ignoring value " + maxEntries + ".");
	    }

	    /**
	     * Implements <code>itemRetrieved</code> with an empty implementation.
	     * The unlimited cache doesn't care that an item was retrieved.
	     */
	    public void itemRetrieved(Object key) {
	    }

	    /**
	     * Implements <code>itemPut</code> with an empty implementation.
	     * The unlimited cache doesn't care that an item was put in the cache.
	     */
	    public void itemPut(Object key) {
	    }

	    /**
	     * This method just returns <code>null</code> since items should
	     * never end up being removed from an unlimited cache!
	     */
	    public Object removeItem() {
	        return null;
	    }

	    /**
	     * An empty implementation. The unlimited cache doesn't care that an
	     * item was removed.
	     */
	    public void itemRemoved(Object key) {
	    }

}
