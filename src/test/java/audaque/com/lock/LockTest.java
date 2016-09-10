package audaque.com.lock;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.util.concurrent.DefaultThreadFactory;

public class LockTest {

//	public static final TicketLock Lock = new TicketLock();
	
//	public static final CLHLock Lock = new CLHLock();
	
//	public static final MCSLock Lock = new MCSLock();
	
	public static final ReentrantLock2 Lock = new ReentrantLock2(true);
	
	public static void main(String[] args) {
//		TicketLock ticketLock = new TicketLock();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(8,64,60,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(),new DefaultThreadFactory(""));
		final AtomicInteger count = new AtomicInteger();
		for(int i=0;i<100;i++){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Lock.lock();
					System.out.println("c:"+(TicketLock.count++)+"thread wait:"+Lock.getQueuedThreads());
					Lock.unlock();
				}
			});
		}
		executor.shutdown();
	}
}

class ReentrantLock2 extends ReentrantLock{
	public ReentrantLock2() {
	}
	public ReentrantLock2(boolean fair){
		super(fair);
	}
	@Override
	protected Collection<Thread> getQueuedThreads() {
		return super.getQueuedThreads();
	}
}