package audaque.com.pbting.cache.house;
/**
 * 
 * <pre>
 * 	生成模板文件协议
 * </pre>
 */
public interface TempProtocol {
	public static final Short BYTE_TEMP_NAME = 1 ;//模板名称占用 1 个字节存储
	
	public static final Short BYTE_TEMP_BODY = 4 ;//模板内容的长度占用4个字节存储
	
	public static final Short BYTE_BODY = 4 ;//压缩后的内容长度占用4个字节存储
	
	public static final char COLUMN_SEPRATOR = 0x01 ;//列与列之间的分隔符
	
	public static final char ROW_SEPRATOR = 0x02 ;//行与行之间的分隔符
	
	public static final char TEMP_SEPRATOR = '\n';//每个模板之间的分隔符
	
	public static final Short BYTE_TEMP_SEPRATOR = 1;//每个模板之间的分隔符
}
