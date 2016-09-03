package audaque.com.pbting.cache.base.init.info;

import java.io.Serializable;
import java.util.Set;

public class CacheInit implements Serializable {

	// 这里面有个命名空间，便于查找 对于对分配置文件
	private String nameSpace;

	private Set<CacheInitPackage> packageSet = null;

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public Set<CacheInitPackage> getPackageSet() {
		return packageSet;
	}

	public void setPackageSet(Set<CacheInitPackage> packageSet) {
		this.packageSet = packageSet;
	}

	public CacheInit(String nameSpace, Set<CacheInitPackage> packageSet) {
		super();
		this.nameSpace = nameSpace;
		this.packageSet = packageSet;
	}

	public CacheInit() {
		super();
	}
}
