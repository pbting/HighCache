package audaque.com.pbting.cache.database.util;

import java.util.List;
/**
 * 
 * @author Administrator
 */
public interface HighCacheSqlHelpInter {

	/**
	 */
	public boolean insert(String sql,String[] parameters);
	
	/**
	 */
	public boolean delete(String sql,String[] paremeters);
	
	/**
	 */
	public boolean modify(String sql,String[] paras);
	
	
	/**
	 */
	public Object[] querySingleRow(String sql,String[] params);
	
	/**
	 */
	public List<Object[]> queryMultiRow(String sql,String[] params);
	
	/**
	 */
	public Integer getTotalRowByTabName(String table);
}
