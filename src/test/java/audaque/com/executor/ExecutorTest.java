package audaque.com.executor;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorTest {

	public static void main(String[] args) {
		ExecutorService exe = 
				new ThreadPoolExecutor(10, 20, 0L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
		
		Random random = new Random();
		 
		for(int i=0;i<=1000;i++){
			exe.execute(new PriorityRunable(random.nextInt(1000)));
		}
	}
}
