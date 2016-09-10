package audaque.com.lock;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/**
 * 适合在NUMA 系统架构下的高性能自旋锁 
 * @author pbting
 */
public class MCSLock {

	public static class MCSNode {
		volatile MCSNode next;
		volatile boolean isLocked = true;
	}

	private static final ThreadLocal<MCSNode> NODE = new ThreadLocal<MCSNode>();
	@SuppressWarnings("unused")
	private volatile MCSNode queue;
	private static final AtomicReferenceFieldUpdater<MCSLock, MCSNode> UPDATER = 
			AtomicReferenceFieldUpdater.newUpdater(MCSLock.class,MCSNode.class, "queue");
	public void lock() {
		MCSNode currentNode = new MCSNode();
		NODE.set(currentNode);
		MCSNode preNode = UPDATER.getAndSet(this, currentNode);
		if (preNode != null) {
			preNode.next = currentNode;
			while (currentNode.isLocked) {
			}
		}
	}

	public void unlock() {
		MCSNode currentNode = NODE.get();
		if (currentNode.next == null) {
			if (UPDATER.compareAndSet(this, currentNode, null)) {
			} else {
				while (currentNode.next == null) {}
			}
		} else {
			currentNode.next.isLocked = false;
			currentNode.next = null;
		}
	}
}
