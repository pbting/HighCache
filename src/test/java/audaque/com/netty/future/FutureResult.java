package audaque.com.netty.future;

public class FutureResult implements ResultInfo {

	protected RealResult realResult = null ;
	
	private boolean isReady = false ;
	
	public synchronized void setRealResult(RealResult realResult){
		if(isReady)
			return ;
		this.realResult = realResult;
		isReady = true ;
		notifyAll();
	}
	
	public synchronized Object getResult() {
		//如果程序中立马得到该结果
		try {
			while(!isReady){
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return this.realResult.getResult();
	}

}
