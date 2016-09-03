package audaque.com.netty.future;

import java.util.HashMap;
import java.util.WeakHashMap;

/*
 * 在这里真正的产生数据，这个过程也许是非常漫长的
 */
public class RealResult implements ResultInfo {

	private Object result;
	
//	private static HashMap<String, String> hashMap = null ;
	private static WeakHashMap<String, String> hashMap = null ;
	static{
		System.out.println("初始化结果----");
		hashMap = new WeakHashMap<String, String>();
//		hashMap = new HashMap<String, String>();
		for(int i =0 ; i < 1000 ;i++)
			hashMap.put(String.valueOf(i+1), "pbting_"+String.valueOf(i+1));
	}
	
	public RealResult(Object key){
		
		try {
			Thread.sleep(5000);
			this.result = this.hashMap.get(String.valueOf(key));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Object getResult() {
		return this.result;
	}

}
