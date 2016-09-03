package audaque.com.pbting.cache.web.context.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dom4j.DocumentException;

import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.database.util.MybatisUtil;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;
import audaque.com.pbting.cache.init.mapper.MybatisSqlInitMapperHandler;
import audaque.com.pbting.cache.init.task.LoadInitDataDelayTask;
import audaque.com.pbting.cache.init.task.MybatisInitDataDelayTask;

/**
 * 在系统启动的时候，就应该实现一个与数据库保持连接的工厂，然后
 * 在这个上下文监听器中，有一个参数是必须给出的，那就是关于mybatis 的配置文件。
 * 这个类通常是在是什么时候用呢？因为系统的CRUD 的操作有可能不是使用mybatis 工具类，那么当需要
 * 系统初始化加载数据时，这个时候可能需要完成比较复杂的操作，如果系统不是使用mybatis的话，如果系统使用的是mybatis
 * 持久层工具，那么配置该监听器将使得非常的方便，整个过程需要完成以下步骤:
 * 
 * 1、在web.xml 文件中 给出mybatis 的配置文件路径，
 * 
 * 2、给出需要初始化映射的sql语句列表的 文件 路径
 * 
 * 3、实现一个任务执行器，来处理相应的sql语句
 * 
 * 这个类还有另外一个作用就是：当系统需要使用mybatis提供的session 类进行CRUD 的操作时，那么这个时候需要完成以下几个操作就可以了
 * 
 * 1、在spring 配置文件中配置好需要实例化该bean
 * 2、在DAO 组件中使用Autowire 注解放在该字段上，接下来就可以使用该对象来获取的session进行相关的操作了
 * 
 * 还有另外一中方法就是直接使用该类的一个静态方法获得一个session,然后进行crud 操作,
 * 
 * @author Administrator
 *
 */

public class SystemInitSqlSessionListener extends CacheSystemInitMapperListener{
	
	private Log log = LogFactory.getLog(SystemInitSqlSessionListener.class);
	
	private final static String MYBATIS_PARAM = "mybatisCfg";
	
	private static SqlSessionFactory sqlSessionFactory = null;
	
	protected MybatisSqlInitMapperHandler cacheSqlInitMapperHandler = null;
	
	public SystemInitSqlSessionListener(){}
	
	public void contextDestroyed(ServletContextEvent arg0) {
		
		//系统关闭的时候，将关闭该session factory 
		this.sqlSessionFactory = null ;
	}

	private ServletContext application = null ;
	
	public void contextInitialized(ServletContextEvent arg0) {
		
		this.application = arg0.getServletContext();
		
		String configPath = application.getInitParameter(MYBATIS_PARAM);
		
		log.info("initialize the sqlSession factory of mybatis.");
		//初始化 sql session factory
		sqlSessionFactory = MybatisUtil.getSqlSessionFactory(configPath);
		
		/**
		 * whether you need here will deal with preloading cold or hot data
		 * 调用父类的方法 初始化
		 * 
		 */
		//先调用父类的处理方法,解析出需要预先执行的SQL 语句
		
		String initMapperFile = application.getInitParameter(INIT_MAPPER_FILE);
		
		if(!StringUtils.isEmpty(initMapperFile)){
			//如果配置不为空，则做处理，
			try {
				cacheSqlInitMapperHandler = 
						new MybatisSqlInitMapperHandler(initMapperFile.trim());
				
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		
		this.initLoadStart(getSession(true));
	}
	
	//如果配置了sql 任务执行器，则启动执行
	protected void initLoadStart(SqlSession session){
		
		Map<String,HighCache> cacheTypes = 
				(HashMap)application.getAttribute(GeneralCacheFactory.CACHE_NAMES);
		//如果配置了初始化文件，则才将任务运行起来.
		if(cacheSqlInitMapperHandler != null){
			LoadInitDataDelayTask loadInitDataDelayTask =
					new MybatisInitDataDelayTask(session,cacheSqlInitMapperHandler.getMapperSql(),
							cacheSqlInitMapperHandler.getMybatisMapperSqlExecutor(),
							cacheTypes.get(cacheSqlInitMapperHandler.getSqlInitDataCacheName())
							);
			
			//这个任务开始跑起来
			new Thread(loadInitDataDelayTask).start();
		}
	}
	
	//得到一个session factory
	public static SqlSessionFactory getSqlSessionFactory(){
		
		return sqlSessionFactory;
	}
	
	//向外提供一个可以获得session 的方法
	
	public static SqlSession getSession(){
		
		return sqlSessionFactory.openSession();
	}
	
	//
	public static SqlSession getSession(boolean isAutoCommit){
		
		return sqlSessionFactory.openSession(isAutoCommit);
	}
	
	public static void close(SqlSession session){
		MybatisUtil.closeSqlSession(session);
	}
	
	
	public static void close(SqlSession session,boolean isCommit){
		
		MybatisUtil.closeSqlSession(session, isCommit);
	}
	
	
}
