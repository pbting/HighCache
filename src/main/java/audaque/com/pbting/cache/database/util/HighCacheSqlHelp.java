package audaque.com.pbting.cache.database.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HighCacheSqlHelp implements HighCacheSqlHelpInter {

	private HighCacheDBPool highCacheDBPool = null;

	public HighCacheSqlHelp(HighCacheDBPool highCacheDBPool) {

		this.highCacheDBPool = this.highCacheDBPool;
	}

	public boolean insert(String sql, String[] parameters) {
		if (checkParameter(sql, parameters))
			return this.dmlUniforExcute(sql, parameters);

		return false;
	}

	private boolean checkParameter(String sql, String[] parameters) {
		if (sql == null || sql.trim().length() <= 0)
			return false;

		if (parameters == null || parameters.length <= 0)
			return false;

		return true;
	}

	//
	public boolean delete(String sql, String[] paremeters) {

		if (checkParameter(sql, paremeters))
			return this.dmlUniforExcute(sql, paremeters);

		return false;
	}

	//
	public boolean modify(String sql, String[] paras) {

		if (checkParameter(sql, paras))
			return this.dmlUniforExcute(sql, paras);

		return false;
	}

	/**
	 * @param sql
	 * @param parameters
	 * @return
	 */
	private boolean dmlUniforExcute(String sql, String[] parameters) {

		Connection con = null;
		PreparedStatement pre = null;
		ResultSet rs = null;

		int flag = 0;

		try {
			con = this.highCacheDBPool.getConnection();
			pre = con.prepareStatement(sql);

			if (parameters == null) {
				pre.executeUpdate();
			} else if (sql.contains("?") && parameters.length >= 0) {

				for (int i = 1; i <= parameters.length; i++)
					pre.setString(i, parameters[i - 1]);

				flag = pre.executeUpdate();

			} else {
				throw new RuntimeException("error");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.close(con, pre, rs);
		}

		return (flag > 1 ? true : false);
	}

	/**
	
	 */
	public Object[] querySingleRow(String sql, String[] params) {

		return null;
	}

	public List<Object[]> queryMultiRow(String sql, String[] params) {

		Connection con = null;

		PreparedStatement pre = null;

		ResultSet rs = null;

		ArrayList<Object[]> list = null;

		try {
			con = this.highCacheDBPool.getConnection();
			pre = con.prepareStatement(sql);

			if (params != null && !params.equals("")) {
				for (int i = 0; i < params.length; i++) {
					pre.setString(i + 1, params[i]);
				}
			}
			rs = pre.executeQuery();
			list = new ArrayList<Object[]>();
			//
			ResultSetMetaData rsmd = rs.getMetaData();
			int cloum = rsmd.getColumnCount();

			while (rs.next()) {
				Object[] obj = new Object[cloum];
				for (int i = 1; i <= cloum; i++) {
					/*
					 * 案例额的顺序存储
					 */
					obj[i - 1] = rs.getObject(i);
				}
				list.add(obj);
			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

			this.close(con, pre, rs);
		}

		return list;
	}

	/**
	 *
	 */
	public Integer getTotalRowByTabName(String table) {

		Connection con = null;

		PreparedStatement pre = null;

		ResultSet rs = null;

		Integer count = null;

		try {
			con = this.highCacheDBPool.getConnection();
			pre = con.prepareStatement("select count(*) from " + table);

			rs = pre.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			this.close(con, pre, rs);
		}

		return count;
	}

	/**
	 * 关闭的是一个代理数据库连接
	 */
	private void close(Connection connection) {

		this.highCacheDBPool.closeConnection(connection);
	}

	private void close(Connection connection, Statement statement, ResultSet rs) {
		// TODO Auto-generated method stub
		this.highCacheDBPool.closeConnection(connection, statement, rs);
	}
}
