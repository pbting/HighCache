package audaque.com.pbting.cache.init.task;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 该任务的目的就是减少每次重构后那些虽然组还存在，但是组内已经没有元素了所占有的内存
 * @author pbting
 *
 */
public class RemoveInvalidRcES extends TimerTask {

	private ConcurrentHashMap<Float, Map> Rc_ES = null ;
	public RemoveInvalidRcES(ConcurrentHashMap<Float, Map> Rc_ES){
		
		this.Rc_ES = Rc_ES;
	}
	
	@Override
	public void run() {
		if(!this.Rc_ES.keySet().isEmpty()){
			Iterator<Float> iter = Rc_ES.keySet().iterator();
			Float key = null ;
			while(iter.hasNext()){
				key = iter.next();
				if(this.Rc_ES.get(key).isEmpty()){//如果已经为空
					iter.remove();//移除当前项
				}
			}
		}
	}
}
