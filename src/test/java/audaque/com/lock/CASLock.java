package audaque.com.lock;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CAS 就是传说中的自旋锁
 * @author pbting
 *
 */
public class CASLock {

	private static AtomicReference<Person> personRefrence = new AtomicReference<Person>();
	private static AtomicInteger atomicInteger = new AtomicInteger(1);
	public static void main(String[] args) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 64, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		for(int i=0;i<100;i++){
//		personRefrence.compareAndSet(expect, update)
			executor.execute(new Runnable() {
				@Override
				public void run() {
					int currentVal = atomicInteger.getAndIncrement();
					if(currentVal==1){
						int value = currentVal;
						personRefrence.compareAndSet(null,new Person("name_"+value, value));
						System.out.println("the first ,name is:"+("name_"+value)+";-->age:"+value);
					}else{
						int oldVal = currentVal-1;
						personRefrence.compareAndSet(new Person("name_"+oldVal, oldVal), new Person("name_"+currentVal, currentVal));
						System.out.println("is compare ,name is:"+("name_"+oldVal)+";-->age:"+oldVal);
					}
				}
			});
		}
		System.out.println("name:"+personRefrence.get().getName()+";age:"+personRefrence.get().getAge());
		executor.shutdown();
	}
}
