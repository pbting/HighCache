package audaque.com.lock;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 锁主要解决的是访问顺序的问题，主要的问题是在多核cpu上
 * @author pbting
 *
 */
public class TicketLock {
	
	private AtomicInteger serviceNum = new AtomicInteger();
	private AtomicInteger tickNum = new AtomicInteger();
	private static final ThreadLocal<Integer> LOCAL = new ThreadLocal<Integer>();
	
	public void lock(){
		int ticktNum = tickNum.getAndIncrement();//获得上一次的票号
		LOCAL.set(ticktNum);
		while(ticktNum != serviceNum.get()){}
	}
	
	public void unlock(){
		int localTicket = LOCAL.get();
		serviceNum.compareAndSet(localTicket, localTicket+1);
	}
	public static int count = 0 ;
}
