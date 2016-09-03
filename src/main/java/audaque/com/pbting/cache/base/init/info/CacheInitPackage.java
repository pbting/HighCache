package audaque.com.pbting.cache.base.init.info;

import java.io.Serializable;
import java.util.Set;
/**
 * 这个类对于缓存初始化中的package 属性
 * @author Administrator
 *
 */
public class CacheInitPackage implements Serializable{

	private String name ;
	
	//这个包下有可能youduoge select statement,so it will

	private Set<CacheInitSelect> selectSets = null ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<CacheInitSelect> getSelectSets() {
		return selectSets;
	}

	public void setSelectSets(Set<CacheInitSelect> selectSets) {
		this.selectSets = selectSets;
	}

	public CacheInitPackage(String name, Set<CacheInitSelect> selectSets) {
		super();
		this.name = name;
		this.selectSets = selectSets;
	}

	public CacheInitPackage() {
		super();
	}
	
}
