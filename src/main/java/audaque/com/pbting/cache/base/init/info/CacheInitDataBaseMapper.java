package audaque.com.pbting.cache.base.init.info;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 这个类将处理与数据库之间的映射关系
 * @author Administrator
 *
 */
public class CacheInitDataBaseMapper {

	
	private SqlSessionFactory sessionFactory = null ;

	Map<String,CacheInit> elementInits = null ;
	
	public SqlSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SqlSessionFactory sessionFactory) {
		System.out.println("spring 依赖注入session factory............");
		
		this.sessionFactory = sessionFactory;
	}

	public CacheInitDataBaseMapper() {
		super();
	}
	
	public void run(SqlSessionFactory sessionFactory,Map<String,CacheInit> elementInits){
		
		this.sessionFactory = sessionFactory;
		
		this.elementInits = elementInits;
		
		System.out.println("the count of init numbers is:"+elementInits.size());
	}
}
