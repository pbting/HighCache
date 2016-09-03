package audaque.com.pbting.cache.web.context.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 读取需要预加载数据的配置文件
 * @author Administrator
 *
 */
public abstract class CacheSystemInitMapperListener implements ServletContextListener {
	
	//初始化 mapper 的映射文件
	protected final static String INIT_MAPPER_FILE = "init.mapper.file";
	
	public abstract void contextInitialized(ServletContextEvent sce) ;

	public abstract void contextDestroyed(ServletContextEvent sce);
}
