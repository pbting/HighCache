package audaque.com.cache.factory.test;

import java.util.TreeMap;

import org.junit.Test;

public class TreeMapTest {

	@Test
	public void treeMapTest() {

		TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>();

		treeMap.put(1, "val-1");
		treeMap.put(1, "val-2");
		treeMap.put(1, "val-3");
		treeMap.put(1, "val-4");

		System.out.println(treeMap.size());
	}
}
