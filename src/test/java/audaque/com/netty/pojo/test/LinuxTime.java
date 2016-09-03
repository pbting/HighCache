package audaque.com.netty.pojo.test;

import java.io.Serializable;

public class LinuxTime implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long time ;
	
	public LinuxTime() {
		this(System.currentTimeMillis() / 1000L + 2208988800L);
	}

	public LinuxTime(long time) {
		super();
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "LinuxTime [time=" + time + "]";
	}
	
}
