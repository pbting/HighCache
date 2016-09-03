package audaque.com.pbting.cache.init.mapper;

import org.apache.commons.dbutils.QueryRunner;

import audaque.com.pbting.cache.base.info.HighCache;
/**
 * 定义一个执行 jdbc 的任务执行器
 * @author pbting
 *
 */
public interface JDBCConMapperSqlExecutor {

	public void execute(String sql,QueryRunner queryRunner,HighCache highCache);
}
