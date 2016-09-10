package audaque.com.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 适合在SMP 系统架构下的公平自旋锁
 * 
 * 互斥锁：如果资源已经被占用，资源申请者只能进入睡眠状态，
 * 自旋锁：不会引起调用者的睡眠，如果自旋锁已经被别的执行线程保持，调用者就一直在【循环】在那里看持有自旋锁的线程是否已经释放了锁。自旋 即循环查看信号量，是否获得执行的资格。
 * 
 * CLHLock 和MCSLock 则是两种类型相似的公平锁，采用链表的形式进行排序，
 * @author pbting
 */
public class CLHLock implements Lock{

	private AtomicReference<QNode> tail ;
	private ThreadLocal<QNode> myPre ;
	private ThreadLocal<QNode> myNode ;
	
	private static final class QNode{
		public volatile boolean locked = false ;
	}
	public CLHLock() {
		tail = new AtomicReference<QNode>(new QNode());
		myNode = new ThreadLocal<QNode>(){
			@Override
			protected QNode initialValue() {
				return new QNode();
			}
		};
		myPre = new ThreadLocal<QNode>();
	}
	/**
	 * 当一个线程需要获取锁时，会创建一个新的QNode，将其中的locked设置为true表示需要获取锁，
	 * 然后线程对tail域调用getAndSet方法，使自己成为队列的尾部，同时获取一个指向其前趋的引用
	 * myPred,然后该线程就在前趋结点的locked字段上旋转，直到前趋结点释放锁。
	 */
	@Override
	public void lock() {
		//1、
		QNode node = myNode.get();
		node.locked = true ;
		//2、
		QNode pre = tail.getAndSet(node);
		
		//设置好指向尾部前驱的引用
		myPre.set(pre);
		while(pre.locked){}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		
	}

	@Override
	public boolean tryLock() {
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		QNode node = myNode.get();
		node.locked = false ;
		myNode.set(myPre.get());
	}

	@Override
	public Condition newCondition() {
		return null;
	}
	
}
