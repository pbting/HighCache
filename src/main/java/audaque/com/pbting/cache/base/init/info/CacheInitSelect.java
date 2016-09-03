package audaque.com.pbting.cache.base.init.info;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * point to select elements in the init xml file
 * @author Administrator
 *
 */
public class CacheInitSelect {

	private String id;
	private String className ;
	
	private SqlResult sqlResult = null ;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	
	public CacheInitSelect(String id, String className) {
		super();
		this.id = id;
		this.className = className;
	}
	
	public SqlResult getSqlResult() {
		return sqlResult;
	}

	public void setSqlResult(SqlResult sqlResult) {
		this.sqlResult = sqlResult;
	}

	public CacheInitSelect() {
		super();
	}



	public static class SqlResult implements Serializable{
		
		//the sql statement
		public String sql ;
		
		public Set<Result> results = null;

		public Set<Result> getResults() {
			return (Set<Result>) Collections.unmodifiableCollection(results);
		}
	}
	
	//the result elements declare related class
	public static class Result implements Serializable{
		public String property;
		
		public String colum ;

		
		public Result(String property, String colum) {
			super();
			this.property = property;
			this.colum = colum;
		}

		public Result() {
			super();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true ;
			
			if(!(obj instanceof Result))
				return false ;
				
			Result result = (Result)obj;
			
			if(!this.colum.equals(result.colum)|| !this.property.equals(result.property))
				return false ;
			
			return true;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "property:["+this.property+"]<--->colum:["+this.colum+"]";
		}
	}
}
