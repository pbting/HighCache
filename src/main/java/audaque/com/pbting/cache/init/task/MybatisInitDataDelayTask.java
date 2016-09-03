package audaque.com.pbting.cache.init.task;

import java.util.Iterator;
import java.util.Set;
import org.apache.ibatis.session.SqlSession;
import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.init.mapper.MybatisMapperSqlExecutor;
import audaque.com.pbting.cache.init.mapper.MybatisSqlInitMapperHandler;
import audaque.com.pbting.cache.web.context.listener.SystemInitSqlSessionListener;

public class MybatisInitDataDelayTask extends LoadInitDataDelayTask {
	private SqlSession session = null;

	private MybatisMapperSqlExecutor sqlExecutor;

	public MybatisInitDataDelayTask(SqlSession session, Set<String> sqlSet, MybatisMapperSqlExecutor sqlExecutor,
			HighCache highCache) {
		super(sqlSet, highCache);
		this.session = session;
		this.sqlExecutor = sqlExecutor;
	}

	public MybatisInitDataDelayTask(SqlSession session,MybatisSqlInitMapperHandler sqlInitMapperHandler,
			HighCache highCache){
		super(sqlInitMapperHandler.getMapperSql(),highCache);
		this.session = session;
		this.sqlExecutor = sqlInitMapperHandler.getMybatisMapperSqlExecutor();
	}
	
	// 这需要处理预加载数据的结果
	public void executeTask(Set<String> sqlSet, HighCache highCache) {
		System.out.println("-----------<delay to load the hot data or cold>"+ sqlSet.size() + "-------------");

		for (Iterator<String> sqlIt = sqlSet.iterator(); sqlIt.hasNext();) {

			// it will execute the sql by programe implements the abstraction
			// method
			this.sqlExecutor.execute(sqlIt.next(), session, highCache);

			session.commit();
		}

		// 关闭session
		SystemInitSqlSessionListener.close(session);
	}
}
