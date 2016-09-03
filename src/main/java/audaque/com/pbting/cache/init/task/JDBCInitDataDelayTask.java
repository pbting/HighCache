package audaque.com.pbting.cache.init.task;

import java.util.Set;
import org.apache.commons.dbutils.QueryRunner;
import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.database.util.HighCacheDBPool;
import audaque.com.pbting.cache.init.mapper.JDBCConMapperSqlExecutor;
import audaque.com.pbting.cache.init.mapper.JDBCSqlInitMapperHandler;

/**
 * 在上下文监听器的情况下，开启此任务
 * @author pbting
 *
 */
public class JDBCInitDataDelayTask extends LoadInitDataDelayTask {

	private HighCacheDBPool cacheDBPool = null ;
	
	//客户端需要实现该接口的一个实现，然后可供任务调度器所调度并执行
	private JDBCConMapperSqlExecutor conMapperSqlExecutor = null ;
	
	public JDBCInitDataDelayTask(Set<String> sqlSet, 
			HighCache highCache,HighCacheDBPool cacheDBPool,JDBCConMapperSqlExecutor conMapperSqlExecutor) {
		super(sqlSet, highCache);
		this.cacheDBPool = cacheDBPool;
		this.conMapperSqlExecutor = conMapperSqlExecutor;
	}

	public JDBCInitDataDelayTask(JDBCSqlInitMapperHandler sqlInitMapperHandler,
			HighCache highCache,HighCacheDBPool cacheDBPool){
		super(sqlInitMapperHandler.getMapperSql(),highCache);
		try {
			this.conMapperSqlExecutor = sqlInitMapperHandler.getJdbcConMapperSqlExecutor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.cacheDBPool = cacheDBPool;
	}
	
	//在这里提供客户端的接口 来实现任务的处理
	@Override
	public void executeTask(Set<String> sqlSet,HighCache highCache) {
		QueryRunner queryRunner = new QueryRunner(cacheDBPool.getPooledDataSource());
		for(String sql :sqlSet){
			
			this.conMapperSqlExecutor.execute(sql,queryRunner, highCache);
		}
	}
}
