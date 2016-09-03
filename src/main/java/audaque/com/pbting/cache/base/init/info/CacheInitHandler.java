package audaque.com.pbting.cache.base.init.info;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * hot or cold data initializtion handler
 * 
 * @author Administrator
 */
public class CacheInitHandler {

	// it will store some package that configue in the init xml file
	private Map<String, CacheInit> elementInits = new HashMap<String, CacheInit>();

	// will based on the nchucache-init.xml to handle some login service

	public CacheInitHandler(String nchuCacheInitFile) throws IOException, DocumentException {
		// TODO Auto-generated constructor stub
		CacheInit cacheInit = this.parse(nchuCacheInitFile);

		elementInits.put(cacheInit.getNameSpace(), cacheInit);
	}

	public Map<String, CacheInit> getElementInits() {
		return elementInits;
	}

	public void setElementInits(Map<String, CacheInit> elementInits) {
		this.elementInits = elementInits;
	}

	private CacheInit parse(String fileName) throws IOException, DocumentException {

		Document doc = new SAXReader().read(CacheInitHandler.class.getClassLoader().getResourceAsStream(fileName));

		Element element = doc.getRootElement();

		String nameSpace = element.attributeValue("namespace");

		@SuppressWarnings("unchecked")
		List<Element> packageEles = element.elements("package");

		Set<CacheInitPackage> packageSet = new HashSet<CacheInitPackage>(packageEles.size());

		// it will handle the package elements
		handlePackages(packageEles, packageSet);

		return new CacheInit(nameSpace, packageSet);
	}

	private void handlePackages(List<Element> packageEles, Set<CacheInitPackage> packageSet) {
		CacheInitPackage cacheInitPackage = null;
		for (Iterator<Element> packageIter = packageEles.iterator(); packageIter.hasNext();) {
			Element packageElem = packageIter.next();
			// get the name of package
			String packageName = packageElem.attributeValue("name");

			List<Element> selectEles = packageElem.elements("select");

			Set<CacheInitSelect> selectSet = new HashSet<CacheInitSelect>(selectEles.size());
			// 遍历得到每一个select 元素
			for (Iterator<Element> selectiter = selectEles.iterator(); selectiter.hasNext();) {
				handleSelectElements(selectSet, selectiter);
			}
			// 出书package
			cacheInitPackage = new CacheInitPackage(packageName, selectSet);
			packageSet.add(cacheInitPackage);
		}
	}

	private void handleSelectElements(Set<CacheInitSelect> selectSet, Iterator<Element> selectiter) {
		CacheInitSelect initSelect;
		Element ele = selectiter.next();

		// 得到他的id and name of class

		initSelect = new CacheInitSelect(ele.attributeValue("id"), ele.attributeValue("class"));

		/**
		 * dom4j 中 获得子元素的方法 得到select 子元素下面的子节点
		 */
		List<Element> selectChild = ele.elements();

		CacheInitSelect.SqlResult sqlResult = new CacheInitSelect.SqlResult();

		Set<CacheInitSelect.Result> results = new HashSet<CacheInitSelect.Result>();;

		// 处理select 元素下面的子元素
		handleSelectChildren(selectChild, sqlResult, results);

		// 2、给sql result 的results 属性赋值
		sqlResult.results = results;

		// 3、给select 元素下面设置相关的子元素
		initSelect.setSqlResult(sqlResult);

		selectSet.add(initSelect);
	}

	private void handleSelectChildren(List<Element> selectChild, CacheInitSelect.SqlResult sqlResult, Set<CacheInitSelect.Result> results) {
		for (Iterator<Element> iters = selectChild.iterator(); iters.hasNext();) {

			Element tempEl = iters.next();

			System.out.println("tag name:" + tempEl.getName());
			if ("sql".equals(tempEl.getName())) {

				// 1、给sql result 的sql属性赋值
				sqlResult.sql = tempEl.attributeValue("value");

				System.out.println("the sql statement is:" + sqlResult.sql);
				continue;
			}

			if ("result".equals(tempEl.getName())) {

				results.add(new CacheInitSelect.Result(tempEl.attributeValue("property"), tempEl.attributeValue("colum")));

				System.out.println("the property of result is:" + tempEl.attributeValue("property") + "-----the colum of result is:" + tempEl.attributeValue("colum"));
				continue;
			}
		}
	}
}
