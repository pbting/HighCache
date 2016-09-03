package audaque.com.pbting.cache.init.mapper;

import org.apache.ibatis.session.SqlSession;
import audaque.com.pbting.cache.base.info.HighCache;

/**
 * 定义一个sql 执行器，客户端端必须执行该方法，以便处理各种类型的预加载sql 语句
 * @author Administrator
 *
 */
public interface MybatisMapperSqlExecutor {

	public void execute(String sql,SqlSession session,HighCache highCache);

}
