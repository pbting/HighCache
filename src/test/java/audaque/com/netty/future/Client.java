package audaque.com.netty.future;

public class Client {

	public ResultInfo getResult(final String key){
		final FutureResult futureResult = new FutureResult();
		new Thread(new Runnable() {
			
			public void run() {
				//着这个构造函数初始想要的结果，完毕后，设置到future中
				RealResult realResult = new RealResult(key);
				futureResult.setRealResult(realResult);
			}
		}).start();
		
		//这里事先返回虚的数据
		return futureResult;
	}
}
