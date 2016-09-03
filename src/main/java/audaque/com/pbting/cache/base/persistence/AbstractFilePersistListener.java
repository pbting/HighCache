package audaque.com.pbting.cache.base.persistence;

import java.io.File;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.info.CacheConfig;
import audaque.com.pbting.cache.base.info.CacheEntry;
import audaque.com.pbting.cache.exception.FilePersistException;
import audaque.com.pbting.cache.house.DataMgr;
import audaque.com.pbting.cache.house.MsgBody;
import audaque.com.pbting.cache.util.StringUtils;

public abstract class AbstractFilePersistListener implements
		FilePersistListener {

	private final static Log log = LogFactory.getLog(AbstractFilePersistListener.class);
	
	/**
	 * 
	 */
	protected final static String GROUP_DIRECTORY = "_groups_path_";

	/**
	 * 
	 */
	protected final static String CACHE_PATH_KEY = "cache.path";

	// 
	protected final static String FILE_EXTION = ".cache";

	// application
	protected final static String APPLICATION_CACHE_PATH = "application";

	// seesion 
	protected final static String SESSION_CACHE_PATH = "session";

	// web
	protected static final String CONTEXT_TMPDIR = "javax.servlet.context.tempdir";

	//
	protected File cachePath = null;

	// web
	protected File webContextTempDir = null;

	// 
	private String rootPath = null;

	/**
	 * 
	 */
	public void clear() throws FilePersistException {

		this.clear(rootPath);
	}

	/**
	 * @param rootDirName
	 * @throws FilePersistException
	 */
	private void clear(String rootDirName) throws FilePersistException {
		
		if(log.isDebugEnabled()){
			
			log.debug(":[clear="+rootDirName+"]");
		}
		
		File rootFile = new File(rootDirName);

		File[] fileList = rootFile.listFiles();

		// 
		try {
			if (fileList != null) {

				for (int i = 0; i < fileList.length; i++) {

					if (fileList[i].isFile()) {

						fileList[i].delete();
					} else {

						this.clear(fileList[i].toString());//
						fileList[i].delete();//
					}
				}
			}

			//
			rootFile.delete();
		} catch (Exception e) {
			throw new FilePersistException("");
		}
	}

	public FilePersistListener config(String topic,CacheConfig cacheConfig) {

		initFileCaching(cacheConfig.getProperty(CACHE_PATH_KEY));
		
		StringBuffer root = new StringBuffer(getCachePath().getPath());
		root.append("/");
		root.append(StringUtils.isEmpty(topic)?this.APPLICATION_CACHE_PATH:topic);
		this.rootPath = root.toString();
		DataMgr.init(topic,rootPath);
		return this;
	}

	public File getCachePath() {
		return cachePath;
	}

	// will see the cache file whether exists or not
	public boolean isGroupStored(String groupName) {
		try {

			return this.getGroupFile(groupName).exists();
		} catch (Exception e) {

			return false;
		}
	}

	public boolean isStored(String key) {
		try {
			File file = this.getCacheFile(key);

			return file.exists();
		} catch (Exception e) {

			return false;
		}
	}

	public boolean remove(String key) throws FilePersistException {
		try {
			File file = this.getCacheFile(key);
			
			this.remove(file);

			return true;
		} catch (Exception e) {

			return false;
		}
	}

	public boolean removeGroup(String key) throws FilePersistException {

		File file;
		try {
			file = getGroupFile(key);
			this.remove(file);
			return true;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Object retrieveCache(String type,String topic,String key) throws FilePersistException {
		
		File file = this.getCacheFile(type);
		
		if("cache".equals(type)){
			return this.retrieve(file);
		}else if("msg".equals(type)){
			return DataMgr.retrieve(topic,file.getAbsolutePath(), key);
		}else{
			log.error("从缓存中获取数据时参数 type 错误!");
		}
		
		return null ;
	}

	public Set retrieveGroupCache(String groupName) throws FilePersistException {
		try {
			File file = this.getGroupFile(groupName);

			return (Set) this.retrieve(file);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void store(String type,String topic,String key, Object value) throws FilePersistException {
		if("cache".equals(type)){
			this.cacheStore(this.getCacheFile(type), value);
		}else if("msg".equals(type)){
			this.MsgStore(topic,this.getCacheFile(type), value);
		}else{
			log.error(AbstractFilePersistListener.class.getName()+"store() method : the data type is error.please check config file of that [cache.type]");
		}
	}

	/**
	 * the group name is that have stored the group object and these file will
	 * put under the group root directory
	 * 
	 * @param groupName
	 * @return
	 * @throws IllegalAccessException
	 */
	protected File getGroupFile(String groupName) throws IllegalAccessException {
		// 1 the first step is that get the file name of the group
		if (StringUtils.isEmpty(groupName))
			throw new IllegalAccessException(
					"the input of the group name is null,please input the right of the name!");

		char[] name = this.getCacheFileName(groupName);

		StringBuffer sb = new StringBuffer(30);

		sb.append(this.GROUP_DIRECTORY).append("/").append(this.FILE_EXTION);

		return new File(rootPath, sb.toString());
	}

	// will get the file of cache directly
	protected File getCacheFile(String key) {
		char[] fileName = this.getCacheFileName(key);

		return new File(rootPath, new String(fileName) + this.FILE_EXTION);
	}

	/**
	 * Sub path name for application cache
	 */
	protected final static String APPLICATION_CACHE_SUBPATH = "application";

	/**
	 * Sub path name for session cache
	 */
	protected final static String SESSION_CACHE_SUBPATH = "session";

	// will based on group name get the file of the related group file
	protected abstract char[] getCacheFileName(String key);

	/**
	 *
	 */
	protected String adjustFileCachePath(String cacheFilePath) {

		if (this.CONTEXT_TMPDIR.compareToIgnoreCase(cacheFilePath) == 0) {

			cacheFilePath = this.webContextTempDir.getAbsolutePath();
		}

		return cacheFilePath;
	}

	/**
	 * init file cacheing by the file path
	 * @param cacheFilePath
	 */
	protected void initFileCaching(String cacheFilePath) {
		if (cacheFilePath != null) {
			this.cachePath = new File(cacheFilePath);
			System.err.println("cache file path is :"+this.cachePath.getAbsolutePath());
			try {

				if (!this.cachePath.exists()) {//不存在，则创建
					if (log.isDebugEnabled()) {

						log.info("HighCache:the file directory of cache is:" + cacheFilePath + "");
					}

					//create new dirs
					this.cachePath.mkdirs();

				}

				// judge the path whether is directory or not 
				if (!this.cachePath.isDirectory()) {//如果配置的不是目录，则删除，并置为null
					if (log.isErrorEnabled()) {

						log.error("[" + this.cachePath.getAbsolutePath()+ "]");
					}

					this.cachePath = null;
					return ;
				}

				// 
				if (!this.cachePath.canWrite()) {
					if (log.isErrorEnabled()) {

						log.error("[" + this.cachePath.getAbsolutePath()+ "]");
					}

					this.cachePath = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	private static final long DELETE_THREAD_SLEEP = 500;

	private static final int DELETE_COUNT = 60;

	protected void remove(File file) throws FilePersistException {
		int count = DELETE_COUNT;

		try {
			//
			while (file.exists() && !file.delete() && count != 0) {
				count--;

				try {
					Thread.sleep(DELETE_THREAD_SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (file.exists() && count == 0) {

			throw new FilePersistException("HighCache:[" + file.getName() + "]cano't delete it");
		}
	}

	public void MsgStore(String topic,File file,Object value){
		if(value instanceof CacheEntry){
			DataMgr.store(topic,(CacheEntry)value, file.getAbsolutePath(),false);
		}
	}
	
	/**
	 * 
	 * @throws
	 */
	public void cacheStore(File file, Object value) throws FilePersistException {
		DataMgr.store(file, value);
	}

	/**
	 * 
	 */
	private Object retrieve(File file) {
		return DataMgr.retrieve(file);
	}
}
