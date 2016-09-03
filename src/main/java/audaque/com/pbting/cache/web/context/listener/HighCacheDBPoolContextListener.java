package audaque.com.pbting.cache.web.context.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.database.util.HighCacheDBPool;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;
import audaque.com.pbting.cache.init.mapper.JDBCConMapperSqlExecutor;
import audaque.com.pbting.cache.init.mapper.JDBCSqlInitMapperHandler;
import audaque.com.pbting.cache.init.task.JDBCInitDataDelayTask;

public class HighCacheDBPoolContextListener extends CacheSystemInitMapperListener {

	//数据库连接池的连接需要指定相关的连接属性，最基本的就是那几个参数
	private final static String DBPOOLCFG = "dbpoolcfg";
	
	private ServletContext application = null ;
	
	private HighCacheDBPool cacheDBPool = null ;
	
	private JDBCSqlInitMapperHandler sqlInitMapperHandler = null ;
	
	public void contextInitialized(ServletContextEvent arg0) {
		application = arg0.getServletContext();
		
		//得到配置的文件
		try {
			cacheDBPool = new HighCacheDBPool(application.getInitParameter(DBPOOLCFG));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//看有没有初始化sql 执行的任务
		String initMapperFile = application.getInitParameter(INIT_MAPPER_FILE);
		
		if(!StringUtils.isEmpty(initMapperFile)){//不为空的情况下
			try {
				//这一初始化 ，所有已经配置好的sql 语句和sql 任务执行器 都已经准备好接下来就是开启执行sql 任务的 线程跑一定的任务
				sqlInitMapperHandler  = new JDBCSqlInitMapperHandler(initMapperFile);
				
				Map<String,HighCache> cacheTypes = 
						(HashMap)application.getAttribute(GeneralCacheFactory.CACHE_NAMES);
				//开启任务
				JDBCInitDataDelayTask timerTask = 
						new JDBCInitDataDelayTask(sqlInitMapperHandler, 
						cacheTypes.get(sqlInitMapperHandler.getSqlInitDataCacheName()), cacheDBPool);
				
				//任务开始调度
				new Timer().schedule(timerTask,sqlInitMapperHandler.getDelay(),sqlInitMapperHandler.getRefreshPeriod());
				
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void contextDestroyed(ServletContextEvent sce) {

		
	}
	
	private JDBCConMapperSqlExecutor mapperSqlExcutor = null ;
	
}
