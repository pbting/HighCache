package audaque.com.pbting.cache.init.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import audaque.com.pbting.cache.exception.RefreshPeriodException;

/**
 * 读取init mapper 的配置文件
 * 
 * @author Administrator
 *
 */
public abstract class CacheSqlInitMapperHandler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final static Log log = LogFactory
			.getLog(CacheSqlInitMapperHandler.class);

	// sql 任务执行器
	private final static String SQL_EXECUTOR = "sqlExecutor";

	// 每个多长时间执行一次任务
	private final static String REFRESH_PERIOD = "refreshPeriod";

	// 默认的每个5秒钟去执行一个任务
	private final static Long DEFUALT_REFRESH_PERIOD = 5000L;

	//定义这个在xml 文件中配置的元素名称
	private final static String DELAY = "delay";
	//定义这个任务延时多长时间才执行
	private Long delay = 0L ;
	//默认延迟5分钟执行，
	private final static Long DEFAULT_DELAY = (long) (3 * 1000);
	
	// 这些数据需要存放在一个缓存容器，给定这个缓存容器的名称
	private final static String HIGH_CACHE_NAME = "highCache";
	// 如果没有给出，则这个缓存容器的默认名称就是这个sql任务执行的类全路径

	// 这里应该存储所有已经配置好的mapper sql 语句，
	private Set<String> mapperSql = null;

	// 指定任务的刷新周期，以秒为单位
	private Long refreshPeriod = 5000L;

	protected String sqlInitDataCacheName = null;

	public CacheSqlInitMapperHandler() {
	}

	public CacheSqlInitMapperHandler(String initConfig)
			throws DocumentException {

		this.parse2(initConfig);
	}

	@Deprecated
	private Set<String> parse(String initConfig) throws DocumentException {

		SAXReader reader = new SAXReader();

		InputStream is = CacheSqlInitMapperHandler.class.getClassLoader()
				.getResourceAsStream(initConfig);

		Document document = reader.read(is);

		Element element = document.getRootElement();

		// get all kinds mapper elements

		List<Element> mapperElements = element.elements("mapper");

		mapperSql = new HashSet<String>(mapperElements.size());

		for (Iterator<Element> iter = mapperElements.iterator(); iter.hasNext();) {

			Element mapperE = iter.next();

			mapperSql.add(mapperE.attributeValue("id"));
		}
		// it must close the resource
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 返回一个不可修改的集合,上层调用只能读取查询操作
		return mapperSql;
	}

	public void parse2(String configPath) throws DocumentException {
		SAXReader reader = new SAXReader();

		// 用这方法绝对能够拿得到资源
		InputStream is = CacheSqlInitMapperHandler.class.getClassLoader()
				.getResourceAsStream(configPath);

		Document doc = reader.read(is);

		Element rootEle = doc.getRootElement();

		// 得到 sql 任务执行器
		this.initSqlExexutor(rootEle.element("sqlExecutor"));
		
		// 初始化缓存容器的名称，因为是有默认值，则该元素的值可以为null
		this.initHighCache(rootEle.element(HIGH_CACHE_NAME));

//		初始化任务的延时加载
		this.initDelay(rootEle.element(DELAY));
		
		// 得到任务执行的周期
		try {
			this.initRefreshPeriod(rootEle.element(REFRESH_PERIOD));
		} catch (RefreshPeriodException e1) {
			e1.printStackTrace();
		}

		// 得到所有的mapper 元素
		initMapperSql(rootEle);

		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMapperSql(Element rootEle) {
		List<Element> mapperEles = rootEle.elements("mapper");
		mapperSql = new HashSet<String>(mapperEles.size());

		for (Element ele : mapperEles) {

			mapperSql.add(ele.getTextTrim());
		}

		log.info("the total quantity of the sql statement is:"
				+ mapperSql.size());
	}

	// 处理任务调度器的周期执行
	private void initRefreshPeriod(Element refreshPeriod)
			throws RefreshPeriodException {
		if (refreshPeriod == null) {// 为空的情况，如何处理、默认为每个5秒钟去同步数据库

			log.info("HighCache:the priod of the task is that of the default systerm :"
					+ DEFUALT_REFRESH_PERIOD);

			this.refreshPeriod = DEFUALT_REFRESH_PERIOD;
		} else {// 不为空的情况又该如何处理
			try {
				String refresf = refreshPeriod.getTextTrim();
				if(!StringUtils.isEmpty(refresf)){
					Integer refresh = Integer.parseInt(refresf);
	
					log.info("HighCache:the period of the task by the configuration file is :"
							+ refreshPeriod);
	
					this.refreshPeriod = refresh * 1000L;// 转化成毫秒
				}else{//这种情况也是处理给出了标签元素，但未给出奇值，则仍然使用默认的值
					
					log.info("HighCache:the priod of the task is that of the default systerm :"
							+ DEFUALT_REFRESH_PERIOD);
					this.refreshPeriod = DEFUALT_REFRESH_PERIOD;
				}
			} catch (NumberFormatException e) {
				throw new RefreshPeriodException(
						"the inputted refresh period value is invalid,please input the correct value of refresh period.");
			}
		}
	}

	// 处理数据需要存放的缓存容器名称，
	public abstract void initHighCache(Element highCacheName);

	//初始化延时
	private void initDelay(Element delay){
		
		
		if(delay == null ){
			log.info("the value of delay that the task will executor after some seconds is the system default that is:"+this.DEFAULT_DELAY/1000);
			this.delay = this.DEFAULT_DELAY;
		}else{
			String de = delay.getTextTrim();
			if(!StringUtils.isEmpty(de)){
				log.info("the value of delay that the task will executor after some seconds is:"+delay);
				this.delay = Long.valueOf(de.trim());
			}else{
				log.info("the value of delay that the task will executor after some seconds is the system default that is:"+this.DEFAULT_DELAY/1000);
				this.delay = this.DEFAULT_DELAY;
			}
		}
	}
	
	public void setMapperSql(Set<String> mapperSql) {
		this.mapperSql = mapperSql;
	}

	public abstract void initSqlExexutor(Element sqlExecutorEle);

	public Set<String> getMapperSql() {
		return mapperSql;
	}

	public Long getRefreshPeriod() {
		return refreshPeriod;
	}

	public void setRefreshPeriod(Long refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	public String getSqlInitDataCacheName() {
		return sqlInitDataCacheName;
	}

	public void setSqlInitDataCacheName(String sqlInitDataCacheName) {
		this.sqlInitDataCacheName = sqlInitDataCacheName;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}
}
