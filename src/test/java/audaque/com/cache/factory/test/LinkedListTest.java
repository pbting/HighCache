package audaque.com.cache.factory.test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LinkedListTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Map<String, String> linkedHashMap = Collections.synchronizedMap(new LinkedHashMap<String, String>(3, 0.75f, true));

		linkedHashMap.put("key-1", "val-1");
		linkedHashMap.put("key-2", "val-2");
		linkedHashMap.put("key-3", "val-3");

		System.out.println("first:" + linkedHashMap);

		linkedHashMap.put("key-4", "val-4");

		System.out.println("put:" + linkedHashMap);
		linkedHashMap.get("key-2");

		Set<String> keySets = linkedHashMap.keySet();

		String firstKey = keySets.iterator().next();
		linkedHashMap.remove(firstKey);

		System.out.println("get:" + linkedHashMap);
	}

}
