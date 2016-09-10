package audaque.com.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CLHLock2 implements Lock{
	public static class CLHNode {
		private volatile boolean isLocked = true;
	}

	private static final ThreadLocal<CLHNode> LOCAL = new ThreadLocal<CLHNode>();
	private static final AtomicReferenceFieldUpdater<CLHLock2, CLHNode> UPDATER = AtomicReferenceFieldUpdater.newUpdater(
			CLHLock2.class,
			CLHNode.class, "tail");

	public void lock() {
		CLHNode node = new CLHNode();
		LOCAL.set(node);
		CLHNode preNode = UPDATER.getAndSet(this, node);//getAndSet(this, node);
		if (preNode != null) {
			while (preNode.isLocked) {
			}
			preNode = null;
			LOCAL.set(node);
		}
	}

	public void unlock() {

		CLHNode node = LOCAL.get();
		if (!UPDATER.compareAndSet(this, node, null)) {
			node.isLocked = false;
		}
		node = null;
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
	public Condition newCondition() {
		return null;
	}
}
