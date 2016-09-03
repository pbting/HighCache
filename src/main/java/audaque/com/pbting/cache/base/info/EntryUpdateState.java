package audaque.com.pbting.cache.base.info;

/**
 * @author pbting
 *
 */
public class EntryUpdateState {

	 /**
     */
    public static final int NOT_YET_UPDATING = -1;

    /**
     * 
     */
    public static final int UPDATE_IN_PROGRESS = 0;

    /**
     * 
     */
    public static final int UPDATE_COMPLETE = 1;

    /**
     * 
     */
    public static final int UPDATE_CANCELLED = 2;

    /**
     * 
     */
    int state = NOT_YET_UPDATING;
    
    private int nbConcurrentUses = 1;

    public boolean isAwaitingUpdate() {
        return state == NOT_YET_UPDATING;
    }

    public boolean isCancelled() {
        return state == UPDATE_CANCELLED;
    }

    public boolean isComplete() {
        return state == UPDATE_COMPLETE;
    }

    public boolean isUpdating() {
        return state == UPDATE_IN_PROGRESS;
    }

    public int cancelUpdate() {
        if (state != UPDATE_IN_PROGRESS) {
            throw new IllegalStateException("Cannot cancel cache update - current state (" + state + ") is not UPDATE_IN_PROGRESS");
        }

        state = UPDATE_CANCELLED;
        return decrementUsageCounter();
    }

    public int completeUpdate() {
        if (state != UPDATE_IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete cache update - current state (" + state + ") is not UPDATE_IN_PROGRESS");
        }

        state = UPDATE_COMPLETE;
        return decrementUsageCounter();
    }

    public int startUpdate() {
        if ((state != NOT_YET_UPDATING) && (state != UPDATE_CANCELLED)) {
            throw new IllegalStateException("Cannot begin cache update - current state (" + state + ") is not NOT_YET_UPDATING or UPDATE_CANCELLED");
        }

        state = UPDATE_IN_PROGRESS;
       
        return incrementUsageCounter();
    }

	public synchronized int incrementUsageCounter() {
		return ++nbConcurrentUses;
	}
	
	public synchronized int getUsageCounter() {
		return nbConcurrentUses;
	}
	
	
	public synchronized int decrementUsageCounter() {
		if (nbConcurrentUses <=0) {
            throw new IllegalStateException("Cannot decrement usage counter, it is already equals to [" + nbConcurrentUses + "]");
		}
		return --nbConcurrentUses;
	}
}
