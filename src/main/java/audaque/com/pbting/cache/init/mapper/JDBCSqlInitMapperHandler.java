package audaque.com.pbting.cache.init.mapper;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * 使用原始的JDBC 来做任务
 * 
 * @author pbting
 *
 */
public class JDBCSqlInitMapperHandler extends CacheSqlInitMapperHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JDBCSqlInitMapperHandler(String initConfig) throws DocumentException {

		super(initConfig);
	}

	protected static String sqlExecutorClassName = null;

	@Override
	public void initSqlExexutor(Element sqlExecutorEle) {
		if (sqlExecutorEle == null)
			throw new RuntimeException(
					"the class name of sql executor is null,please check it carefully.");

		sqlExecutorClassName = sqlExecutorEle.getTextTrim();
		System.out.println("-----sql executor-------:"+ this.sqlExecutorClassName);
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

	
	public String getSqlExecutorClassName() {
		return sqlExecutorClassName;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public JDBCConMapperSqlExecutor getJdbcConMapperSqlExecutor()
			throws Exception {
		System.out.println("-----sql executor:" + this.sqlExecutorClassName);
		return (JDBCConMapperSqlExecutor) Class.forName(sqlExecutorClassName)
				.newInstance();
	}
}
