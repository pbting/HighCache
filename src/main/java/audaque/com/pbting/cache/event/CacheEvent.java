package audaque.com.pbting.cache.event;

import java.util.EventObject;

/**
 * @author pbting
 *
 */
public class CacheEvent extends EventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//�ù��캯�� ����ָ�����¼���˭ʲô
	public CacheEvent(Object source) {
		super(source);
	}

}
