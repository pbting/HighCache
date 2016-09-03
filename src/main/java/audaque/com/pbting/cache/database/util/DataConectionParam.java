package audaque.com.pbting.cache.database.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import audaque.com.pbting.cache.web.context.listener.HighCacheDBPoolContextListener;

/**
 * 根据配置文件来初始化数据库连接的相关属性
 * @author pbting
 *
 */
public class DataConectionParam {

	private final static String maxPoolSize = "maxPoolSize";
	private final static String minPoolSize = "minPoolSize";
	private final static String maxIdleTime = "maxIdleTime";
	
	
	private Properties properties =  new Properties() ;
	//设置c3p0参数
	private Map<String, Object> c3p0_pool_conf = null;
	
	public DataConectionParam(String propertiesCfg) throws IOException{
		
		properties.load(
				HighCacheDBPoolContextListener.class.getClassLoader().getResourceAsStream(propertiesCfg));
		c3p0_pool_conf =new HashMap<String, Object>();
		//最大连接数
		c3p0_pool_conf.put(maxPoolSize, properties.get(maxPoolSize));
		//最小连接数
		c3p0_pool_conf.put(minPoolSize, properties.get(minPoolSize));
		//最长连接时间
		c3p0_pool_conf.put(maxIdleTime, properties.get(maxIdleTime));
	}
	
	public String getUserName(String key){
		
		return properties.getProperty(key);
	}
	
	public String getPassword(String key){
		
		return properties.getProperty(key);
	} 
	
	public String getDriver(String key){
		
		return properties.getProperty(key);
	}
	
	public String getUrl(String key){
		
		return properties.getProperty(key);
	}

	public Map<String, Object> getC3p0_pool_conf() {
		return c3p0_pool_conf;
	}

	public void setC3p0_pool_conf(Map<String, Object> c3p0_pool_conf) {
		this.c3p0_pool_conf = c3p0_pool_conf;
	}
}
