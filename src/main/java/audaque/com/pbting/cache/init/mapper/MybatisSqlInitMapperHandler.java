
package audaque.com.pbting.cache.init.mapper;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * 
 * @author pbting
 *
 */
public class MybatisSqlInitMapperHandler extends CacheSqlInitMapperHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String sqlExecutorClassName = null ;
	
	public MybatisSqlInitMapperHandler(String initConfig) throws DocumentException{
		super(initConfig);
	}
	
	@Override
	public void initSqlExexutor(Element sqlExecutorEle) {
		if(sqlExecutorEle == null)
			throw new RuntimeException("the class name of sql executor is null,please check it carefully.");
		
		try {
			sqlExecutorClassName = sqlExecutorEle.getTextTrim();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 处理数据需要存放的缓存容器名称，
		public void initHighCache(Element highCacheName) {
			if (highCacheName == null) {

				super.sqlInitDataCacheName = this.sqlExecutorClassName;
				log.info("HighCache:the high cache name is empty so that the high cache system will use the name of sqlExecutor instead it["
						+ this.sqlInitDataCacheName + "].");
			} else {

				String name = highCacheName.getTextTrim();
				if (StringUtils.isEmpty(name)) {// 这个时候防止用户只写了标签元素。但未给起名，默认情况下用的是任务执行器的名称
					super.sqlInitDataCacheName = this.sqlExecutorClassName;
					log.info("HighCache:the high cache name is empty so that the high cache system will use the name of sqlExecutor instead it["
							+ this.sqlInitDataCacheName + "].");
				} else {
					super.sqlInitDataCacheName = name.trim();
					log.info("HighCache:the high cache name of the configuration file is:"
							+ sqlInitDataCacheName);
				}
			}
		}
	
	public MybatisMapperSqlExecutor getMybatisMapperSqlExecutor() {
		try {
			return (MybatisMapperSqlExecutor) Class.forName(sqlExecutorClassName).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null ;
	}
}
