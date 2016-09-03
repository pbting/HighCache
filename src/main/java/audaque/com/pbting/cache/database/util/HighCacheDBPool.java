package audaque.com.pbting.cache.database.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;


public class HighCacheDBPool {

	private final static String driver = "driver";
	private final static String url = "url";
	private final static String username = "username";
	private final static String password = "password";
	
	private  DataSource pooledDataSource = null ;
	//连接数据库所需要的相关参数
	private DataConectionParam connConectionParam;
	
	public HighCacheDBPool(DataConectionParam dataConectionParam){
		
		this.connConectionParam = dataConectionParam;
		
		initPool();
	}
	
	public HighCacheDBPool(String dbpollcfg) throws IOException{
		
		this.connConectionParam =  new DataConectionParam(dbpollcfg);
		
		initPool();
	}

	private void initPool() {
		this.loadDriver();
		this.initPooledDB();
	}
	
	public DataSource getPooledDataSource() {
		return pooledDataSource;
	}

	public void setPooledDataSource(DataSource pooledDataSource) {
		this.pooledDataSource = pooledDataSource;
	}

	private void loadDriver(){
		try {
			Class.forName(connConectionParam.getDriver(driver));
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void initPooledDB(){
		
		try {
			DataSource unpooled = 
					DataSources.unpooledDataSource(connConectionParam.getUrl(url),
							connConectionParam.getUserName(username),
			
							connConectionParam.getPassword(password));
			pooledDataSource = 
					DataSources.pooledDataSource(unpooled,this.connConectionParam.getC3p0_pool_conf());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized Connection getConnection() throws SQLException{
		
		return pooledDataSource.getConnection();
	}

	public synchronized void closeConnection(Connection con){
		try {
			if(con != null && !con.isClosed()){
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void closeConnection(Connection connection, Statement statement, ResultSet rs){
		/**
		 * 
		 */
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
				rs = null ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		 * 
		 */
		try {
			if (statement != null && !statement.isClosed()) {
				statement.close();
				statement = null ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		 * 
		 */
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				connection = null ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
