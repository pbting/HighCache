package audaque.com.pbting.cache.base.persistence;

import java.util.Set;

import audaque.com.pbting.cache.base.info.CacheConfig;
import audaque.com.pbting.cache.exception.FilePersistException;

public interface FilePersistListener {

	/**
	 * 
	 */
	public void clear() throws FilePersistException;
	
	/**
	 * 
	 */
	public FilePersistListener config(String topic,CacheConfig cacheConfig);
	
	/**
	 * 
	 */
	public boolean isGroupStored(String groupName);
	
	/**
	 */
	public boolean isStored(String key);
	
	/**
	 * 
	 */
	
	public boolean remove(String key) throws FilePersistException;
	
	/**
	 * 
	 */
	public boolean removeGroup(String key) throws FilePersistException;
	
	/**
	 * 
	 */
	public Object retrieveCache(String type,String topic,String key)throws FilePersistException;
	
	/**
	 */
	public Set retrieveGroupCache(String groupName)throws FilePersistException;
	
	/**
	 * 
	 */
	public void store(String type,String topic,String key,Object value) throws FilePersistException; 
	
	/**
	 * 
	 */
	public void storeGroup(String groupName,Set Valve)throws FilePersistException;
	
}
