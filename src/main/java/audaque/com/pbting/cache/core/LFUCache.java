package audaque.com.pbting.cache.core;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 最少使用缓存置换算法的实现latest-seldom use cache
 * @author pbting
 *
 */
public class LFUCache extends AbstractMapCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LFUCache(){
		super();
	}
	
	public LFUCache(int capacity){
		
		super(capacity);
		this.maxEntries = capacity;
	}
	
	private final static Log log = LogFactory.getLog(LFUCache.class);
	
	//定义用来存放访问次数Set集合
	private Set<Integer> V_S = new ConcurrentSkipListSet<Integer>(new Comparator<Integer>() {
		public int compare(Integer o1, Integer o2) {
			
			return o1.compareTo(o2);
		};
	});
	
	/**
	 * key和访问次数据映射的hashmap
	 */
	private ConcurrentHashMap<Object, Integer> K_V =
								new ConcurrentHashMap<Object, Integer>();
	
	/**
	 * 访问映射字段：次数和这一类key的映射排序分类处理
	 */
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue> V_KS =
								new ConcurrentHashMap<Integer, ConcurrentLinkedQueue>();
	
	@Override
	public void itemPut(Object key) {
		
		if(K_V.containsKey(key)){//已经包含该key，则该怎么办
			/**
			 * 先把这个key取出来，然后增加次数,放到适当的等级中
			 *这个时候就成了一个获取时的操作了，改变其次序 
			 */
			log.info("[HighCache]-LFUConcurrentCache.itemPut and containsKey:"+key);
			this.itemRetrieved(key);
		}else{//不包含该怎么办
			LFUKey lfuKey = new LFUKey(key, 1);
			
			//得到一次级别上的所有map对象，放到第一个级别上
			ConcurrentLinkedQueue KS =  this.V_KS.get(lfuKey.count);
			
			if(KS == null){
				//表示系统的第一次
				KS = new ConcurrentLinkedQueue();
			}
			
			KS.add(lfuKey);
			if(!this.V_S.contains(lfuKey.count))
				this.V_S.add(lfuKey.count);
			//就算有一次也要记录下来
			this.K_V.put(key, lfuKey.count);
			this.V_KS.put(lfuKey.count, KS);
			log.debug("[HighCache]-LFUConcurrentCache.itemPut and first:"+key);
			//一次处理处理完成
		}
	}

	/**
	 * 当从缓存容器中get操作，促发该动作，目的就是改变key的一个排序
	 */
	@Override
	public void itemRetrieved(Object key) {
		// TODO Auto-generated method stub
		//1、获取当前key的一个访问次数，
		if(!K_V.containsKey(key))
			return ;
		
		Integer V = K_V.get(key);
		//获取这个访问次次数的一批key
		ConcurrentLinkedQueue KS =  this.V_KS.get(V);
		
		LFUKey lFUKey = (LFUKey) KS.poll();

		log.debug(key+"-->itemRetrieved:"+V+"--->"+lFUKey);		
		//对这访问次数加一
		lFUKey.count++;
		log.debug("[HighCache]-LFUConcurrentCache.itemRetrieved and the access count is"+lFUKey.count);
		//获得下一个等级
		ConcurrentLinkedQueue KS_NEXT =  this.V_KS.get(lFUKey.count);
		
		if(KS_NEXT == null){
			KS_NEXT = new ConcurrentLinkedQueue();
		}
		//然后在这一个等级内添加key和他访问次数的一个映射
		KS_NEXT.add(lFUKey);
		this.V_KS.put(lFUKey.count, KS_NEXT);
		this.K_V.put(key, lFUKey.count);
		//必须的放进去
		log.debug("访问频率最高的次数为："+lFUKey.count+",and the key is:"+lFUKey.key);
		if(!this.V_S.contains(lFUKey.count))
			V_S.add(lFUKey.count);
		//一次处理完成
	}

	/**
	 * 缓存容器中remove时，促发该动作，移除该key，重新保持最新的次序
	 */
	@Override
	public void itemRemoved(Object key) {
		
		if(!this.K_V.containsKey(key))
			return ;
			
		Integer count = K_V.get(key);
		
		//获取这个访问次次数的一批key
		ConcurrentLinkedQueue KS =  this.V_KS.get(count);
		
		//这个时候存在再次放入，则提高一次级别，的从原来的级别中移除
		if(KS.contains(key)){
			LFUKey removeKey = (LFUKey) KS.poll();
			log.debug("the remove key is"+removeKey.key+" and the access count is:"+removeKey.count);
		}
		
		this.K_V.remove(key);//移除这个key所对应的访问次数
	}

	/**
	 * 一级缓存和二级缓存兑换时，自动替换一个缓存实体,这里是核心
	 * 
	 * 有个一次级别全部扫描的关系在里面，对相同的次数又该如何处理
	 * ,这里一定要确保移除掉，不要就会影响命中率
	 * 
	 */
	@Override
	public Object removeItem() {
		Object key = null ;
		LFUKey lfuKey;
		try {
			lfuKey = null;
			//从低级别的开始扫描,也即排序迭代
			for(Integer count : this.V_S){
			
				ConcurrentLinkedQueue KS =  this.V_KS.get(count);
				if(KS.isEmpty())//这个时候表明这个访问层已经清空，则跳到下一层
					continue;
				//移除这一类级别中的任何一个数，因为这些的访问次数是相同的
				else{
					//并将在HashMap中的已移除的
					lfuKey = (LFUKey) KS.poll();
					key = lfuKey.key;
					
					if(this.K_V!=null)
						this.K_V.remove(lfuKey.key);
					
log.debug(key+"<-----iter.next(),and ------>the lfuKey key is:"+lfuKey+", and the seldom of count is:"+lfuKey);		
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//如果正常，则应该替换村次数最少的
		
		return key;
	}

	
	private final class LFUKey{
		Object key;
		Integer count ;
		
		public LFUKey(Object key,Integer count){
			this.key = key ;
			this.count = count;
		}
	}
}
