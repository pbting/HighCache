package audaque.com.pbting.cache.list.eventImp;

import audaque.com.pbting.cache.event.ScopeEvent;
import audaque.com.pbting.cache.event.ScopeEventListener;
import audaque.com.pbting.cache.event.ScopeEventType;

/**
 *
 */
public class ScopeEventListenerImpl implements ScopeEventListener {

	/**
     */
    public static final String[] SCOPE_NAMES = {
        null, "page", "request", "session", "application"
    };

    /**
     * Number of known scopes
     */
    public static final int NB_SCOPES = SCOPE_NAMES.length - 1;

    /**
     * Page scope number
     */
    public static final int PAGE_SCOPE = 1;

    /**
     * Request scope number
     */
    public static final int REQUEST_SCOPE = 2;

    /**
     * Session scope number
     */
    public static final int SESSION_SCOPE = 3;

    /**
     * Application scope number
     */
    public static final int APPLICATION_SCOPE = 4;

    /**
     */
    private int[] scopeFlushCount = new int[NB_SCOPES + 1];

    public ScopeEventListenerImpl() {
    }

    /**
     * @throws IllegalAccessException 
     */
	public void scopeFlushed(ScopeEvent event) {
		
		ScopeEventType scopeEventType = event.getEventType();
		
		if(scopeEventType == ScopeEventType.ALL_SCOPES_FLUSHED){
			//
			for(int i= 1 ;i<= NB_SCOPES;i++){
				scopeFlushCount[i]++;
			}
		}else if(scopeEventType == ScopeEventType.SCOPE_FLUSHED){
			
			scopeFlushCount[event.getScope()]++;
		}else 
			 throw new IllegalArgumentException("illegal argument,please check the input");
	}
	

	/**
	 * @return the scopeNames
	 */
	public static String[] getScopeNames() {
		return SCOPE_NAMES;
	}

	/**
	 * @return the nbScopes
	 */
	public static int getNbScopes() {
		return NB_SCOPES;
	}

	/**
	 * @return the pageScope
	 */
	public static int getPageScope() {
		return PAGE_SCOPE;
	}

	/**
	 * @return the requestScope
	 */
	public static int getRequestScope() {
		return REQUEST_SCOPE;
	}

	/**
	 * @return the sessionScope
	 */
	public static int getSessionScope() {
		return SESSION_SCOPE;
	}

	/**
	 * @return the applicationScope
	 */
	public static int getApplicationScope() {
		return APPLICATION_SCOPE;
	}

	/**
	 * @return the scopeFlushCount
	 */
	public int[] getScopeFlushCount() {
		return scopeFlushCount;
	}

	/**
	 * @param scopeFlushCount the scopeFlushCount to set
	 */
	public void setScopeFlushCount(int[] scopeFlushCount) {
		this.scopeFlushCount = scopeFlushCount;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
