package audaque.com.netty.future;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import audaque.com.executor.PriorityRunable;

public class Main {

	public static void main(String[] args) {
		System.out.println("---");
		ExecutorService exe = 
				new ThreadPoolExecutor(10, 20, 0L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
		
		Random random = new Random();
		 
		for(int i=0;i<=1000;i++){
			exe.execute(new PriorityRunable(random.nextInt(1000)));
		}
	}

	private static void future() {
		Client client = new Client();
		
		ResultInfo resultInfo = client.getResult("4");
		try {
			//这里模拟处理其他的业务
			Thread.sleep(000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(String.valueOf(resultInfo.getResult()));
	}
}
