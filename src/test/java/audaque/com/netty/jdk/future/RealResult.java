package audaque.com.netty.jdk.future;

import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class RealResult implements Callable<Object> {

	private Object key = null ;
	
	private static WeakHashMap<String, String> hashMap = null ;
	
	static{
		System.out.println("初始化结果----");
		hashMap = new WeakHashMap<String, String>();
//		hashMap = new HashMap<String, String>();
		for(int i =0 ; i < 1000 ;i++)
			hashMap.put(String.valueOf(i+1), "pbting_"+String.valueOf(i+1));
	}

	public RealResult() {
	}
	
	public RealResult(Object key) {
		this.key = key;
	}
	
	public void methodOne(){
		System.out.println("methodOne---is runing....");
	}
	
	public void methodTwo(){
		System.out.println("methodTwo---is runing....");
	}
	
	public Object call() throws Exception {
		//返回响应的结果
		//return this.hashMap.get(String.valueOf(key));
		//这里根据传入的指定方法名称运行
		
		try {
			Class clazz = RealResult.class.forName("audaque.com.netty.jdk.future.RealResult");
			
			Method method = clazz.getMethod(String.valueOf(this.key), null);
			
			method.invoke(RealResult.class.forName("audaque.com.netty.jdk.future.RealResult").newInstance(),null);
			return "success" ;
		} catch (Exception e) {
			e.printStackTrace();
			return "error" ;
		}
	}
	
	public static void main(String[] args) throws Exception {
		FutureTask<Object> futureTask = new FutureTask<Object>(new RealResult("methodOne"));
		
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		
		executorService.submit(futureTask);
		
		System.out.println(futureTask.get().toString());
		
		System.exit(0);
	}
}
