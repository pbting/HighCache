package audaque.com.pbting.cache.init.task;

import java.util.Set;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import audaque.com.pbting.cache.base.info.HighCache;
//自定义一个延时处理的子任务，这里缓存系统默认延时5s 钟去加载冷数据
public abstract class LoadInitDataDelayTask extends TimerTask  {

	private Log log = LogFactory.getLog(LoadInitDataDelayTask.class);
	
	protected Set<String> sqlSet = null ;
	
	protected HighCache highCache = null ;
	
	public LoadInitDataDelayTask(Set<String> sqlSet,HighCache highCache) {
		super();
		this.sqlSet = sqlSet;
		this.highCache = highCache;
	}


	@Override
	public void run() {
		
		this.executeTask(this.sqlSet,this.highCache);
	}
	
	//这需要处理预加载数据的结果
	public abstract void executeTask(Set<String> sqlSet, HighCache highCache);
}
