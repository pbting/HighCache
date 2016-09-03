package audaque.com.executor;

public class PriorityRunable implements Runnable,Comparable<PriorityRunable>{
	/** 这个属性是表明该线程的一个优先级程度*/
	protected Integer priority;
	
	public PriorityRunable() {
	}

	public PriorityRunable(Integer priority){
		this.priority = priority ;
	}
	
	public int compareTo(PriorityRunable o) {
		return this.priority.compareTo(o.priority);
	}

	public void run() {
		try {
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
