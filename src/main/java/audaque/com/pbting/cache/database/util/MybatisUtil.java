package audaque.com.pbting.cache.database.util;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 如何来设计这个 工具类呢？获得一个工厂在整个项目的运行期间只要一即可，因此该类应该设计 成单利
 * 
 * @author pbting
 * 
 */
public class MybatisUtil {

	private static SqlSessionFactory sqlSessionFactory = null;

	private MybatisUtil() {}

	public static SqlSessionFactory getSqlSessionFactory(String resoure) {
		if (sqlSessionFactory == null) {
			synchronized (MybatisUtil.class) {
				if (sqlSessionFactory == null) {

					sqlSessionFactory = new SqlSessionFactoryBuilder()
							.build(MybatisUtil.class.getClassLoader()
									.getResourceAsStream(resoure));
				}
			}
		}

		return sqlSessionFactory;
	}

	public static synchronized SqlSession getSqlSession(String resource) {

		return getSqlSessionFactory(resource).openSession();
	}

	public static synchronized SqlSession getSqlSession(String resource,
			boolean autoCommit) {

		return getSqlSessionFactory(resource).openSession(autoCommit);
	}

	/**
	 * 
	 * @param sqlSession
	 *            这个参数最好是由该工具类生成的
	 */
	public static synchronized void closeSqlSession(SqlSession sqlSession) {

		if (sqlSession != null)
			try {
				sqlSession.close();
			} catch (Exception e) {
				e.printStackTrace();
				sqlSession.rollback();
			}
	}

	public static synchronized void closeSqlSession(SqlSession sqlSession,
			boolean isCommit) {
		if (sqlSession != null) {
			try {
				if (isCommit)// 如果参数中设置了提交，则在关闭之前进行数据库事务的提交
					sqlSession.commit();

				sqlSession.close();
			} catch (Exception e) {
				e.printStackTrace();
				sqlSession.rollback();
			}
		}
	}
}
