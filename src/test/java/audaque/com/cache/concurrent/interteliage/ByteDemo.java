package audaque.com.cache.concurrent.interteliage;

public class ByteDemo {

	private static byte[] bytes = null ;
	public static void main(String[] args) {
		
		bytes = new byte[1024];
		
		bytes[0] = 0x7e;
		//定义前一个字节为标识TCP/UDP
		
		int t = bytes[0] & 0xFF;
		
		System.out.println(Integer.toHexString(t));
	}
}
