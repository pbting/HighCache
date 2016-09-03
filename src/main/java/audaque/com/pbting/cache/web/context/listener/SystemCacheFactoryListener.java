package audaque.com.pbting.cache.web.context.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;

public class SystemCacheFactoryListener implements ServletContextListener {

	private GeneralCacheFactory cacheFactory = null ;
	
	private static final String CACHE_CONFIG_PATH = "cache.config.path";
	
	//得到一个初始化上下文
	private ServletContext application = null ;
	
	public void contextInitialized(ServletContextEvent sce) {
		
		application = sce.getServletContext();
		
		//得到缓存的配置文件的路径,没有给出的话，我就自己默认的配置,尽量减轻开发者的负担,就像struts 一样
		String paramCacheConfigPath = 
				application.getInitParameter(CACHE_CONFIG_PATH);
		
		//according the configuration file initialize the cacne factory
		GeneralCacheFactory.initByConfigPath(paramCacheConfigPath);//(properties);
		cacheFactory = GeneralCacheFactory.getInstance();
		
		//将缓存类别放在application 与范围内
		this.application.setAttribute(GeneralCacheFactory.CACHE_NAMES, cacheFactory.getMapCacheType());
		
		System.out.println("---------<系统初始化缓存工厂:"+cacheFactory+">----------");
	}

	public void contextDestroyed(ServletContextEvent sce) {
		
		cacheFactory = null ;
		
		System.out.println("------------销毁缓存工厂------------");
	}

	public GeneralCacheFactory getCacheFactory() {
		return cacheFactory;
	}
}
