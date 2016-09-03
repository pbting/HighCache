package audaque.com.pbting.cache.house;

import java.util.TimerTask;

public class FlushDataMetaIndexTask extends TimerTask{

	@Override
	public void run() {
		DataMgr.flushMetaIndex();
	}
}
