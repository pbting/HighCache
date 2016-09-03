package audaque.com.pbting.cache.house;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import audaque.com.pbting.cache.base.info.CacheEntry;
import audaque.com.pbting.cache.exception.FilePersistException;

public class DataMgr {

	private final static Log log = LogFactory.getLog(DataMgr.class);

	private static final Map<String, Map<String, PartionIndex>> TOPIC_META_DATA_INDEX = new ConcurrentHashMap<String, Map<String, PartionIndex>>();
	private static final Map<String, String> TOPIC_PATH = new ConcurrentHashMap<String, String>();

	private volatile static AtomicInteger changeCount = new AtomicInteger();

	private final static Charset charset = Charset.forName("UTF-8");
	/**
	 *
	 */
	private static final long DELETE_THREAD_SLEEP = 500;

	private static final int DELETE_COUNT = 60;

	private static final String FILE_EXTENION = "_index.index";

	private static java.util.Timer timer = new Timer("flush meta index", true);

	/**
	 * <pre>
	 * 初始化数据存储目录
	 * </pre>
	 *
	 * @param path
	 * @return
	 */
	public static boolean init(String topic, String path) {
		File file = new File(path);
		checkFile(file);
		TOPIC_PATH.put(topic, path);
		TOPIC_META_DATA_INDEX.put(topic, loadMetaIndex(topic, path));
		return true;
	}

	private static Map<String, PartionIndex> loadMetaIndex(String topic, String path) {
		File file = new File(path, topic + FILE_EXTENION);
		if(file.exists()){
			try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				if (!StringUtils.isEmpty(sb.toString())) {
					return new Gson().fromJson(sb.toString(), new TypeToken<HashMap<String, PartionIndex>>() {}.getType());
				}
			} catch (IOException e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		return new HashMap<String, PartionIndex>();
	}

	public static void registerTask() {
		long ONE_MIN = 1000 * 60 * 1;
		timer.schedule(new FlushDataMetaIndexTask(), ONE_MIN * 2, ONE_MIN * 2);
	}

	public static void flushMetaIndex() {
		if (changeCount.get() > 0) {
			for (Entry<String, String> entry : TOPIC_PATH.entrySet()) {
				String topic = entry.getKey();
				String path = entry.getValue();
				Map<String, PartionIndex> partionIndexs = TOPIC_META_DATA_INDEX.get(topic);
				File file = new File(path, topic + FILE_EXTENION);
				checkFile(file);
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
					writer.write(new Gson().toJson(partionIndexs));
				} catch (IOException e) {
					log.error("", e);
				}
			}
			changeCount.set(0);
		}
	}

	public static boolean store(String topic, CacheEntry cacheEntry, String filePath, boolean isZip) {
		if (cacheEntry.getKey() == null ||cacheEntry.getContent() == null || filePath.isEmpty()) {
			return false;
		}
		File file = new File(filePath);
		checkFile(file);
		PartionIndex partionIndex = new PartionIndex();
		long startIndex = file.length();
		partionIndex.setStartIndex(startIndex);
		try {
			FileOutputStream fileOut = new FileOutputStream(file, true);
			DataOutputStream writer = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			if (isZip) {
				DeflaterOutputStream defZip = new DeflaterOutputStream(bos) {
					{
						this.def.setLevel(Deflater.BEST_COMPRESSION);
					}
				};
				writer = new DataOutputStream(defZip);
			} else {
				writer = new DataOutputStream(fileOut);
			}
			// 先加密后压缩存储
			String inputString = DESUtil.encrypt(new Gson().toJson(cacheEntry));
			int tempLenth = inputString.getBytes("UTF-8").length;

			ByteBuffer bb = ByteBuffer.allocate(tempLenth);
			bb.put(inputString.getBytes("UTF-8"));
			bb.flip();

			writer.write(bb.array());
			writer.flush();
			writer.close();
			// 做测试
			if (isZip) {
				// 文件头使用int存放内容大小
				ByteBuffer head = ByteBuffer.allocate(TempProtocol.BYTE_TEMP_BODY);
				byte[] compress = bos.toByteArray();// 这里进行二次压缩
				head.putInt(compress.length);// 压缩后的内容长度
				fileOut.write(head.array());// 压缩后的内容长度 写入文件
				fileOut.write(compress);// 压缩后的内容写入文件
				fileOut.flush();
				fileOut.close();
			}
			partionIndex.setSize(new Long(file.length() - startIndex));

			System.err.println("文件增加的大小：" + partionIndex.getSize());

			TOPIC_META_DATA_INDEX.get(topic).put(MD5Security.compute(cacheEntry.getKey().toString()), partionIndex);
			changeCount.incrementAndGet();
			
			return true;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}

	public static void store(File file, Object value) throws FilePersistException {

		checkFile(file);

		try {
			// 每次添加的时候都会将之前的值给清空，
			FileOutputStream fis = new FileOutputStream(file);

			try {
				ObjectOutputStream oos = new ObjectOutputStream(fis);

				try {
					oos.writeObject(value);
					oos.flush();//
				} finally {
					try {
						oos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} finally {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			int count = DELETE_COUNT;
			//
			while (file.exists() && !file.delete() && count != 0) {
				count--;

				try {
					Thread.sleep(DELETE_THREAD_SLEEP);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			if (file.exists() && count == 0) {
				throw new FilePersistException("Unable to write '" + file + "' in the cache. Exception: " + e.getClass().getName() + ", Message: " + e.getMessage());
			}
		}
	}

	private static void checkFile(File file) {
		try {
			if (!file.exists()) {
				File parentFile = new File(file.getParent());

				if (!parentFile.exists()) {
					parentFile.mkdirs();//
				}

				if (file.isDirectory()) {
					file.mkdir();
				} else if (file.isFile()) {
					file.createNewFile();
				}
				log.info("mkdirs by store method ,the absoulute path is:" + parentFile.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(DataMgr.class.getName() + "创建文件错误" + e);
		}
	}

	@SuppressWarnings("resource")
	public static CacheEntry retrieve(String topic, String fileName, String key) {
		try {
			if (fileName == null || fileName.length() == 0)
				return null;
			
			if(!new File(fileName).exists())
				return null ;
			
			FileInputStream fileInputStream = new FileInputStream(fileName);
			FileChannel fileChannel = fileInputStream.getChannel();
			if(fileChannel.size() <=0)
				return null ;
			
			PartionIndex partionIndex = TOPIC_META_DATA_INDEX.get(topic).get(MD5Security.compute(key));
			
			if(partionIndex == null){
				return null ;
			}
			
			MappedByteBuffer mappedByteBuffer = fileChannel.map(MapMode.READ_ONLY, partionIndex.getStartIndex(), partionIndex.getSize());
			fileInputStream.close();
			fileChannel.close();
			String str = charset.decode(mappedByteBuffer).toString();
			mappedByteBuffer = null;
			System.out.println(str);
			str = DESUtil.decrypt(str);
			return new Gson().fromJson(str, new TypeToken<CacheEntry>() {}.getType());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object retrieve(File file) {

		Object readContent = null;
		boolean isExits = false;//

		try {
			isExits = file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isExits) {//
			ObjectInputStream ois = null;

			try {
				ois = new ObjectInputStream(new FileInputStream(file));

				try {
					//
					readContent = ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return readContent;
	}

	public static byte[] compress(byte[] data) {
		byte[] output = new byte[0];
		Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);) {
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = null;
		}
		compresser.end();
		return output;
	}

	/**
	 * 解压缩
	 * 
	 * @param data 待压缩的数据
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		decompresser.end();
		return output;
	}

	public static void main(String[] args) {
		String data = "C52F74F495E9E09CB083887214BB690593652C75B3C046D2B183208E5D671D61B8888771F875AF99DC53EFF2DFE1DC3B70D69D2F4C583FD86A86AA7FB3070BCA780FE486D7E736CD64698F69030B244D2A6E451AE73D53B2";
		System.out.println("前：" + data.length());
		byte[] src = compress(data.getBytes());
		System.out.println("后：" + src.length);
		String af = new String(decompress(src));
		System.out.println("解压：" + af);
	}

}